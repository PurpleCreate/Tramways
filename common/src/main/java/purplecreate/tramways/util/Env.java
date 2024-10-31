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

  public static void unsafeRunWhenOn(Env env, Supplier<Runnable> toRun) {
    if (getEnv() == env) {
      toRun.get().run();
    }
  }
}
