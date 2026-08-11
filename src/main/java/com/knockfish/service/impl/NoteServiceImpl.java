package com.knockfish.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.knockfish.convert.NoteConvert;
import com.knockfish.dto.file_reference.FileReferenceQueryByRefDTO;
import com.knockfish.dto.note.NoteCreateDTO;
import com.knockfish.dto.note.NoteQueryDTO;
import com.knockfish.dto.note.NoteUpdateDTO;
import com.knockfish.entity.Note;
import com.knockfish.repository.NoteRepository;
import com.knockfish.service.FileReferenceService;
import com.knockfish.service.NoteService;
import com.knockfish.service.R2FileService;
import com.knockfish.utils.UrlUtil;
import com.knockfish.vo.file_reference.FileReferenceVO;
import com.knockfish.vo.link.LinkVO;
import com.knockfish.vo.note.NoteDetailVO;
import com.knockfish.vo.note.NoteMenuVO;
import com.knockfish.vo.note.NoteVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final NoteConvert noteConvert;
    private final FileReferenceService fileReferenceService;
    private final R2FileService r2FileService;

    @Override
    public PageInfo<NoteVO> getNoteList(NoteQueryDTO query, Integer pageNum, Integer pageSize) {
        try(Page<NoteVO> page = PageHelper.startPage(pageNum, pageSize)){
            List<Note> noteListEntity = noteRepository.selectList(query);
            List<NoteVO> noteListVO = noteConvert.listToVOlist(noteListEntity);
            PageInfo<NoteVO> pageInfo = PageInfo.of(page);
            pageInfo.setList(noteListVO);
            return pageInfo;
        }
    }

    @Override
    public List<NoteMenuVO> getNoteMenuList() {
        List<Note> noteListEntity = noteRepository.selectMenuList();
        return noteConvert.listToVOMenulist(noteListEntity);
    }

    @Override
    public NoteDetailVO getNoteById(Long noteId) {
        Note note = noteRepository.selectById(noteId);
        return noteConvert.toDetailVO(note);
    }

    @Override
    public Long createNote(NoteCreateDTO createDTO) {
        Note entity = noteConvert.createToEntity(createDTO);
        entity.setCreateTime(LocalDateTime.now());
        noteRepository.insert(entity);
        return entity.getNoteId();
    }

    @Override
    public void updateNote(NoteUpdateDTO updateDTO) {
        Note entity = noteConvert.updateToEntity(updateDTO);
        noteRepository.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNote(Long noteId) {
        Note note = noteRepository.selectById(noteId);
        if (note == null) {
            log.warn("删除笔记失败: 笔记不存在, id={}", noteId);
            return;
        }

        // 提取内容图片
        List<String> keyList = new ArrayList<>(extractUsedFileKeys(note));

        // 调用文件服务批量删除图片
        if (!keyList.isEmpty()) {
            log.debug("删除笔记关联文件: noteId={}, fileCount={}", noteId, keyList.size());
            r2FileService.batchDeleteR2File(keyList);
        }

        // 删除file_reference记录
        FileReferenceQueryByRefDTO queryDTO = new FileReferenceQueryByRefDTO();
        queryDTO.setReferenceType("note");
        queryDTO.setReferenceId(noteId);
        fileReferenceService.deleteByReference(queryDTO);

        noteRepository.deleteById(noteId);
        log.info("笔记删除成功: noteId={}, title={}", noteId, note.getNoteTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindUnusedFiles(Long noteId) {
        if (noteId == null) {
            return;
        }
        Note note = noteRepository.selectById(noteId);
        if (note == null) {
            log.warn("解绑未使用图片失败: 笔记不存在, noteId={}", noteId);
            return;
        }

        // 1. 从 noteContent 提取实际使用的 file key 集合
        Set<String> usedKeys = extractUsedFileKeys(note);

        // 2. 查询 file_reference 表中绑定该笔记的所有记录
        FileReferenceQueryByRefDTO queryDTO = new FileReferenceQueryByRefDTO();
        queryDTO.setReferenceType("note");
        queryDTO.setReferenceId(noteId);
        List<FileReferenceVO> boundFiles = fileReferenceService.selectByReference(queryDTO);

        // 3. 差集：绑定记录中 file_path 不在 usedKeys 中的 → 解绑
        List<Long> unbindIds = boundFiles.stream()
                .filter(f -> StrUtil.isNotBlank(f.getFilePath()) && !usedKeys.contains(f.getFilePath()))
                .map(FileReferenceVO::getFileId)
                .collect(Collectors.toList());

        if (unbindIds.isEmpty()) {
            log.info("笔记无未使用图片需解绑: noteId={}", noteId);
            return;
        }

        fileReferenceService.unbindByIds(unbindIds);
        log.info("笔记未使用图片解绑完成: noteId={}, 解绑数量={}", noteId, unbindIds.size());
    }

    /**
     * 提取笔记实际使用的所有图片 file key（noteContent 中的 img src）
     * noteContent 为 HTML 文本，使用 Jsoup 解析
     */
    private Set<String> extractUsedFileKeys(Note note) {
        Set<String> usedKeys = new HashSet<>();
        if (StrUtil.isNotBlank(note.getNoteContent())) {
            Document doc = Jsoup.parse(note.getNoteContent());
            Elements images = doc.select("img");
            for (org.jsoup.nodes.Element img : images) {
                String src = img.attr("src");
                if (StrUtil.isNotBlank(src)) {
                    String key = UrlUtil.extractFileKeyFromUrl(src);
                    if (StrUtil.isNotBlank(key)) {
                        usedKeys.add(key);
                    }
                }
            }
        }
        return usedKeys;
    }
}