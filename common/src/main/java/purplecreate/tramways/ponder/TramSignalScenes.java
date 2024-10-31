package purplecreate.tramways.ponder;

import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.railwayteam.railways.content.switches.TrackSwitchBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.ParrotElement;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.ponder.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import purplecreate.tramways.TBlocks;
import purplecreate.tramways.content.signals.TramSignalBlock;
import purplecreate.tramways.content.signals.TramSignalState;
import purplecreate.tramways.content.signs.TramSignBlock;

import java.util.Optional;

public class TramSignalScenes {
  // https://github.com/Layers-of-Railways/Railway/blob/ac1937f95bdaa6f6463dda547f73a5d5b279ac42/common/src/main/java/com/railwayteam/railways/ponder/TrainScenes.java#L652
  private static void setSwitchState(SceneBuilder scene, BlockPos switchPos, TrackSwitchBlock.SwitchState state) {
    scene.addInstruction(new PonderInstruction() {
      @Override
      public boolean isComplete() {
        return true;
      }

      private Optional<TrackSwitchBlockEntity> getSwitch(PonderScene scene) {
        if (scene.getWorld().getBlockEntity(switchPos) instanceof TrackSwitchBlockEntity switchBE)
          return Optional.of(switchBE);
        return Optional.empty();
      }

      private TrackSwitchBlock.SwitchState prevState = TrackSwitchBlock.SwitchState.NORMAL;

      @Override
      public void reset(PonderScene scene) {
        super.reset(scene);
        getSwitch(scene).ifPresent(switchBE -> switchBE.setStatePonder(prevState));
      }

      @Override
      public void tick(PonderScene scene) {
        getSwitch(scene).ifPresent(switchBE -> {
          prevState = switchBE.getState();
          switchBE.setStatePonder(state);
        });
      }
    });
  }

  private static void setTramSignalState(SceneBuilder scene, BlockPos tramSignal, TramSignalState next) {
    scene.world.modifyBlock(
      tramSignal,
      (state) -> state.setValue(TramSignalBlock.STATE, next),
      false
    );
  }

  public static void trackSignals(SceneBuilder scene, SceneBuildingUtil util) {
    scene.title("tram_signals", "Tram Signals with Track Signals");
    scene.scaleSceneView(.6f);
    scene.showBasePlate();

    BlockPos tramSignal = new BlockPos(7, 3, 4);
    BlockPos girder = new BlockPos(7, 2, 4);
    BlockPos trackSignal = new BlockPos(7, 1, 4);
    Selection train = util.select.fromTo(7, 2, 6, 7, 3, 7);

    // show track
    for (int i = 0; i < 9; i++) {
      scene.world.showSection(util.select.position(4, 1, i), Direction.DOWN);
      scene.idle(1);
    }

    // show focuses
    scene.world.showSection(util.select.position(trackSignal), Direction.DOWN);
    scene.idle(1);
    scene.world.showSection(util.select.position(girder), Direction.DOWN);
    scene.idle(1);
    scene.world.showSection(util.select.position(tramSignal), Direction.DOWN);
    scene.idle(20);

    // some labels
    scene.overlay.showText(60)
      .pointAt(util.vector.topOf(tramSignal))
      .text("Tram Signals can be placed on track signals to display the signal's state");
    scene.idle(80);

    scene.overlay.showText(60)
      .pointAt(util.vector.centerOf(girder))
      .attachKeyFrame()
      .text("They can be placed with different types of pole between the tram and track signal");

    scene.world.setBlock(
      girder,
      Blocks.SPRUCE_FENCE.defaultBlockState(),
      true
    );
    scene.idle(20);
    scene.world.setBlock(
      girder,
      TBlocks.TRAM_SIGN.get().defaultBlockState()
        .setValue(TramSignBlock.FACING, Direction.WEST),
      true
    );
    scene.idle(20);
    scene.world.setBlock(
      girder,
      AllBlocks.METAL_GIRDER.get().defaultBlockState(),
      true
    );
    scene.idle(40);

    // train time
    ElementLink<WorldSectionElement> trainElement = scene.world.showIndependentSection(train, null);
    ElementLink<ParrotElement> birbElement =
      scene.special.createBirb(util.vector.centerOf(4, 3, 0), ParrotElement.FacePointOfInterestPose::new);

    scene.world.moveSection(trainElement, util.vector.of(-3, 0, -6), 0);

    scene.overlay.showText(60)
      .pointAt(util.vector.topOf(tramSignal))
      .attachKeyFrame()
      .text("When a train occupies the signal block...");
    scene.idle(80);

    scene.world.moveSection(trainElement, util.vector.of(0, 0, 6), 30);
    scene.special.moveParrot(birbElement, util.vector.of(0, 0, 6), 30);
    scene.idle(10);

    scene.world.changeSignalState(trackSignal, SignalBlockEntity.SignalState.RED);
    setTramSignalState(scene, tramSignal, TramSignalState.STOP);
    scene.idle(10);

    scene.overlay.showText(60)
      .pointAt(util.vector.topOf(tramSignal))
      .text("...the tram signal changes to a horizontal line");
    scene.idle(80);
  }

  public static void trackSwitches(SceneBuilder scene, SceneBuildingUtil util) {
    scene.title("tram_switches", "Tram Signals with Track Switches");
    scene.scaleSceneView(.6f);
    scene.showBasePlate();

    BlockPos tramSignal = new BlockPos(7, 3, 1);
    BlockPos girder = new BlockPos(7, 2, 1);
    BlockPos trackSwitch = new BlockPos(7, 1, 1);

    Vec3 switchOrigin = util.vector.centerOf(4, 1, 0);
    Vec3 switchForward = util.vector.centerOf(4, 1, 7);
    Vec3 switchRight = util.vector.centerOf(1, 1, 7);
    Vec3 switchLeft = util.vector.centerOf(7, 1, 7);

    // show track
    for (int i = 0; i < 9; i++) {
      scene.world.showSection(util.select.position(4, 1, i), Direction.DOWN);
      scene.idle(1);
    }

    scene.world.showSection(util.select.position(1, 1, 7), Direction.DOWN);
    scene.idle(1);
    scene.world.showSection(util.select.position(7, 1, 7), Direction.DOWN);
    scene.idle(1);

    // show focuses
    scene.world.showSection(util.select.position(trackSwitch), Direction.DOWN);
    scene.idle(1);
    scene.world.showSection(util.select.position(girder), Direction.DOWN);
    scene.idle(1);
    scene.world.showSection(util.select.position(tramSignal), Direction.DOWN);
    scene.idle(20);

    scene.overlay.showText(60)
      .pointAt(util.vector.topOf(tramSignal))
      .text("Tram Signals can also show a track switch's state");

    scene.overlay.showBigLine(PonderPalette.RED, switchOrigin, switchRight, 40);
    setSwitchState(scene, trackSwitch, TrackSwitchBlock.SwitchState.REVERSE_RIGHT);
    setTramSignalState(scene, tramSignal, TramSignalState.RIGHT);
    scene.idle(40);

    scene.overlay.showBigLine(PonderPalette.RED, switchOrigin, switchForward, 40);
    setSwitchState(scene, trackSwitch, TrackSwitchBlock.SwitchState.NORMAL);
    setTramSignalState(scene, tramSignal, TramSignalState.FORWARD);
    scene.idle(40);

    scene.overlay.showBigLine(PonderPalette.RED, switchOrigin, switchLeft, 40);
    setSwitchState(scene, trackSwitch, TrackSwitchBlock.SwitchState.REVERSE_LEFT);
    setTramSignalState(scene, tramSignal, TramSignalState.LEFT);
    scene.idle(40);
  }
}
