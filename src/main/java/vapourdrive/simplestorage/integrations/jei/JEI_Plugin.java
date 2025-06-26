package vapourdrive.simplestorage.integrations.jei;

import dev.emi.emi.api.stack.EmiStack;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.setup.Registration;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEI_Plugin implements IModPlugin {


    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(SimpleStorage.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
//        registry.addIngredientInfo(new ItemStack(Registration.PRIMITIVE_QUARRY_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("primitivequarry.primitive_quarry.info"));
        DeferredComponent comp = new DeferredComponent(SimpleStorage.MODID, "crate.info");
        registry.addIngredientInfo(new ItemStack(Registration.CRATE_ITEM.get()), VanillaTypes.ITEM_STACK, comp.get());
        addCrateUpgrade(0, registry);
        addCrateUpgrade(1, registry);
        addCrateUpgrade(2, registry);
        addCrateUpgrade(3, registry);
        addWardingUpgrade(registry);

    }

    @Override
    public void registerExtraIngredients(@NotNull IExtraIngredientRegistration registry) {
        List<ItemStack> stacks = new ArrayList<>(List.of());
        for(int i=1; i<5; i++){
            ItemStack stack = new ItemStack(Registration.CRATE_ITEM.get());
            stack.set(Registration.TIER_DATA, i);
            stacks.add(stack);
        }
        for(int i=1; i<5; i++){
            ItemStack stack = new ItemStack(Registration.CRATE_ITEM.get());
            stack.set(Registration.VARIANT_DATA, i);
            stacks.add(stack);
        }
        registry.addExtraItemStacks(stacks);
    }

    private void addCrateUpgrade(int inTier, IRecipeRegistration registry) {
        ItemStack in_crate_stack = new ItemStack(Registration.CRATE_ITEM.get());
        in_crate_stack.set(Registration.TIER_DATA, inTier);
        Ingredient in_crate = Ingredient.of(in_crate_stack);
        Ingredient upgrade = Ingredient.of(Registration.STORAGE_COMPARTMENT_ITEM.get());
        ItemStack crate_stack = new ItemStack(Registration.CRATE_ITEM.get());
        crate_stack.set(Registration.TIER_DATA, inTier+1);
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SimpleStorage.MODID,"/crate_upgrade_"+inTier+1);
        CraftingRecipe recipe = new ShapelessRecipe(SimpleStorage.MODID+".crate_upgrade", CraftingBookCategory.MISC, crate_stack, NonNullList.of(in_crate, upgrade, in_crate));
        registry.addRecipes(RecipeTypes.CRAFTING, List.of(new RecipeHolder<>(id, recipe)));
    }

    private void addWardingUpgrade(IRecipeRegistration registry) {
        Ingredient in_crate = Ingredient.of(Registration.CRATE_ITEM.get());
        Ingredient upgrade = Ingredient.of(Registration.WARDING_CHARM_ITEM.get());
        ItemStack crate_stack = new ItemStack(Registration.CRATE_ITEM.get());
        crate_stack.set(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SimpleStorage.MODID,"/crate_upgrade_warding");
        CraftingRecipe recipe = new ShapelessRecipe(SimpleStorage.MODID+".crate_warding_upgrade", CraftingBookCategory.MISC, crate_stack, NonNullList.of(in_crate, upgrade, in_crate));
        registry.addRecipes(RecipeTypes.CRAFTING, List.of(new RecipeHolder<>(id, recipe)));
    }

}
