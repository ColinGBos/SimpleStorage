package vapourdrive.simplestorage.integrations.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.setup.Registration;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

@JeiPlugin
public class JEI_Plugin implements IModPlugin {


    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(SimpleStorage.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
//        registration.addIngredientInfo(new ItemStack(Registration.PRIMITIVE_QUARRY_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("primitivequarry.primitive_quarry.info"));
        DeferredComponent comp = new DeferredComponent(SimpleStorage.MODID, "crate.info");
        registration.addIngredientInfo(new ItemStack(Registration.CRATE_ITEM.get()), VanillaTypes.ITEM_STACK, comp.get());
    }

}
