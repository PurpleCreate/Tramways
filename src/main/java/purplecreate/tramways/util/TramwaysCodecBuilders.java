package purplecreate.tramways.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashSet;
import java.util.Set;

public interface TramwaysCodecBuilders {
  static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, Set<V>> set() {
    return streamCodec -> ByteBufCodecs.collection(HashSet::new, streamCodec);
  }

  static <B extends ByteBuf, V> StreamCodec<B, Set<V>> set(StreamCodec<B, V> base) {
    return base.apply(set());
  }
}
