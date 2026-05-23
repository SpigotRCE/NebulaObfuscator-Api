# Nebula Obfuscator API

A Java annotation-based API for controlling bytecode obfuscation behavior at the class, method, and field level.

---

## Overview

The Nebula Obfuscator API gives you fine-grained control over how your code is obfuscated. By annotating classes, 
methods, and fields, you can instruct the obfuscator to preserve names, keep signatures, inline methods, encrypt resources, or compile down to native code - without modifying any build configuration.

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

Use this on any class, method, or field whose name must remain stable - for example, public APIs, reflection targets, 
or serialized fields.

---

### `@KeepSignature`

Prevents the obfuscator from altering the method's signature (parameter and return types).

**Targets:** Method

```java
import dev.nebulaobf.api.annotation.KeepSignature;

@KeepSignature
public List<String> fetchItems(Map<String, Integer> params) { /*...*/ }
```

Use this when a method's generic type information needs to survive obfuscation, such as for frameworks that rely on 
reflection to read signatures at runtime.

---

### `@InlineMethod`

Marks a method as a candidate for inlining during obfuscation - the method body is inserted directly at call sites, 
eliminating the method call.

**Targets:** Method

```java
import dev.nebulaobf.api.annotation.InlineMethod;

@InlineMethod
private int add(int a, int b) {
    return a + b;
}
```

Best used on small, frequently-called utility methods where eliminating the call overhead and the method's existence 
improves both performance and obfuscation strength.

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

Applying `@Native` to a class marks all of its methods for native compilation. You can also apply it to individual 
methods for selective native conversion.

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

`NebulaResourceEncryption` is a stub class used to integrate resource encryption into your code. During obfuscation, 
the obfuscator replaces the stub's implementation with an encrypted resource loader.

Replace direct `getResourceAsStream` calls with the stub:

```java
import dev.nebulaobf.api.resource.NebulaResourceEncryption;

// Before
InputStream is = MyClass.class.getResourceAsStream("config.json");

// After
InputStream is = NebulaResourceEncryption.getResourceAsStream(MyClass.class, "config.json");
```

The obfuscator detects these calls and replaces them with decryption logic at build time. The encryption itself is 
handled entirely by the obfuscator - no manual key management is needed.
After obfuscation, the annotations are removed from classes and their members.
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

## Setup
 
Add the full NebulaObfuscator API jar to your project so all annotations are available at compile time. However, only 
the `resource` package needs to be **shaded** into your output jar - this ensures `NebulaResourceEncryption` works 
correctly when running your jar pre-obfuscation during development. The annotation classes themselves are only needed 
at compile/obfuscation time and don't need to be bundled.
 
### Gradle (Shadow Plugin)
 
```groovy
plugins {
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}
 
dependencies {
    // Full jar on the compile classpath - gives you all annotations
    compileOnly 'dev.nebulaobf.obfuscator:NebulaObfuscator-Api:1.0'
 
    // Resource package also needs to be resolvable at runtime pre-obfuscation
    implementation 'dev.nebulaobf.obfuscator:NebulaObfuscator-Api:1.0'
}
 
shadowJar {
    // The annotation classes are compile-only and don't belong in the shaded output.
    dependencies {
        include(dependency('dev.nebulaobf.obfuscator:NebulaObfuscator-Api'))
    }
    exclude 'dev/nebulaobf/api/annotation/**'
    exclude 'by/radioegor146/**'
    exclude 'org/objectweb/asm/**'
}
```
 
### Maven (Shade Plugin)
 
```xml
<dependencies>
    <!-- Full jar on the compile classpath - gives you all annotations -->
    <dependency>
        <groupId>dev.nebulaobf.obfuscator</groupId>
        <artifactId>NebulaObfuscator-Api</artifactId>
        <version>1.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
 
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.5.2</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals><goal>shade</goal></goals>
                    <configuration>
                        <artifactSet>
                            <includes>
                                <include>dev.nebulaobf:nebula-api</include>
                            </includes>
                        </artifactSet>
                        <filters>
                            <filter>
                                <artifact>dev.nebulaobf.obfuscator:NebulaObfuscator-Api</artifact>
                                <!-- Only bundle the resource package; exclude everything else -->
                                <includes>
                                    <include>dev/nebulaobf/api/resource/**</include>
                                </includes>
                            </filter>
                        </filters>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
 
### Why only shade the resource package?
 
| Package | Needed at compile time | Needs to be shaded |
|---|---|---|
| `dev.nebulaobf.api.annotation` | ✅ (for `@KeepName`, etc.) | ❌ (consumed by the obfuscator, not at runtime) |
| `dev.nebulaobf.api.resource` | ✅ | ✅ (stub must exist pre-obfuscation so your jar runs) |
| `by.radioegor146.nativeobfuscator` | ✅ (for `@Native`, etc.) | ❌ (compile/obfuscation time only) |
 
After obfuscation runs, the obfuscator replaces the `NebulaResourceEncryption` stub with its own decryption 
implementation and all the resources defined are encrypted, so the shaded stub is no longer present in the final 
distributed jar.
 
---

## Dependencies

- [ASM](https://asm.ow2.io/) - used by `AnnotationProcessor` to inspect bytecode nodes
- [native-obfuscator](https://github.com/radioegor146/native-obfuscator) - provides `@Native` and `@NotNative`