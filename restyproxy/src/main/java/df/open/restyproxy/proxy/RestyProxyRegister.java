package df.open.restyproxy.proxy;

import df.open.restyproxy.annotation.EnableRestyProxy;
import df.open.restyproxy.annotation.RestyMethod;
import df.open.restyproxy.annotation.RestyService;
import df.open.restyproxy.base.MethodMetaDataContext;
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
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
 * @author darren-fu
 * @version 1.0.0
 * @contact 13914793391
 * @date 2016/11/22
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

            Map<String, Object> attrs = importingClassMetadata.getAnnotationAttributes(EnableRestyProxy.class.getName());

            // TODO 处理类注解
            System.out.println(attrs.get("value"));

            ////////////////////////////////////////////////////////
            ClassPathScanningCandidateComponentProvider scanner = getScanner();
//        scanner.addIncludeFilter();
            Set<String> basePackages = getBasePackages(importingClassMetadata);
            for (String basePackage : basePackages) {
                Set<BeanDefinition> components = scanner.findCandidateComponents(basePackage);
                for (BeanDefinition component : components) {
                    if (component instanceof ScannedGenericBeanDefinition) {
                        ScannedGenericBeanDefinition definition = (ScannedGenericBeanDefinition) component;

                        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RestyProxyBeanFactory.class);
                        beanDefinitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                        Class beanCLz = Class.forName(component.getBeanClassName());
                        beanDefinitionBuilder.addPropertyValue("type", beanCLz);


                        // 注册bean
                        registry.registerBeanDefinition(component.getBeanClassName(), beanDefinitionBuilder.getBeanDefinition());

                    }


                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        ClassPathScanningCandidateComponentProvider scan = new CustomClassPathScanningCandidateComponentProvider(false);
        scan.addIncludeFilter((metadataReader, metadataReaderFactory) -> metadataReader.getClassMetadata().isInterface()
                && metadataReader.getAnnotationMetadata().hasAnnotation(RestyService.class.getName()));
        return scan;
    }


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

        CustomClassPathScanningCandidateComponentProvider(boolean useDefaultFilters) {
            super(useDefaultFilters);
        }

        @Override
        protected boolean isCandidateComponent(
                AnnotatedBeanDefinition beanDefinition) {
            return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
        }
    }


    private void parserClassAndMethdoAnnotation(Class beanClz) {
        RestyService annotation = (RestyService) beanClz.getDeclaredAnnotation(RestyService.class);
        MethodMetaDataContext.storeRestyService(beanClz.getTypeName(), annotation);


        for (Method method : beanClz.getMethods()) {
            RestyMethod restyMethod = method.getDeclaredAnnotation(RestyMethod.class);
            if (restyMethod == null) {

            }
            MethodMetaDataContext.storeRestyMethod(method, restyMethod);


        }

    }
}
