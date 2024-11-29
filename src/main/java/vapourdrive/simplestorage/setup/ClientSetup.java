package vapourdrive.simplestorage.setup;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.content.crate.CrateItem;
import vapourdrive.simplestorage.content.crate.CrateScreen;

@EventBusSubscriber(modid = SimpleStorage.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(Registration.CRATE_CONTAINER.get(), CrateScreen::new);
    }

    @SubscribeEvent
    public static void fmlClientSetupEvent(FMLClientSetupEvent event){
        event.enqueueWork(() -> { // ItemProperties#register is not threadsafe, so we need to call it on the main thread
            ItemProperties.register(
                    Registration.CRATE_ITEM.get(),
                    ResourceLocation.fromNamespaceAndPath(SimpleStorage.MODID, "tier"),
                    (stack, level, player, seed) -> CrateItem.getTier(stack)
            );
            ItemProperties.register(
                    Registration.CRATE_ITEM.get(),
                    ResourceLocation.fromNamespaceAndPath(SimpleStorage.MODID, "variant"),
                    (stack, level, player, seed) -> CrateItem.getVariant(stack)
            );
        });
    }
}
