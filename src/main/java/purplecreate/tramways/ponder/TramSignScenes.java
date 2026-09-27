package purplecreate.tramways.ponder;

import com.simibubi.create.AllItems;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.catnip.math.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import purplecreate.tramways.TBlocks;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signs.TramSignBlock;
import purplecreate.tramways.content.signs.TramSignBlockEntity;

public class TramSignScenes {
  private static void setSignDemand(
    SceneBuilder scene,
    SceneBuildingUtil util,
    BlockPos pos,
    ResourceLocation demand
  ) {
    scene.world().modifyBlockEntityNBT(
      util.select().position(pos),
      TramSignBlockEntity.class,
      (tag) -> tag.putString("Demand", demand.toString())
    );
  }

  public static void placingSigns(SceneBuilder scene, SceneBuildingUtil util) {
    scene.title("tram_signs_placing", "Placing Tram Signs");
    scene.showBasePlate();

    BlockPos tramSign = new BlockPos(4, 1, 2);
    BlockPos targetTrack = new BlockPos(1, 1, 2);

    // show track
    for (int i = 0; i < 5; i++) {
      scene.world().showSection(util.select().position(1, 1, i), Direction.DOWN);
      scene.idle(1);
    }
    scene.idle(19);

    scene.overlay().showControls(util.vector().topOf(targetTrack), Pointing.DOWN, 60)
      .rightClick()
      .withItem(TBlocks.TRAM_SIGN.asStack());
    scene.idle(60);
    scene.overlay().showText(60)
      .pointAt(util.vector().topOf(targetTrack))
      .text("Click on the track and place the sign nearby");
    scene.idle(80);

    scene.world().showSection(util.select().position(tramSign), Direction.DOWN);
    scene.idle(20);

    scene.overlay().showText(60)
      .pointAt(util.vector().topOf(tramSign))
      .text("Tram Signs can modify the behaviour of approaching trains in only one direction");
    scene.idle(80);

    scene.addKeyframe();

    scene.overlay().showControls(util.vector().topOf(tramSign), Pointing.DOWN, 60)
      .rightClick()
      .withItem(AllItems.WRENCH.asStack());
    scene.idle(60);
    scene.overlay().showText(60)
      .pointAt(util.vector().topOf(tramSign))
      .text("Click on the sign with a wrench to change it's settings");
    scene.idle(80);

    scene.addKeyframe();

    scene.overlay().showText(60)
      .pointAt(util.vector().topOf(tramSign))
      .text("There is even a different block style for railways!");
    scene.world().setBlock(
      tramSign,
      TBlocks.RAILWAY_SIGN.getDefaultState()
        .setValue(TramSignBlock.FACING, Direction.WEST),
      true
    );
    scene.idle(80);
  }

  public static void signDemandSpeedLimit(SceneBuilder scene, SceneBuildingUtil util) {
    scene.title("tram_signs_speed_limit", "Sign Demands: Speed Limiting");
    scene.showBasePlate();

    BlockPos tramSign = new BlockPos(4, 1, 2);

    // show tracks
    for (int i = 0; i < 5; i++) {
      scene.world().showSection(util.select().position(1, 1, i), Direction.DOWN);
      scene.idle(1);
    }

    // show focus
    scene.world().showSection(util.select().position(tramSign), Direction.DOWN);
    scene.idle(20);

    // text
    scene.overlay().showText(60)
      .pointAt(util.vector().topOf(tramSign))
      .text("The speed limit sign demand changes the speed of an automatic train");
    scene.idle(60);
    scene.overlay().showText(60)
      .pointAt(util.vector().topOf(tramSign))
      .text("If the train needs to slow down, it will start doing so before reaching the sign");
    scene.idle(60);
    scene.overlay().showText(60)
      .pointAt(util.vector().topOf(tramSign))
      .text("If the train needs to speed up, it will do so after passing the sign");
    scene.idle(60);
  }

  public static void signDemandWhistle(SceneBuilder scene, SceneBuildingUtil util) {
    scene.title("tram_signs_whistle", "Sign Demands: Whistling");
    scene.showBasePlate();

    BlockPos tramSign = new BlockPos(4, 1, 2);

    // show tracks
    for (int i = 0; i < 5; i++) {
      scene.world().showSection(util.select().position(1, 1, i), Direction.DOWN);
      scene.idle(1);
    }

    // show focus
    setSignDemand(scene, util, tramSign, Tramways.rl("whistle"));
    scene.world().showSection(util.select().position(tramSign), Direction.DOWN);
    scene.idle(20);

    // text
    scene.overlay().showText(60)
      .pointAt(util.vector().topOf(tramSign))
      .text("The whistle sign demand will make an automatic train whistle");
    scene.idle(60);
    scene.overlay().showText(60)
      .pointAt(util.vector().topOf(tramSign))
      .text("It'll whistle on the approach towards the sign");
    scene.idle(60);
  }

  public static void signDemandTemporaryLimits(SceneBuilder scene, SceneBuildingUtil util) {
    scene.title("tram_signs_temporary_limits", "Sign Demands: Temporary Speed Limits");
    scene.showBasePlate();

    BlockPos tramSign = new BlockPos(4, 1, 2);

    // show tracks
    for (int i = 0; i < 5; i++) {
      scene.world().showSection(util.select().position(1, 1, i), Direction.DOWN);
      scene.idle(1);
    }

    // show focus
    setSignDemand(scene, util, tramSign, Tramways.rl("temporary_speed"));
    scene.world().showSection(util.select().position(tramSign), Direction.DOWN);
    scene.idle(20);

    // text
    scene.overlay().showText(60)
      .pointAt(util.vector().topOf(tramSign))
      .text("The temporary speed limit sign demand will set an automatic train's speed limit");
    scene.idle(60);
    scene.overlay().showText(60)
      .pointAt(util.vector().topOf(tramSign))
      .text("This speed limit will be in place until a temporary speed limit end sign is reached");
    scene.idle(60);
    scene.overlay().showText(60)
      .pointAt(util.vector().topOf(tramSign))
      .text("Once the end sign is reached, the train will return to it's original speed");
    scene.idle(60);
    scene.overlay().showText(60)
      .pointAt(util.vector().topOf(tramSign))
      .text("The train will ignore any normal speed limits whilst in a temporary speed limit");
    scene.idle(60);
  }
}
