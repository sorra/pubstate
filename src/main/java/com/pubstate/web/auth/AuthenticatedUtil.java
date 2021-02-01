package com.pubstate.web.auth;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

public class AuthenticatedUtil {

  private AuthenticatedUtil() {}

  public static boolean shouldAuthenticate(HandlerMethod handler) {
    Authenticated annotation = AnnotationUtils.getAnnotation(handler.getMethod(), Authenticated.class);
    if (annotation == null) {
      annotation = AnnotationUtils.getAnnotation(handler.getBeanType(), Authenticated.class);
    }

    return annotation != null && annotation.value();
  }
}
