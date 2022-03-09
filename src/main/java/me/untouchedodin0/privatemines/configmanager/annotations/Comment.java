package me.untouchedodin0.privatemines.configmanager.annotations;

import java.lang.annotation.*;

/**
 * Used to denote comments which should be applied to a config path. Only supported in 1.18.1+
 * @author Redempt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Repeatable(Comments.class)
public @interface Comment {

    String value();

}