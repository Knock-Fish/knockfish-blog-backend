package com.knockfish.service.impl;

import com.knockfish.convert.GanttTaskConvert;
import com.knockfish.dto.gantt_task.GanttTaskCreateDTO;
import com.knockfish.dto.gantt_task.GanttTaskUpdateDTO;
import com.knockfish.entity.GanttTask;
import com.knockfish.enums.TaskStatus;
import com.knockfish.enums.TaskType;
import com.knockfish.repository.GanttLinkRepository;
import com.knockfish.repository.GanttTaskRepository;
import com.knockfish.security.CustomUserDetails;
import com.knockfish.service.GanttTaskService;
import com.knockfish.vo.gantt_task.GanttTaskVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GanttTaskServiceImpl implements GanttTaskService {

    private final GanttTaskRepository ganttTaskRepository;
    private final GanttLinkRepository ganttLinkRepository;
    private final GanttTaskConvert ganttTaskConvert;

    @Override
    public List<GanttTaskVO> getTaskTree() {
        Long userId = getCurrentUserId();
        // 先重算派生字段，保证返回数据一致
        recalculateDerivedFields(userId);
        List<GanttTask> taskList = ganttTaskRepository.selectByUserId(userId);
        List<GanttTaskVO> voList = ganttTaskConvert.toVOList(taskList);
        return buildTree(voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTask(GanttTaskCreateDTO createDTO) {
        Long userId = getCurrentUserId();
        GanttTask entity = ganttTaskConvert.createToEntity(createDTO);
        entity.setUserId(userId);
        entity.setCreateTime(LocalDateTime.now());

        Long parentId = entity.getParentId();
        Long insertAfterId = createDTO.getInsert_after_id();
        int sortOrder;

        if (insertAfterId != null) {
            // 插入到指定兄弟任务之后
            GanttTask afterTask = ganttTaskRepository.selectByTaskId(insertAfterId);
            if (afterTask != null) {
                int afterSort = afterTask.getSortOrder() == null ? 0 : afterTask.getSortOrder();
                sortOrder = afterSort + 1;
                // 将同级中 sortOrder >= sortOrder 的任务后移一位
                ganttTaskRepository.shiftSortOrder(userId, parentId, sortOrder);
            } else {
                // 指定的兄弟不存在，追加到末尾
                sortOrder = getNextSortOrder(userId, parentId);
            }
        } else {
            // 追加到同级末尾
            sortOrder = getNextSortOrder(userId, parentId);
        }

        entity.setSortOrder(sortOrder);
        if (entity.getOpen() == null) {
            entity.setOpen(1);
        }
        // 里程碑：progress 完全由 status 推导
        normalizeMilestoneProgress(entity);

        ganttTaskRepository.insert(entity);
        // 重算祖先 PROJECT 及里程碑进度
        recalculateDerivedFields(userId);
        return entity.getTaskId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTask(GanttTaskUpdateDTO updateDTO) {
        Long userId = getCurrentUserId();
        GanttTask existed = ganttTaskRepository.selectByTaskId(updateDTO.getTask_id());
        if (existed == null || !existed.getUserId().equals(userId)) {
            log.warn("更新任务失败: 任务不存在或不属于当前用户, taskId={}", updateDTO.getTask_id());
            return;
        }
        // 汇总任务（PROJECT）是只读派生项，禁止手动覆盖 start/end/progress/status
        if (TaskType.PROJECT.equals(existed.getType())) {
            updateDTO.setStart(null);
            updateDTO.setEnd(null);
            updateDTO.setProgress(null);
            updateDTO.setStatus(null);
            updateDTO.setType(null); // 同时禁止切换类型
        }
        // 里程碑：progress 完全由 status 推导（忽略用户传入的 progress）
        if (TaskType.MILESTONE.equals(existed.getType())) {
            if (updateDTO.getStatus() == null) {
                updateDTO.setProgress(null); // 使用数据库中的 status 推导
            } else {
                updateDTO.setProgress(TaskStatus.DONE.equals(updateDTO.getStatus()) ? 1.0 : 0.0);
            }
        }
        GanttTask entity = ganttTaskConvert.updateToEntity(updateDTO);
        entity.setUpdateTime(LocalDateTime.now());
        // 以数据库存在的 type 为准，避免 project 的 type 被改回去
        if (entity.getType() == null) {
            entity.setType(existed.getType());
        }
        // 里程碑再次校正（若上面 status==null 且用户传了 progress，则这里按 DB 的 status 二次保证）
        normalizeMilestoneProgress(entity, existed.getStatus());
        ganttTaskRepository.updateById(entity);
        // 重算派生字段（祖先 PROJECT + 所有 milestone progress）
        recalculateDerivedFields(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long taskId) {
        Long userId = getCurrentUserId();
        GanttTask task = ganttTaskRepository.selectByTaskId(taskId);
        if (task == null || !task.getUserId().equals(userId)) {
            log.warn("删除任务失败: 任务不存在或不属于当前用户, taskId={}", taskId);
            return;
        }
        // 递归收集所有子任务ID
        List<Long> allIds = collectTaskIdsRecursively(userId, taskId);
        // 删除关联的连线
        for (Long id : allIds) {
            ganttLinkRepository.deleteByTaskId(id);
        }
        // 删除所有子任务（DB层按parent_id删，再删自身）
        deleteRecursively(taskId);
        // 重算派生字段
        recalculateDerivedFields(userId);
    }

    // ==================== 私有方法 ====================

    /**
     * 构造任务树
     */
    private List<GanttTaskVO> buildTree(List<GanttTaskVO> voList) {
        Map<Long, GanttTaskVO> voMap = new HashMap<>();
        for (GanttTaskVO vo : voList) {
            vo.setChildren(new ArrayList<>());
            voMap.put(vo.getTask_id(), vo);
        }
        List<GanttTaskVO> roots = new ArrayList<>();
        for (GanttTaskVO vo : voList) {
            Long parentId = vo.getParent_id();
            if (parentId == null || !voMap.containsKey(parentId)) {
                roots.add(vo);
            } else {
                voMap.get(parentId).getChildren().add(vo);
            }
        }
        return roots;
    }

    /**
     * 计算同一父级下下一个 sortOrder（最大值+1，没有则为0）
     */
    private int getNextSortOrder(Long userId, Long parentId) {
        List<GanttTask> siblings = ganttTaskRepository.selectByUserId(userId).stream()
                .filter(t -> (parentId == null && t.getParentId() == null)
                        || (parentId != null && parentId.equals(t.getParentId())))
                .collect(Collectors.toList());
        if (siblings.isEmpty()) {
            return 0;
        }
        return siblings.stream()
                .map(GanttTask::getSortOrder)
                .filter(s -> s != null)
                .max(Integer::compareTo)
                .map(m -> m + 1)
                .orElse(0);
    }

    /**
     * 递归收集所有子任务ID（含自身）
     */
    private List<Long> collectTaskIdsRecursively(Long userId, Long rootId) {
        List<GanttTask> all = ganttTaskRepository.selectByUserId(userId);
        List<Long> result = new ArrayList<>();
        collectChildren(all, rootId, result);
        return result;
    }

    private void collectChildren(List<GanttTask> all, Long parentId, List<Long> result) {
        result.add(parentId);
        for (GanttTask t : all) {
            if (parentId.equals(t.getParentId())) {
                collectChildren(all, t.getTaskId(), result);
            }
        }
    }

    /**
     * 递归删除子任务（先删子，再删自身）
     */
    private void deleteRecursively(Long taskId) {
        ganttTaskRepository.deleteByParentId(taskId);
        ganttTaskRepository.deleteByTaskId(taskId);
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        }
        throw new IllegalStateException("用户未登录");
    }

    // ==================== 派生字段重算 ====================

    /**
     * 统一重算：
     * 1. 所有 MILESTONE 的 progress（status=DONE→1.0，其他→0.0）
     * 2. 所有 PROJECT 的 start / end / progress / status（自底向上，由子项聚合）
     * 若检测到字段变化则同步写入 DB，保证后续查询数据一致。
     */
    private void recalculateDerivedFields(Long userId) {
        List<GanttTask> all = ganttTaskRepository.selectByUserId(userId);
        if (all.isEmpty()) return;
        Map<Long, GanttTask> map = new HashMap<>();
        for (GanttTask t : all) map.put(t.getTaskId(), t);

        // 1) 里程碑：progress 完全由 status 决定
        boolean changed = false;
        for (GanttTask t : all) {
            if (TaskType.MILESTONE.equals(t.getType())) {
                double expected = TaskStatus.DONE.equals(t.getStatus()) ? 1.0 : 0.0;
                if (Math.abs(t.getProgress() - expected) > 1e-9) {
                    t.setProgress(expected);
                    changed = true;
                }
            }
        }

        // 2) 汇总任务：自底向上聚合子项（先子 project，再父 project）
        List<GanttTask> projects = all.stream()
                .filter(t -> TaskType.PROJECT.equals(t.getType()))
                .collect(Collectors.toList());
        // 按任务树深度排序（叶子深，先处理；根浅，后处理）。找不到子即深=0。
        Map<Long, Integer> depth = new HashMap<>();
        for (GanttTask p : projects) depth.put(p.getTaskId(), depthOf(p, map, depth, new HashSet<>()));
        projects.sort((a, b) -> Integer.compare(depth.get(b.getTaskId()), depth.get(a.getTaskId())));

        Map<Long, List<GanttTask>> childrenByParent = new HashMap<>();
        for (GanttTask t : all) {
            Long pid = t.getParentId();
            if (pid != null) {
                childrenByParent.computeIfAbsent(pid, k -> new ArrayList<>()).add(t);
            }
        }

        for (GanttTask project : projects) {
            List<GanttTask> children = childrenByParent.getOrDefault(project.getTaskId(), new ArrayList<>());
            GanttTask aggregated = aggregateProject(project, children);
            if (aggregated != null) changed = true;
        }

        // 3) 持久化变化
        if (changed) {
            LocalDateTime now = LocalDateTime.now();
            for (GanttTask t : all) {
                // 仅对 type=milestone/project 的派生字段变化做保存
                if (!TaskType.TASK.equals(t.getType())) {
                    t.setUpdateTime(now);
                    ganttTaskRepository.updateById(t);
                }
            }
        }
    }

    /**
     * 计算节点在 PROJECT 子树中的深度（非 PROJECT 的不计深度，返回 -1）
     */
    private int depthOf(GanttTask project, Map<Long, GanttTask> map,
                        Map<Long, Integer> memo, Set<Long> visiting) {
        if (memo.containsKey(project.getTaskId())) return memo.get(project.getTaskId());
        if (!visiting.add(project.getTaskId())) return 0; // 防环
        Long pid = project.getParentId();
        int parentDepth = -1;
        if (pid != null) {
            GanttTask parent = map.get(pid);
            if (parent != null && TaskType.PROJECT.equals(parent.getType())) {
                parentDepth = depthOf(parent, map, memo, visiting);
            }
        }
        int d = parentDepth + 1;
        memo.put(project.getTaskId(), d);
        return d;
    }

    /**
     * 根据直属子项聚合 PROJECT 的派生字段：
     *  - start = min(children.start)  若无子项则保持不变
     *  - end   = max(children.end)    若无子项则保持不变
     *  - progress = Σ(子项 progress * 子项 duration) / Σ 子项 duration   无子项=0
     *  - status：全部 done → done；存在 delay → delay；存在 doing 或 0<progress<1 → doing；其他 todo
     *            （cancel 不自动继承）
     * 对传入 project 对象就地修改；若有变化返回 project，否则返回 null。
     */
    private GanttTask aggregateProject(GanttTask project, List<GanttTask> children) {
        if (children.isEmpty()) {
            // 无子项：进度=0，状态=todo（保持 start/end 不变）
            boolean dirty = false;
            if (Math.abs(project.getProgress() - 0.0) > 1e-9) {
                project.setProgress(0.0);
                dirty = true;
            }
            if (!TaskStatus.TODO.equals(project.getStatus())) {
                project.setStatus(TaskStatus.TODO);
                dirty = true;
            }
            return dirty ? project : null;
        }

        LocalDateTime minStart = null;
        LocalDateTime maxEnd = null;
        double weightedSum = 0.0;
        double totalDuration = 0.0;
        boolean anyDelay = false;
        boolean anyDoing = false;
        boolean anyPartial = false; // 是否存在 0 < progress < 1
        boolean allDone = true;

        for (GanttTask c : children) {
            if (c.getStart() != null && (minStart == null || c.getStart().isBefore(minStart))) {
                minStart = c.getStart();
            }
            if (c.getEnd() != null && (maxEnd == null || c.getEnd().isAfter(maxEnd))) {
                maxEnd = c.getEnd();
            }
            double d = durationMinutes(c.getStart(), c.getEnd());
            if (d > 0) {
                weightedSum += c.getProgress() * d;
                totalDuration += d;
            }
            if (TaskStatus.DELAY.equals(c.getStatus())) anyDelay = true;
            if (TaskStatus.DOING.equals(c.getStatus())) anyDoing = true;
            if (!TaskStatus.DONE.equals(c.getStatus())) allDone = false;
            double p = c.getProgress();
            if (p > 1e-9 && p < 1.0 - 1e-9) anyPartial = true;
        }

        double newProgress = totalDuration > 0 ? weightedSum / totalDuration : 0.0;
        TaskStatus newStatus;
        if (allDone) newStatus = TaskStatus.DONE;
        else if (anyDelay) newStatus = TaskStatus.DELAY;
        else if (anyDoing || anyPartial) newStatus = TaskStatus.DOING;
        else newStatus = TaskStatus.TODO;

        boolean dirty = false;
        if (minStart != null && !minStart.equals(project.getStart())) {
            project.setStart(minStart);
            dirty = true;
        }
        if (maxEnd != null && !maxEnd.equals(project.getEnd())) {
            project.setEnd(maxEnd);
            dirty = true;
        }
        if (Math.abs(project.getProgress() - newProgress) > 1e-9) {
            project.setProgress(newProgress);
            dirty = true;
        }
        if (!newStatus.equals(project.getStatus())) {
            project.setStatus(newStatus);
            dirty = true;
        }
        return dirty ? project : null;
    }

    private double durationMinutes(LocalDateTime s, LocalDateTime e) {
        if (s == null || e == null) return 0.0;
        return Math.max(0.0, Duration.between(s, e).toMinutes());
    }

    /**
     * 里程碑：progress 完全由 status 推导（DONE→1.0，其他→0.0）
     */
    private void normalizeMilestoneProgress(GanttTask task) {
        if (task == null || !TaskType.MILESTONE.equals(task.getType())) return;
        task.setProgress(TaskStatus.DONE.equals(task.getStatus()) ? 1.0 : 0.0);
    }

    private void normalizeMilestoneProgress(GanttTask task, TaskStatus fallbackStatus) {
        if (task == null || !TaskType.MILESTONE.equals(task.getType())) return;
        TaskStatus s = task.getStatus() != null ? task.getStatus() : fallbackStatus;
        task.setProgress(TaskStatus.DONE.equals(s) ? 1.0 : 0.0);
        if (task.getStatus() == null && fallbackStatus != null) {
            task.setStatus(fallbackStatus);
        }
    }
}
