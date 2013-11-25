package com.fig.annotations;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Documents that the annotated element (class/method/field) is NOT threadsafe
 * User: Fizal
 * Date: 11/24/13
 * Time: 5:39 PM
 */
@Documented
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotThreadSafe
{
}