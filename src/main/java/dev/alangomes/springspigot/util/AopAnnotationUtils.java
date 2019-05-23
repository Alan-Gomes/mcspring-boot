package dev.alangomes.springspigot.util;

import lombok.val;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public class AopAnnotationUtils {

    private AopAnnotationUtils() {}

    public static <T extends Annotation> Stream<T> getAppliableAnnotations(Method method, Class<T> annotation) {
        val declaringClass = ClassUtils.getUserClass(method.getDeclaringClass());
        val methodAnnotations = AnnotationUtils.getRepeatableAnnotations(method, annotation);
        val classAnnotations = AnnotationUtils.getRepeatableAnnotations(declaringClass, annotation);
        return Stream.concat(classAnnotations.stream(), methodAnnotations.stream());
    }

}
