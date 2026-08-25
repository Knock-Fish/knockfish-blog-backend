package com.knockfish.config;

import com.knockfish.enums.BaseEnum;
import com.knockfish.handler.GenericBaseEnumHandler;
import jakarta.annotation.PostConstruct;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 启动时扫描 {@code com.knockfish.enums} 包下所有实现了 {@link BaseEnum} 的枚举类，
 * 自动创建 {@link GenericBaseEnumHandler} 并注册到 MyBatis，
 * 做到"新增枚举自动映射，无需额外配置"。
 * <p>
 * 约定：新增业务枚举只需两步
 * <ol>
 *   <li>放在 {@code com.knockfish.enums} 包（或其子包）下；</li>
 *   <li>实现 {@link BaseEnum} 并提供静态方法 {@code static E fromCode(String code)}。</li>
 * </ol>
 */
@Component
public class MybatisEnumConfigurer {

    private static final Logger log = LoggerFactory.getLogger(MybatisEnumConfigurer.class);

    private static final String ENUMS_BASE_PACKAGE = "com.knockfish.enums";

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @PostConstruct
    public void registerEnumHandlers() {
        Set<Class<?>> enumClasses = scanBaseEnumClasses();
        if (enumClasses.isEmpty()) {
            log.warn("[MybatisEnumConfigurer] 未扫描到任何实现 BaseEnum 的枚举类，请检查包路径：{}", ENUMS_BASE_PACKAGE);
            return;
        }
        for (Class<?> clazz : enumClasses) {
            if (!clazz.isEnum()) {
                continue;
            }
            Class<? extends Enum> enumClazz = (Class<? extends Enum>) clazz;
            GenericBaseEnumHandler handler = new GenericBaseEnumHandler(enumClazz);
            sqlSessionFactory.getConfiguration().getTypeHandlerRegistry().register(enumClazz, handler);
            log.info("[MybatisEnumConfigurer] 注册枚举TypeHandler：{} -> {}",
                    enumClazz.getName(), GenericBaseEnumHandler.class.getSimpleName());
        }
    }

    private Set<Class<?>> scanBaseEnumClasses() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(BaseEnum.class));

        Set<Class<?>> result = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (var bd : scanner.findCandidateComponents(ENUMS_BASE_PACKAGE)) {
            try {
                Class<?> clazz = ClassUtils.forName(bd.getBeanClassName(), classLoader);
                result.add(clazz);
            } catch (ClassNotFoundException ex) {
                log.warn("[MybatisEnumConfigurer] 无法加载类：{}", bd.getBeanClassName(), ex);
            }
        }
        return result;
    }
}
