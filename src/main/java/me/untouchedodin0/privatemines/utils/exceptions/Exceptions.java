package me.untouchedodin0.privatemines.utils.exceptions;

import java.util.function.Function;

public class Exceptions {

  public static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
    //noinspection unchecked
    throw (E) e;
  }

  public static <A, B> Function<A, B> throwing(ThrowingFunction<A, B> function) {
    return a -> {
      try {
        return function.apply(a);
      } catch (Throwable e) {
        sneakyThrow(e);
      }
      return null;
    };
  }


  public interface ThrowingFunction<A, B> {

    B apply(A a) throws Throwable;
  }
}