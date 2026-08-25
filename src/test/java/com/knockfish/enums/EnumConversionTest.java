package com.knockfish.enums;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knockfish.handler.GenericBaseEnumHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 枚举全链路转换测试：Jackson 序列化/反序列化 + TypeHandler toCode/fromCode（反射）。
 * 不依赖数据库，纯单元测试验证小写 code 转换链路。
 */
class EnumConversionTest {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 通过反射调用 GenericBaseEnumHandler 的 protected toCode/fromCode，
     * 验证 TypeHandler 层转换逻辑（与 MyBatis 实际调用路径一致）。
     */
    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> void assertTypeHandler(Class<E> enumClass) throws Exception {
        GenericBaseEnumHandler<E> handler = new GenericBaseEnumHandler<>(enumClass);
        Method toCode = GenericBaseEnumHandler.class.getDeclaredMethod("toCode", Enum.class);
        Method fromCode = GenericBaseEnumHandler.class.getDeclaredMethod("fromCode", String.class);
        toCode.setAccessible(true);
        fromCode.setAccessible(true);

        for (E e : enumClass.getEnumConstants()) {
            BaseEnum be = (BaseEnum) e;
            // toCode → getCode() → 小写
            String code = (String) toCode.invoke(handler, e);
            assertEquals(be.getCode(), code, "toCode 应等于 getCode: " + e);
            // fromCode → 枚举还原
            E result = (E) fromCode.invoke(handler, code);
            assertEquals(e, result, "fromCode 应还原原枚举: " + e);
        }
    }

    // ─── ArticleStatus ───

    @Test
    @DisplayName("ArticleStatus: Jackson 序列化输出小写 code")
    void articleStatus_serialize() throws Exception {
        assertEquals("\"publish\"", mapper.writeValueAsString(ArticleStatus.PUBLISH));
        assertEquals("\"draft\"", mapper.writeValueAsString(ArticleStatus.DRAFT));
    }

    @Test
    @DisplayName("ArticleStatus: Jackson 反序列化接受小写")
    void articleStatus_deserialize() throws Exception {
        assertEquals(ArticleStatus.PUBLISH, mapper.readValue("\"publish\"", ArticleStatus.class));
        assertEquals(ArticleStatus.DRAFT, mapper.readValue("\"draft\"", ArticleStatus.class));
    }

    @Test
    @DisplayName("ArticleStatus: TypeHandler toCode/fromCode 小写往返")
    void articleStatus_typeHandler() throws Exception {
        assertTypeHandler(ArticleStatus.class);
    }

    @Test
    @DisplayName("ArticleStatus: fromCode 大写兼容")
    void articleStatus_fromCode_uppercase() {
        assertEquals(ArticleStatus.PUBLISH, ArticleStatus.fromCode("PUBLISH"));
        assertEquals(ArticleStatus.DRAFT, ArticleStatus.fromCode("DRAFT"));
    }

    @Test
    @DisplayName("ArticleStatus: getCode 返回小写")
    void articleStatus_getCode() {
        assertEquals("publish", ArticleStatus.PUBLISH.getCode());
        assertEquals("draft", ArticleStatus.DRAFT.getCode());
    }

    // ─── LinkStatus ───

    @Test
    @DisplayName("LinkStatus: 全链路小写")
    void linkStatus_fullChain() throws Exception {
        assertEquals("\"display\"", mapper.writeValueAsString(LinkStatus.DISPLAY));
        assertEquals(LinkStatus.HIDE, mapper.readValue("\"hide\"", LinkStatus.class));
        assertEquals("hide", LinkStatus.HIDE.getCode());
        assertTypeHandler(LinkStatus.class);
    }

    // ─── PermissionStatus ───

    @Test
    @DisplayName("PermissionStatus: 全链路小写")
    void permissionStatus_fullChain() throws Exception {
        assertEquals("\"enable\"", mapper.writeValueAsString(PermissionStatus.ENABLE));
        assertEquals(PermissionStatus.DISABLE, mapper.readValue("\"disable\"", PermissionStatus.class));
        assertEquals("enable", PermissionStatus.ENABLE.getCode());
        assertTypeHandler(PermissionStatus.class);
    }

    // ─── PermissionType ───

    @Test
    @DisplayName("PermissionType: 全链路小写（4 个值）")
    void permissionType_fullChain() throws Exception {
        for (PermissionType t : PermissionType.values()) {
            String json = mapper.writeValueAsString(t);
            assertEquals("\"" + t.getCode() + "\"", json, "序列化应为小写: " + t);
            assertEquals(t, mapper.readValue(json, PermissionType.class));
        }
        assertTypeHandler(PermissionType.class);
    }

    // ─── TaskStatus ───

    @Test
    @DisplayName("TaskStatus: 全链路小写（5 个值）")
    void taskStatus_fullChain() throws Exception {
        for (TaskStatus s : TaskStatus.values()) {
            String json = mapper.writeValueAsString(s);
            assertEquals("\"" + s.getCode() + "\"", json, "序列化应为小写: " + s);
            assertEquals(s, mapper.readValue(json, TaskStatus.class));
        }
        assertTypeHandler(TaskStatus.class);
    }

    // ─── TaskType ───

    @Test
    @DisplayName("TaskType: 全链路小写（3 个值）")
    void taskType_fullChain() throws Exception {
        for (TaskType t : TaskType.values()) {
            String json = mapper.writeValueAsString(t);
            assertEquals("\"" + t.getCode() + "\"", json, "序列化应为小写: " + t);
            assertEquals(t, mapper.readValue(json, TaskType.class));
        }
        assertTypeHandler(TaskType.class);
    }

    // ─── 非法值 ───

    @Test
    @DisplayName("非法 code 抛 IllegalArgumentException")
    void invalidCode() {
        assertThrows(IllegalArgumentException.class, () -> ArticleStatus.fromCode("xxx"));
        assertThrows(IllegalArgumentException.class, () -> PermissionType.fromCode("unknown"));
    }

    // ─── 模拟新增/更新场景 ───

    @Test
    @DisplayName("模拟新增：DTO 反序列化 → getCode → 写入 DB")
    void simulate_create() throws Exception {
        // 前端传小写 JSON → Jackson 反序列化 → 枚举 → getCode() → DB 存的值
        ArticleStatus status = mapper.readValue("\"publish\"", ArticleStatus.class);
        String dbValue = status.getCode();
        assertEquals("publish", dbValue, "新增时 DB 应存小写 'publish'");
    }

    @Test
    @DisplayName("模拟更新：旧大写数据 → fromCode 兼容 → getCode → 新小写写入")
    void simulate_update_oldData() {
        // DB 旧数据可能是大写 'PUBLISH'，fromCode 用 equalsIgnoreCase 兼容
        ArticleStatus status = ArticleStatus.fromCode("PUBLISH");
        // 更新后重新写入，getCode 返回小写
        assertEquals("publish", status.getCode(), "更新后 DB 应存小写 'publish'");
    }

    @Test
    @DisplayName("模拟查询：DB 小写 → fromCode → Jackson 序列化 → 前端收到小写")
    void simulate_query() throws Exception {
        // DB 存小写 'draft' → TypeHandler fromCode → 枚举 → Jackson 序列化 → 前端 JSON
        ArticleStatus status = ArticleStatus.fromCode("draft");
        String json = mapper.writeValueAsString(status);
        assertEquals("\"draft\"", json, "查询返回前端应为小写 'draft'");
    }
}
