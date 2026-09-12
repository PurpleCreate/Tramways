package purplecreate.tramways.util;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.function.Supplier;

public enum Env {
  CLIENT,
  SERVER;

  @ExpectPlatform
  public static Env getEnv() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static boolean isDevVersion() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static String getVersion() {
    throw new AssertionError();
  }

  public static void unsafeRunWhenOn(Env env, Supplier<Runnable> toRun) {
    if (getEnv() == env) {
      toRun.get().run();
    }
  }

  public static <T> T unsafeEvaluateWhenOn(Env env, Supplier<Supplier<T>> toEvaluate) {
    return unsafeEvaluateWhenOn(env, toEvaluate, null);
  }

  public static <T> T unsafeEvaluateWhenOn(Env env, Supplier<Supplier<T>> toEvaluate, T defaultValue) {
    if (getEnv() == env) {
      return toEvaluate.get().get();
    }
    return defaultValue;
  }
}
