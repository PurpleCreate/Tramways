package purplecreate.tramways.content.requestStop;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.foundation.utility.Components;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.requestStop.network.RequestStopC2SPacket;
import purplecreate.tramways.content.requestStop.network.StoppingBroadcastS2CPacket;

import java.util.Optional;
import java.util.UUID;

public class RequestStopClient {
  private static final UUID bossBar = UUID.randomUUID();
  private static boolean lastKeyDown = false;

  public static void tick(Minecraft mc) {
    Entity vehicle = mc.player.getVehicle();
    boolean keyDown = mc.options.keyJump.isDown();
    boolean listenForKey = false;

    if (vehicle instanceof CarriageContraptionEntity cce) {
      Optional<UUID> controlling = cce.getControllingPlayer();
      if (controlling.isEmpty() || controlling.get() != mc.player.getUUID())
        listenForKey = true;
    } else {
      mc.gui.getBossOverlay().update(
        ClientboundBossEventPacket.createRemovePacket(bossBar)
      );
    }

    if (listenForKey && lastKeyDown != keyDown) {
      lastKeyDown = keyDown;
      if (keyDown) onKeyDown();
    }
  }

  public static void handleBroadcast(Minecraft mc, StoppingBroadcastS2CPacket packet) {
    NonNullConsumer<ClientboundBossEventPacket> update = mc.gui.getBossOverlay()::update;

    update.accept(
      ClientboundBossEventPacket.createRemovePacket(bossBar)
    );

    if (packet.type == StoppingBroadcastS2CPacket.Type.SHOW_STOPPING) {
      AllSoundEvents.CONFIRM.play(mc.level, mc.player, mc.player.position(), 1f, 1f);
    }

    if (packet.type != StoppingBroadcastS2CPacket.Type.REMOVE) {
      update.accept(
        ClientboundBossEventPacket.createAddPacket(
          new BossEvent(
            bossBar,
            packet.type == StoppingBroadcastS2CPacket.Type.SHOW_STOPPING
              ? Tramways.translatable("request_stop.stopping")
              : Tramways.translatable("request_stop.countdown",
                                      Components.keybind("key.jump"),
                                      packet.stationName),
            packet.type == StoppingBroadcastS2CPacket.Type.SHOW_STOPPING
              ? BossEvent.BossBarColor.RED
              : BossEvent.BossBarColor.BLUE,
            BossEvent.BossBarOverlay.PROGRESS
          ) {
            @Override
            public float getProgress() {
              return packet.type == StoppingBroadcastS2CPacket.Type.SHOW_STOPPING
                ? 1f
                : packet.progress;
            }
          }
        )
      );
    }
  }

  private static void onKeyDown() {
    TNetworking.sendToServer(new RequestStopC2SPacket());
  }
}
