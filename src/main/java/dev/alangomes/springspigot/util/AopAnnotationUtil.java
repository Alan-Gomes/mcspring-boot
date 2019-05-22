package dev.alangomes.springspigot.util;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Stream;

public class AopAnnotationUtil {

    private AopAnnotationUtil() {}

    public static <T extends Annotation> Stream<T> getAppliableAnnotations(Method method, Class<T> annotation) {
        Class<?> declaringClass = ClassUtils.getUserClass(method.getDeclaringClass());
        Set<T> methodAnnotations = AnnotationUtils.getRepeatableAnnotations(method, annotation);
        Set<T> classAnnotations = AnnotationUtils.getRepeatableAnnotations(declaringClass, annotation);
        return Stream.concat(classAnnotations.stream(), methodAnnotations.stream());
    }

}
