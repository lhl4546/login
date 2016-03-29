/**
 * 
 */
package com.fire.login.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lhl
 *
 *         2016年3月28日 下午4:45:32
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRequestHandler {

    String uri();

    boolean isEnabled();
}
