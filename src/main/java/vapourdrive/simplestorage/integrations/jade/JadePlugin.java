package vapourdrive.simplestorage.integrations.jade;

import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.content.crate.CrateBlock;
import vapourdrive.simplestorage.content.crate.CrateTile;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    public static final ResourceLocation CRATES = ResourceLocation.fromNamespaceAndPath(SimpleStorage.MODID, "crate");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(CrateContentProvider.INSTANCE, CrateTile.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CrateContentProvider.INSTANCE, CrateBlock.class);
    }

}
