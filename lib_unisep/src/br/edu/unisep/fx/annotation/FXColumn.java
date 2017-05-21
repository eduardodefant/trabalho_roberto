package br.edu.unisep.fx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javafx.geometry.Pos;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FXColumn {

	String property();
	
	double percentWidth() default -1;

	String dateFormat() default "";
	
	String numberFormat() default "";

	Pos align() default Pos.CENTER_LEFT;
	
}