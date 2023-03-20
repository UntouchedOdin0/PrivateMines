package me.untouchedodin0.privatemines.utils.addon.old;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Addon2 {
  String name();
  String author();
  String version();
  String description();
}
