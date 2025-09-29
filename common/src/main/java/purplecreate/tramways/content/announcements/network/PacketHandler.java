package purplecreate.tramways.content.announcements.network;

import com.mojang.blaze3d.audio.OggAudioStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.config.MessageConfig;
import purplecreate.tramways.content.announcements.sound.FragmentAudioStream;
import purplecreate.tramways.content.announcements.sound.MP3AudioStream;
import purplecreate.tramways.content.announcements.util.TTSFileManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PacketHandler {
  public static AudioStream getAudioStream(String voice, MessageConfig content) {
    switch (content.getType()) {
      case "tts":
        InputStream stream = TTSFileManager.instance.cachedStream(voice, content.getMessage().get(0));
        try {
          return new MP3AudioStream(stream);
        } catch (IOException e) {
          File file = TTSFileManager.instance.getFile(voice, content.getMessage().get(0));

          if (file.delete()) {
            Tramways.LOGGER.warn(
              "{}: {} Successfully deleted rogue file. ({})",
              e.getClass().getName(),
              e.getLocalizedMessage(),
              file.getName()
            );
          } else {
            file.deleteOnExit();
            Tramways.LOGGER.warn(
              "{}: {} Could not delete rogue file. Deletion queued for when VM terminates. ({})",
              e.getClass().getName(),
              e.getLocalizedMessage(),
              file.getName()
            );
          }

          return null;
        }
      case "files":
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        List<AudioStream> streams = new ArrayList<>();

        for (String part : content.getMessage()) {
          List<Resource> resources = manager.getResourceStack(new ResourceLocation(part));

          if (resources.isEmpty())
            continue;

          try {
            InputStream resourceStream = resources.get(0).open();
            if (part.endsWith(".mp3"))
              streams.add(new MP3AudioStream(resourceStream));
            else if (part.endsWith(".ogg"))
              streams.add(new OggAudioStream(resourceStream));
          } catch (IOException e) {
          }
        }

        return new FragmentAudioStream(streams);
    }

    return null;
  }
}
