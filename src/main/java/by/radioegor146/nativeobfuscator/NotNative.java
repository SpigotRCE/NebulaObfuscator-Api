package by.radioegor146.nativeobfuscator;

import java.lang.annotation.*;

@Retention(value = RetentionPolicy.CLASS) @Target(value = {ElementType.METHOD}) public @interface NotNative {
}
