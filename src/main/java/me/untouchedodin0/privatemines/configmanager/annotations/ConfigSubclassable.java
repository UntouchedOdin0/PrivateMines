package me.untouchedodin0.privatemines.configmanager.annotations;

import java.lang.annotation.*;

/**
 * Indicates that this type can be
 * @author Redempt
 */


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface ConfigSubclassable {
}