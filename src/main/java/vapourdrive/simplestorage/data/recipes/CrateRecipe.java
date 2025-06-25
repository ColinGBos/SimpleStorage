package vapourdrive.simplestorage.data.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.setup.Registration;

public class CrateRecipe extends CustomRecipe{
//    protected final ItemStack ingredient;
//    protected final ItemStack result;

    public CrateRecipe(CraftingBookCategory category) {
        super(category);
//        this.ingredient = ingredient;
//        this.result = result;
    }

    @Override
    public boolean matches(@NotNull CraftingInput craftingInput, @NotNull Level level) {
        if (craftingInput.ingredientCount() == 2){
            ItemStack one = craftingInput.getItem(0);
            ItemStack two = craftingInput.getItem(1);
            if (one.is(Registration.CRATE_ITEM.get())){
                if (two.is(Registration.STORAGE_COMPARTMENT_ITEM.get())) {
                    return one.getOrDefault(Registration.TIER_DATA, 0) < 4;
                } else if (two.is(Registration.WARDING_CHARM_ITEM.get())) {
                    return !one.has(DataComponents.CONTAINER);
                }
            } else if (two.is(Registration.CRATE_ITEM.get())){
                if (one.is(Registration.STORAGE_COMPARTMENT_ITEM.get())) {
                    return two.getOrDefault(Registration.TIER_DATA, 0) < 4;
                } else if (one.is(Registration.WARDING_CHARM_ITEM.get())) {
                    return !two.has(DataComponents.CONTAINER);
                }
            }
        }

        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput craftingInput, HolderLookup.@NotNull Provider provider) {
        ItemStack crateStack = ItemStack.EMPTY;
        boolean tierUp = false;
        boolean warded = false;
        for (ItemStack stack : craftingInput.items()) {
            if (stack.is(Registration.CRATE_ITEM.get())) {
                crateStack = stack.copy();
                crateStack.setCount(1);
            } else if (stack.is(Registration.STORAGE_COMPARTMENT_ITEM.get())) {
                tierUp = true;
            } else if (stack.is(Registration.WARDING_CHARM_ITEM.get())) {
                warded = true;
            }
        }
        if(tierUp) {
            crateStack.set(Registration.TIER_DATA, crateStack.getOrDefault(Registration.TIER_DATA, 0)+1);
        }
        if(warded){
            crateStack.set(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        }
        return crateStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Registration.CRATE_RECIPE_SERIALIZER.get();
    }

}
