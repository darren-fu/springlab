package df.open.spring.proxy;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.reflect.Proxy.newProxyInstance;
import static javafx.scene.input.KeyCode.T;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

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
public class InterfaceProxyRegister implements ImportBeanDefinitionRegistrar,
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
            System.out.println("registerBeanDefinitions");

            Map<String, Object> attrs = importingClassMetadata.getAnnotationAttributes(EnableInterfaceProxy.class.getName());

            System.out.println(attrs.get("value"));

            BeanDefinitionBuilder builder =
                    genericBeanDefinition(InterfaceProxyConfig.class);
            builder.addConstructorArgValue("proxy.name");

            registry.registerBeanDefinition("proxy.config", builder.getBeanDefinition());

            ////////////////////////////////////////////////////////
            ClassPathScanningCandidateComponentProvider scanner = getScanner();
//        scanner.addIncludeFilter();
            Set<String> basePackages = getBasePackages(importingClassMetadata);
            scanner.addIncludeFilter(new TypeFilter() {
                @Override
                public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                    return metadataReader.getClassMetadata().isInterface() && !metadataReader.getClassMetadata().isAnnotation();
                }
            });

            for (String basePackage : basePackages) {
                Set<BeanDefinition> components = scanner.findCandidateComponents(basePackage);
                for (BeanDefinition component : components) {


                    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(InterfaceBeanFactory.class);
                    beanDefinitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                    beanDefinitionBuilder.addPropertyValue("type", Class.forName(component.getBeanClassName()));

                    registry.registerBeanDefinition(component.getBeanClassName(), beanDefinitionBuilder.getBeanDefinition());

                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
            }
        };
    }


    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableInterfaceProxy.class.getCanonicalName());

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


}
