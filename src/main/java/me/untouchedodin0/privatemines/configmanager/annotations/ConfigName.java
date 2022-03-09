package me.untouchedodin0.privatemines.configmanager.annotations;

import java.lang.annotation.*;

/**
 * Specifies the name that should be used to access and set a value in config
 * @author Redempt
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigName {

    String value();

}