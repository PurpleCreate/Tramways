package purplecreate.tramways.ponder;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class SpeakerScenes {
  public static void train(SceneBuilder scene, SceneBuildingUtil util) {
    scene.title("speaker_train", "Speakers on Trains");
    scene.showBasePlate();

    BlockPos speaker = new BlockPos(2, 1, 2);

    scene.world.showSection(util.select.position(speaker), Direction.DOWN);
    scene.idle(20);

    scene.overlay.showText(60)
      .pointAt(util.vector.topOf(speaker))
      .text("Using speakers is as simple as placing it on a train, assembling it, and giving it a schedule!");
    scene.idle(60);
  }

  public static void station(SceneBuilder scene, SceneBuildingUtil util) {
    scene.title("speaker_station", "Speakers on Stations");
    scene.showBasePlate();

    BlockPos speaker = new BlockPos(3, 1, 2);
    BlockPos station = new BlockPos(4, 1, 2);
    BlockPos displayLink = new BlockPos(4, 1, 1);

    // show track
    for (int i = 0; i < 5; i++) {
      scene.world.showSection(util.select.position(1, 1, i), Direction.DOWN);
      scene.idle(1);
    }

    // show focuses
    scene.world.showSection(util.select.position(speaker), Direction.DOWN);
    scene.idle(1);
    scene.world.showSection(util.select.position(station), Direction.DOWN);
    scene.idle(1);
    scene.world.showSection(util.select.position(displayLink), Direction.DOWN);
    scene.idle(20);

    // explanation
    scene.overlay.showText(60)
      .pointAt(util.vector.topOf(displayLink))
      .text("Use a display link on a station to announce the station's arrivals");
    scene.idle(60);
    scene.overlay.showText(60)
      .pointAt(util.vector.topOf(displayLink))
      .text("Make sure to leave the display link set to 'Train Station Summary'");
    scene.idle(60);
  }
}
