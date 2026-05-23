package dev.nebulaobf.api.annotation;

import org.objectweb.asm.tree.MethodNode;

import java.lang.annotation.*;

@ObfuscationControl @Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD}) public @interface KeepSignature {
  class Processor {
    public static boolean isAnnotated(MethodNode n) {
      return AnnotationProcessor.isAnnotated(n, KeepSignature.class);
    }
  }
}
