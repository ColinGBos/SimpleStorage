package vapourdrive.simplestorage.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.data.recipes.CrateRecipe;
import vapourdrive.simplestorage.setup.Registration;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        SpecialRecipeBuilder.special(CrateRecipe::new).save(output, ResourceLocation.fromNamespaceAndPath(SimpleStorage.MODID, "crate_upgrade"));

//        crateRecipe(1, output);
//        crateRecipe(2, output);
//        crateRecipe(3, output);
//        crateRecipe(4, output);

//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, crate_t2)
//                .requires(Items.CHEST).requires(Items.DIAMOND).requires(Items.REDSTONE)
//                .unlockedBy("has_chest", has(Items.CHEST))
//                .save(output, ResourceLocation.fromNamespaceAndPath(SimpleStorage.MODID, "crate2"));



    }

    private void crateRecipe(int i, RecipeOutput output) {
        ItemStack crate_in = Registration.CRATE_ITEM.get().getWithTier(i-1);
        ItemStack crate_out = Registration.CRATE_ITEM.get().getWithTier(i);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, crate_out)
                .requires(Ingredient.of(crate_in)).requires(Registration.STORAGE_COMPARTMENT_ITEM.get())
                .unlockedBy("has_chest", has(Items.CHEST))
                .save(output, ResourceLocation.fromNamespaceAndPath(SimpleStorage.MODID, "crate"+i));
    }
}