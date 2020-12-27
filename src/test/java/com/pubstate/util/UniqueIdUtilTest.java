package com.pubstate.util;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

public class UniqueIdUtilTest {

  @Test
  public void testIsOrdered() {
    long time = System.currentTimeMillis();
    String id1;
    {
      ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[18]);
      byteBuffer.putLong(time);
      byteBuffer.putChar(' ');
      byteBuffer.putLong(time);
      id1 = UniqueIdUtil.newId(byteBuffer);
    }

    String id2;
    {
      ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[18]);
      byteBuffer.putLong(time + 1);
      byteBuffer.putChar(' ');
      byteBuffer.putLong(time - 1);
      id2 = UniqueIdUtil.newId(byteBuffer);
    }
    Assert.assertTrue(String.format("%s versus %s", id1, id2), id1.compareTo(id2) < 0);
  }

  @Test
  public void testLength22() {
    String id = UniqueIdUtil.newId();
    System.out.println(id);
    Assert.assertEquals(22, id.length());
  }
}