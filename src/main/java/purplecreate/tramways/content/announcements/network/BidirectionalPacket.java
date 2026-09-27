package purplecreate.tramways.content.announcements.network;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;

public interface BidirectionalPacket<C extends ClientboundPacketPayload, S extends ServerboundPacketPayload> {
  C clientbound();
  S serverbound();
}
