package vapourdrive.simplestorage.integrations.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;
import vapourdrive.simplestorage.content.crate.CrateScreen;

@EmiEntrypoint
public class EMI_Plugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addExclusionArea(CrateScreen.class, (screen, consumer) -> {
            int left = screen.getGuiLeft()-10;
            int top = screen.getGuiTop()-10;
            int width = (int)(screen.getXSize()*1.1)+10;
            int height = screen.getYSize()+20;
            consumer.accept(new Bounds(left, top, width, height));
        });
    }
}
