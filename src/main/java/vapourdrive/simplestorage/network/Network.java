package vapourdrive.simplestorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.SimpleStorage;

@EventBusSubscriber(modid = SimpleStorage.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class Network {
//    public static void registerPayloadHandlers(final IEventBus bus) {
//        bus.addListener(Network::registerPayloadHandler);
//    }

    @SubscribeEvent
    private static void registerPayloadHandler(RegisterPayloadHandlersEvent evt) {
        PayloadRegistrar registrar = evt.registrar(SimpleStorage.MODID).versioned("1");
        registrar.playToServer(SortNotification.TYPE, SortNotification.STREAM_CODEC, InvHandler::sort);
        registrar.playToServer(TransferNotification.TYPE, TransferNotification.STREAM_CODEC, InvHandler::transfer);
        SimpleStorage.debugLog("registered Payload handler");
    }

    public record SortNotification(BlockPos blockPos, int means) implements CustomPacketPayload {

        static final Type<SortNotification> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleStorage.MODID, "sorting"));
        public static final StreamCodec<ByteBuf, SortNotification> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                SortNotification::blockPos,
                ByteBufCodecs.VAR_INT,
                SortNotification::means,
                SortNotification::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record TransferNotification(BlockPos blockPos, int means) implements CustomPacketPayload {

        static final Type<TransferNotification> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleStorage.MODID, "transfer"));
        public static final StreamCodec<ByteBuf, TransferNotification> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                TransferNotification::blockPos,
                ByteBufCodecs.VAR_INT,
                TransferNotification::means,
                TransferNotification::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}