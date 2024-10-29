package purplecreate.tramways.util;

import java.util.List;
import java.util.function.Function;

public class ListUtil {
  public static <T> T getFirst(List<T> list, Function<T, Boolean> func) {
    for (T item : list) {
      if (func.apply(item))
        return item;
    }

    return null;
  }
}
