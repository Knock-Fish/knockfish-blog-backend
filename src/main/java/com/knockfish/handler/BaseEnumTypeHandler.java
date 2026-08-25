package com.knockfish.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.*;

/**
 * 枚举 TypeHandler 抽象基类
 * 子类只需实现 code ↔ 枚举 的两个转换方法
 */
public abstract class BaseEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private final Class<E> type;

    protected BaseEnumTypeHandler(Class<E> type) {
        this.type = type;
    }

    /** 子类实现：枚举 → 数据库存储字符串（如 TaskType.TASK → "task"） */
    protected abstract String toCode(E enumValue);

    /** 子类实现：数据库字符串 → 枚举（如 "task" → TaskType.TASK） */
    protected abstract E fromCode(String code);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, toCode(parameter));
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

    private E stringToEnum(String code) {
        if (code == null || code.isEmpty()) return null;
        E result = fromCode(code);
        if (result == null) {
            throw new IllegalArgumentException("数据库存在非法枚举值: " + code);
        }
        return result;
    }
}