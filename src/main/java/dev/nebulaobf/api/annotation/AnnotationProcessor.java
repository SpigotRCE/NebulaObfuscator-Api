package dev.nebulaobf.api.annotation;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.List;

public final class AnnotationProcessor {
  private AnnotationProcessor() {
  }

  public static boolean isAnnotated(ClassNode clazz, Class<?> annotation) {
    String desc = Type.getDescriptor(annotation);
    return hasAnnotation(clazz.visibleAnnotations, desc) || hasAnnotation(clazz.invisibleAnnotations, desc);
  }

  private static boolean hasAnnotation(List<AnnotationNode> annotations, String desc) {
    if (annotations == null) {
      return false;
    }
    for (AnnotationNode ann : annotations) {
      if (desc.equals(ann.desc)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isAnnotated(MethodNode method, Class<?> annotation) {
    String desc = Type.getDescriptor(annotation);
    return hasAnnotation(method.visibleAnnotations, desc) || hasAnnotation(method.invisibleAnnotations, desc);
  }

  public static boolean isAnnotated(FieldNode field, Class<?> annotation) {
    String desc = Type.getDescriptor(annotation);
    return hasAnnotation(field.visibleAnnotations, desc) || hasAnnotation(field.invisibleAnnotations, desc);
  }
}
