package com.fig.annotations;

import java.lang.annotation.*;

/**
 * Marker annotation to denote that the processing inside an annotated method is wrapped in a transaction
 * User: Fizal
 * Date: 11/22/13
 * Time: 6:36 PM
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Transactional {

}
