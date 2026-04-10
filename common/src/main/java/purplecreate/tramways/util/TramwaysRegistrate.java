package purplecreate.tramways.util;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.api.registry.registrate.SimpleBuilder;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.state.BlockBehaviour;
import purplecreate.tramways.TExtras;
import purplecreate.tramways.TTags;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signals.base.SignalType;
import purplecreate.tramways.content.signals.block.SignalBlock;
import purplecreate.tramways.content.signals.block.SignalBlockEntity;
import purplecreate.tramways.content.signals.block.SignalRenderer;
import purplecreate.tramways.content.signals.item.SignalItem;

import java.util.function.Supplier;

import static purplecreate.tramways.TBlocks.attachedBlock;

public class TramwaysRegistrate extends AbstractRegistrate<TramwaysRegistrate> {
  protected TramwaysRegistrate(String modid) {
    super(modid);
  }

  @ExpectPlatform
  public static TramwaysRegistrate create(String modid) {
    return new TramwaysRegistrate(modid);
  }

  public <T extends DisplaySource> SimpleBuilder<DisplaySource, T, TramwaysRegistrate> displaySource(String name, Supplier<T> supplier) {
    return this.entry(name, (callback) ->
      new SimpleBuilder<>(this, this, name, callback, CreateRegistries.DISPLAY_SOURCE, supplier)
        .byBlock(DisplaySource.BY_BLOCK)
        .byBlockEntity(DisplaySource.BY_BLOCK_ENTITY)
    );
  }

  public <T extends DisplayTarget> SimpleBuilder<DisplayTarget, T, TramwaysRegistrate> displayTarget(String name, Supplier<T> supplier) {
    return this.entry(name, (callback) ->
      new SimpleBuilder<>(this, this, name, callback, CreateRegistries.DISPLAY_TARGET, supplier)
        .byBlock(DisplayTarget.BY_BLOCK)
        .byBlockEntity(DisplayTarget.BY_BLOCK_ENTITY)
    );
  }

  public SignalBuilder<?> signalType(String name, NonNullSupplier<SignalType> factory) {
    return (SignalBuilder<?>) this.entry(name, (callback) ->
      SignalBuilder.create(this, self(), name, callback, factory)
    );
  }

  public static class SignalBuilder<P> extends BlockBuilder<SignalBlock, P> {
    private final SignalType signalType;
    private String cycleCategory;

    protected SignalBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, SignalType signalType) {
      super(owner, parent, name, callback, SignalBlock.of(signalType), BlockBehaviour.Properties::of);
      this.signalType = signalType;
    }

    public static <P> SignalBuilder<P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullSupplier<SignalType> factory) {
      return (SignalBuilder<P>)
        new SignalBuilder<>(owner, parent, name, callback, factory.get())
          .defaultBlockstate()
          .defaultLoot()
          .defaultLang()
          .onRegister((block) -> block.setBlockEntityEntry(owner.get(name, Registries.BLOCK_ENTITY_TYPE)));
    }

    public SignalBuilder<P> cycleCategory(String category) {
      cycleCategory = category;
      return this;
    }

    public SignalBuilder<P> attachesToBlock() {
      return (SignalBuilder<P>) this.tag(TTags.pole()).onRegister(attachedBlock());
    }

    public SignalBuilder<P> bindsToSignal() {
      return (SignalBuilder<P>) this.transform(DisplayTarget.displayTarget(TExtras.DisplayTargets.SIGNAL));
    }

    @Override
    public BlockEntry<SignalBlock> register() {
      this.blockEntity(SignalBlockEntity::new)
        .renderer(() -> SignalRenderer.of(signalType))
        .build();

      this.item(SignalItem.of(signalType, cycleCategory))
        .model((c, p) -> p.getExistingFile(Tramways.rl("item/" + c.getName())))
        .onRegister(SignalItem::onAfterRegister)
        .build();

      return super.register();
    }
  }
}
