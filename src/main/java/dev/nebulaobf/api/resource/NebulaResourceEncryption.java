package dev.nebulaobf.api.resource;

import by.radioegor146.nativeobfuscator.Native;

import java.io.InputStream;

/**
 * Used for resource encryption
 */
@Native public class NebulaResourceEncryption {
  /**
   * This is a stub method, it will be replaced with the actual implementation during obfuscation.
   * @param clazz the clazz loader used
   * @param name name of the resource
   * @return returns {@link InputStream} of the resource
   */
  public static InputStream getResourceAsStream(Class<?> clazz, String name) {
    return clazz.getResourceAsStream(name);
  }
}
