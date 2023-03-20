package me.untouchedodin0.privatemines.utils.addon.old;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(Dependencies.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dependency {
  String name();
  String version();
  boolean isAddon();
}
