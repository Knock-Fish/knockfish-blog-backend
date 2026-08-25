package com.knockfish.handler;

import com.knockfish.enums.BaseEnum;

import java.lang.reflect.Method;

/**
 * 通用枚举 TypeHandler：基于 {@link BaseEnumTypeHandler}，
 * 通过反射适配任意实现了 {@link BaseEnum} 且提供了静态
 * {@code fromCode(String)} 工厂方法的枚举类。
 * <p>
 * 与 {@code MybatisEnumConfigurer} 配合，启动时为每个 BaseEnum
 * 枚举自动创建本实例并注册到 MyBatis，无需手写具体子类。
 */
public class GenericBaseEnumHandler<E extends Enum<E>> extends BaseEnumTypeHandler<E> {

    private final Method fromCodeMethod;

    @SuppressWarnings("unchecked")
    public GenericBaseEnumHandler(Class<E> enumClass) {
        super(enumClass);
        if (!BaseEnum.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException(enumClass.getName() + " 未实现 BaseEnum 接口");
        }
        try {
            // 约定：实现 BaseEnum 的枚举必须提供静态 fromCode(String) 方法
            // （TaskStatus/TaskType 使用 fromCode；老枚举也统一成 fromCode）
            this.fromCodeMethod = enumClass.getDeclaredMethod("fromCode", String.class);
            this.fromCodeMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(enumClass.getName()
                    + " 未提供静态方法 public static " + enumClass.getSimpleName()
                    + " fromCode(String code)，无法自动注册 TypeHandler", e);
        }
    }

    @Override
    protected String toCode(E enumValue) {
        return ((BaseEnum) enumValue).getCode();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected E fromCode(String code) {
        try {
            return (E) fromCodeMethod.invoke(null, code);
        } catch (ReflectiveOperationException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new IllegalStateException("枚举 fromCode 调用失败，code=" + code, cause);
        }
    }
}
