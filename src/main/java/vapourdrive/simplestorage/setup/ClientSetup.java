package vapourdrive.simplestorage.setup;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.content.crate.CrateScreen;

@EventBusSubscriber(modid = SimpleStorage.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(Registration.CRATE_CONTAINER.get(), CrateScreen::new);
    }
}
