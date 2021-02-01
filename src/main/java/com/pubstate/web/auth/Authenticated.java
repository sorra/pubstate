package com.pubstate.web.auth;

import java.lang.annotation.*;

/**
 * Require login for a controller or its method.
 * Usually used with Spring HandlerMethod and AnnotationUtils.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Authenticated {
  /**
   * Attribute for override.
   * For example,
   * a superclass or method is annotated, then its subclass correspondents inherit the annotation,
   * but a subclass correspondent can re-annotate to override.
   * @return true if should do authentication; false if should bypass authentication
   */
  boolean value() default true;
}
