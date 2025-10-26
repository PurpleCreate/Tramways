package purplecreate.tramways.content.announcements.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
  public static MessageDigest getSha256() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
