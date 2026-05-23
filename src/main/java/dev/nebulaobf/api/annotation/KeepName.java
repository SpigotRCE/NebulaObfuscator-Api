package dev.nebulaobf.api.annotation;

import org.objectweb.asm.tree.*;

import java.lang.annotation.*;

@ObfuscationControl @Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD}) public @interface KeepName {
  class Processor {
    public static boolean isAnnotated(ClassNode n) {
      return AnnotationProcessor.isAnnotated(n, KeepName.class);
    }

    public static boolean isAnnotated(MethodNode n) {
      return AnnotationProcessor.isAnnotated(n, KeepName.class);
    }

    public static boolean isAnnotated(FieldNode n) {
      return AnnotationProcessor.isAnnotated(n, KeepName.class);
    }
  }
}
