package purplecreate.tramways.util;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.util.ArrayList;
import java.util.List;

public class UploadDialog {
  private String title = "Select files";
  private String defaultPath = null;
  private final List<String> patterns = new ArrayList<>();
  private String patternDescription = null;
  private boolean allowMultiple = false;

  public UploadDialog title(String title) {
    this.title = title;
    return this;
  }

  public UploadDialog defaultPath(String defaultPath) {
    this.defaultPath = defaultPath;
    return this;
  }

  public UploadDialog patterns(String... patterns) {
    this.patterns.addAll(List.of(patterns));
    return this;
  }

  public UploadDialog patternDescription(String patternDescription) {
    this.patternDescription = patternDescription;
    return this;
  }

  public UploadDialog allowMultiple() {
    allowMultiple = true;
    return this;
  }

  public String[] createSync() {
    MemoryStack stack = MemoryStack.stackGet();
    int lastPointer = stack.getPointer();

    String result = TinyFileDialogs.tinyfd_openFileDialog(
      title,
      defaultPath,
      stack.pointers(
        patterns
          .stream()
          .mapToLong(p -> {
            stack.nUTF8Safe(p, true);
            return stack.getPointerAddress();
          })
          .toArray()
      ),
      patternDescription,
      allowMultiple
    );

    stack.setPointer(lastPointer);
    return result == null ? new String[0] : result.split("\\|");
  }
}
