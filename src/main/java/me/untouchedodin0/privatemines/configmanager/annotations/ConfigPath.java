package me.untouchedodin0.privatemines.configmanager.annotations;

import java.lang.annotation.*;

/**
 * Indicates that a field should be populated with the last element of the path to its location in config
 * @author Redempt
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigPath {
}