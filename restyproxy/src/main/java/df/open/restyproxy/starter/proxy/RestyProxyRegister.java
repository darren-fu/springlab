package df.open.restyproxy.starter.proxy;

import df.open.restyproxy.starter.EnableRestyProxy;
import df.open.restyproxy.annotation.RestyService;
import df.open.restyproxy.command.RestyCommandContext;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 说明:
 * <p/>
 * Copyright: Copyright (c)
 * <p/>
 * Company:
 * <p/>
 *
 * @author darren -fu
 * @version 1.0.0
 * @contact 13914793391
 * @date 2016 /11/22
 */
public class RestyProxyRegister implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, BeanClassLoaderAware {

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        try {
            RestyCommandContext commandContext = RestyCommandContext.getInstance();
            Map<String, Object> attrs = importingClassMetadata.getAnnotationAttributes(EnableRestyProxy.class.getName());

            // TODO 处理类注解
            System.out.println(attrs.get("value"));

            // bean搜索器
            ClassPathScanningCandidateComponentProvider scanner = getScanner();
            Set<String> basePackages = getBasePackages(importingClassMetadata);

            for (String basePackage : basePackages) {
                // 搜索符合条件的bean
                Set<BeanDefinition> components = scanner.findCandidateComponents(basePackage);

                for (BeanDefinition component : components) {
                    if (component instanceof ScannedGenericBeanDefinition) {

                        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RestyProxyBeanFactory.class);
                        beanDefinitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                        Class beanClz = Class.forName(component.getBeanClassName());
                        // 分析类元数据，并存储到RestyCommandContext中
                        commandContext.initContextForService(beanClz);

                        beanDefinitionBuilder.addPropertyValue("type", beanClz);
                        beanDefinitionBuilder.addPropertyValue("restyCommandContext", commandContext);

                        // 注册bean
                        registry.registerBeanDefinition(component.getBeanClassName(), beanDefinitionBuilder.getBeanDefinition());
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets scanner.
     *
     * @return the scanner
     */
    protected ClassPathScanningCandidateComponentProvider getScanner() {
        ClassPathScanningCandidateComponentProvider scan = new CustomClassPathScanningCandidateComponentProvider(false);
        scan.addIncludeFilter((metadataReader, metadataReaderFactory) -> metadataReader.getClassMetadata().isInterface()
                && metadataReader.getAnnotationMetadata().hasAnnotation(RestyService.class.getName()));
        return scan;
    }


    /**
     * Gets base packages.
     *
     * @param importingClassMetadata the importing class metadata
     * @return the base packages
     */
    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableRestyProxy.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        if (StringUtils.hasText((String) attributes.get("value"))) {
            basePackages.add((String) attributes.get("value"));
        }

        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }


    private static class CustomClassPathScanningCandidateComponentProvider extends ClassPathScanningCandidateComponentProvider {

        /**
         * Instantiates a new Custom class path scanning candidate component provider.
         *
         * @param useDefaultFilters the use default filters
         */
        CustomClassPathScanningCandidateComponentProvider(boolean useDefaultFilters) {
            super(useDefaultFilters);
        }

        @Override
        protected boolean isCandidateComponent(
                AnnotatedBeanDefinition beanDefinition) {
            return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
        }
    }

}
