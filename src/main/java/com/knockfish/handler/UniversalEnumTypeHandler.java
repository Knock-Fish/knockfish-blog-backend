package com.knockfish.handler;

import com.knockfish.enums.BaseEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 全局默认枚举 TypeHandler（mybatis.default-enum-type-handler）。
 * <p>
 * 处理策略：
 * <ul>
 *   <li>如果枚举实现了 {@link BaseEnum}：使用 {@code getCode()} 写库，
 *       使用静态 {@code fromCode(String)} 反序列化（存储为 code，通常小写）</li>
 *   <li>普通枚举：回退到 MyBatis 默认行为 {@code enum.name()} / {@code Enum.valueOf}</li>
 * </ul>
 * <p>
 * 解决 MyBatis 自带 {@code EnumTypeHandler} 始终写枚举常量名（大写）的问题，
 * 让实现了 BaseEnum 的枚举字段（如 GanttTask.type / status）入库时统一走 code 小写。
 */
public class UniversalEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private final Class<E> enumClass;
    private final boolean isBaseEnum;
    private final Method fromCodeMethod;

    @SuppressWarnings("unchecked")
    public UniversalEnumTypeHandler(Class<E> enumClass) {
        if (enumClass == null) {
            throw new IllegalArgumentException("enumClass 不能为空");
        }
        this.enumClass = enumClass;
        this.isBaseEnum = BaseEnum.class.isAssignableFrom(enumClass);
        if (this.isBaseEnum) {
            try {
                Method m = enumClass.getDeclaredMethod("fromCode", String.class);
                m.setAccessible(true);
                this.fromCodeMethod = m;
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(enumClass.getName()
                        + " 实现了 BaseEnum 但缺少静态 fromCode(String) 方法", e);
            }
        } else {
            this.fromCodeMethod = null;
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType)
            throws SQLException {
        String value = isBaseEnum
                ? ((BaseEnum) parameter).getCode()
                : parameter.name();
        ps.setString(i, value);
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return stringToEnum(rs.getString(columnName));
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return stringToEnum(rs.getString(columnIndex));
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return stringToEnum(cs.getString(columnIndex));
    }

    @SuppressWarnings("unchecked")
    private E stringToEnum(String raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        if (isBaseEnum) {
            try {
                E result = (E) fromCodeMethod.invoke(null, raw);
                if (result == null) {
                    throw new IllegalArgumentException("枚举 fromCode 返回 null, code=" + raw);
                }
                return result;
            } catch (ReflectiveOperationException e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
                throw new IllegalStateException("枚举 fromCode 调用失败，code=" + raw, cause);
            }
        }
        // 普通枚举走 Enum.valueOf
        return Enum.valueOf(enumClass, raw);
    }
}
