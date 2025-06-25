package vapourdrive.simplestorage.integrations.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.Ingredient;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.content.crate.CrateScreen;
import vapourdrive.simplestorage.setup.Registration;

import java.util.List;

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

        addCrateUpgrade(0, registry);
        addCrateUpgrade(1, registry);
        addCrateUpgrade(2, registry);
        addCrateUpgrade(3, registry);
        addCrateWarding(registry);
    }

    private void addCrateUpgrade(int inTier, EmiRegistry registry) {
        ItemStack in_crate_stack = new ItemStack(Registration.CRATE_ITEM.get());
        in_crate_stack.set(Registration.TIER_DATA, inTier);
        EmiIngredient in_crate = EmiIngredient.of(Ingredient.of(in_crate_stack));
        EmiIngredient upgrade = EmiIngredient.of(Ingredient.of(Registration.STORAGE_COMPARTMENT_ITEM.get()));
        ItemStack crate_stack = new ItemStack(Registration.CRATE_ITEM.get());
        crate_stack.set(Registration.TIER_DATA, inTier+1);
        EmiStack out_crate = EmiStack.of(crate_stack);
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(SimpleStorage.MODID,"/crate_upgrade_"+inTier+1);
        registry.addRecipe(new ShapelessCraftingRecipe(List.of(in_crate, upgrade), out_crate, location));
    }

    private void addCrateWarding(EmiRegistry registry) {
        EmiIngredient in_crate = EmiIngredient.of(Ingredient.of(Registration.CRATE_ITEM.get()));
        EmiIngredient upgrade = EmiIngredient.of(Ingredient.of(Registration.WARDING_CHARM_ITEM.get()));
        ItemStack crate_stack = new ItemStack(Registration.CRATE_ITEM.get());
        crate_stack.set(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        EmiStack out_crate = EmiStack.of(crate_stack);
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(SimpleStorage.MODID,"/crate_upgrade_warding");
        registry.addRecipe(new ShapelessCraftingRecipe(List.of(in_crate, upgrade), out_crate, location));
    }
}
