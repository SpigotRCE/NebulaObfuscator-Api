# Nebula Obfuscator API

A Java annotation-based API for controlling bytecode obfuscation behavior at the class, method, and field level.

---

## Overview

The Nebula Obfuscator API gives you fine-grained control over how your code is obfuscated. By annotating classes, methods, and fields, you can instruct the obfuscator to preserve names, keep signatures, inline methods, encrypt resources, or compile down to native code - without modifying any build configuration.

---

## Annotations

### `@KeepName`

Prevents the obfuscator from renaming the annotated element.

**Targets:** Class, Method, Field

```java
import dev.nebulaobf.api.annotation.KeepName;

@KeepName
public class MyPublicApi {

    @KeepName
    public String getUserName() { /*...*/ }

    @KeepName
    public String userEmail;
}
```

Use this on any class, method, or field whose name must remain stable - for example, public APIs, reflection targets, or serialized fields.

---

### `@KeepSignature`

Prevents the obfuscator from altering the method's signature (parameter and return types).

**Targets:** Method

```java
import dev.nebulaobf.api.annotation.KeepSignature;

@KeepSignature
public List<String> fetchItems(Map<String, Integer> params) { /*...*/ }
```

Use this when a method's generic type information needs to survive obfuscation, such as for frameworks that rely on reflection to read signatures at runtime.

---

### `@InlineMethod`

Marks a method as a candidate for inlining during obfuscation - the method body is inserted directly at call sites, eliminating the method call.

**Targets:** Method

```java
import dev.nebulaobf.api.annotation.InlineMethod;

@InlineMethod
private int add(int a, int b) {
    return a + b;
}
```

Best used on small, frequently-called utility methods where eliminating the call overhead and the method's existence improves both performance and obfuscation strength.

---

### `@Native` (from `by.radioegor146.nativeobfuscator`)

Marks a class or method to be compiled to native code by the native obfuscator.

**Targets:** Type, Method  
**Retention:** CLASS

```java
import by.radioegor146.nativeobfuscator.Native;

@Native
public class CriticalLogic {
    public static int compute(int x) { /*...*/ }
}
```

Applying `@Native` to a class marks all of its methods for native compilation. You can also apply it to individual methods for selective native conversion.

---

### `@NotNative` (from `by.radioegor146.nativeobfuscator`)

Excludes a specific method from native compilation, even if its containing class is annotated with `@Native`.

**Targets:** Method  
**Retention:** CLASS

```java
import by.radioegor146.nativeobfuscator.Native;
import by.radioegor146.nativeobfuscator.NotNative;

@Native
public class CriticalLogic {

    public static int compute(int x) { /*...*/ }  // compiled to native

    @NotNative
    public static String debugInfo() { /*...*/ }  // kept as JVM bytecode
}
```

---

## Resource Encryption

`NebulaResourceEncryption` is a stub class used to integrate resource encryption into your code. During obfuscation, the obfuscator replaces the stub's implementation with an encrypted resource loader.

Replace direct `getResourceAsStream` calls with the stub:

```java
import dev.nebulaobf.api.resource.NebulaResourceEncryption;

// Before
InputStream is = MyClass.class.getResourceAsStream("config.json");

// After
InputStream is = NebulaResourceEncryption.getResourceAsStream(MyClass.class, "config.json");
```

The obfuscator detects these calls and replaces them with decryption logic at build time. The encryption itself is handled entirely by the obfuscator - no manual key management is needed.

---

## Quick Reference

| Annotation | Target               | Effect |
|---|----------------------|---|
| `@KeepName` | Class, Method, Field | Preserves the element's name after obfuscation |
| `@KeepSignature` | Method               | Preserves the method's generic signature |
| `@InlineMethod` | Method               | Inlines the method at call sites |
| `@Native` | Type, Method         | Compiles the element to native code |
| `@NotNative` | Method               | Excludes a method from native compilation |

---

## Dependencies

- [ASM](https://asm.ow2.io/) - used by `AnnotationProcessor` to inspect bytecode nodes
- [native-obfuscator](https://github.com/radioegor146/native-obfuscator) - provides `@Native` and `@NotNative`