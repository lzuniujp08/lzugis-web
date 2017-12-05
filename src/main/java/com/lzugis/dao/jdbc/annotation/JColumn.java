package com.lzugis.dao.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * <p>Title: JColumn.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: 21at</p>
 * @author yuc
 * @createdate 2013-7-5下午4:47:03
 * @version 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JColumn{
	
	String name() default "";
	
	boolean updatable() default true;
}
