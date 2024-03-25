package cn.autumnclouds.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 命令实现注解，用于标记命令实现类。
 * 标记有此注解的类将被识别为命令对象，并自动注册到命令解释器中。
 *
 * @author Fu Qiujie
 * @since 2024/3/16
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandImpl {
}