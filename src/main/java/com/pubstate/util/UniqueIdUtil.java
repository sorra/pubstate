package com.pubstate.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * (22 chars) 48bit milliseconds + 80bit random value (ASCII-ordered URL-safe Base64-encoded)
 */
public final class UniqueIdUtil {

  private static final SecureRandom secureRandom = new SecureRandom();
  private static final byte[] remapper = new byte[128];
  static {
    byte[] oldCodes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".getBytes(StandardCharsets.US_ASCII);
    byte[] newCodes = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz~".getBytes(StandardCharsets.US_ASCII);
    for (int i = 0; i < oldCodes.length; i++) {
      remapper[oldCodes[i]] = newCodes[i];
    }
  }

  private UniqueIdUtil() {}

  public static String newId() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[18]);
    byteBuffer.putLong(System.currentTimeMillis());
    byte[] randomBytes = new byte[10];
    secureRandom.nextBytes(randomBytes);
    byteBuffer.put(randomBytes);

    return newId(byteBuffer);
  }

  static String newId(ByteBuffer byteBuffer) {
    byte[] original = Arrays.copyOfRange(byteBuffer.array(), 2, 18);
    byte[] encoded = Base64.getUrlEncoder().withoutPadding().encode(original);
    for (int i = 0; i < encoded.length; i++) {
      encoded[i] = remapper[encoded[i]];
    }
    return new String(encoded, StandardCharsets.US_ASCII);
  }

  public static String initial() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 22; i++) {
      sb.append('0');
    }
    return sb.toString();
  }

  public static boolean equal(Object id1, Object id2) {
    if (id1 == null || id2 == null) {
      // Here let null != null, in case the newly created beans have no id
      return false;
    }
    return id1.equals(id2);
  }
}
