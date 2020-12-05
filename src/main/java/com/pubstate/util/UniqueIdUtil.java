package com.pubstate.util;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

/**
 * 22 chars, URL safe Base64 encoded UUID (Java random)
 */
public final class UniqueIdUtil {
  private UniqueIdUtil() {}

  public static String newId() {
    UUID uuid = UUID.randomUUID();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
    byteBuffer.putLong(uuid.getMostSignificantBits());
    byteBuffer.putLong(uuid.getLeastSignificantBits());
    byte[] encoded = Base64.getUrlEncoder().encode(byteBuffer.array());
    return new String(encoded, 0, 22);
  }

  public static String one() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 21; i++) {
      sb.append('0');
    }
    sb.append('1');
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
