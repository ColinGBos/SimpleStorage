package vapourdrive.simplestorage.data.recipes;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.setup.Registration;

import javax.annotation.Nullable;

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

//    @Override
//    public @NotNull RecipeSerializer<?> getSerializer() {
//        return Serializer.INSTANCE;
//    }
//
//    @Override
//    public @NotNull RecipeType<?> getType() {
//        return Type.INSTANCE;
//    }
//
//    public static class Type implements RecipeType<CrateRecipe> {
//        public static final Type INSTANCE = new Type();
//        @SuppressWarnings("unused")
//        public static final String ID = "fertilizer";
//
//        private Type() {
//        }
//    }
//
//    public static class Serializer implements RecipeSerializer<CrateRecipe> {
//        public static final Serializer INSTANCE = new Serializer();
//        @SuppressWarnings("unused")
////        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(AgriculturalEnhancements.MODID, "fertilizer");
//        public static final StreamCodec<RegistryFriendlyByteBuf, CrateRecipe> STREAM_CODEC = StreamCodec.of(CrateRecipe.Serializer::toNetwork, CrateRecipe.Serializer::fromNetwork);
//
//        private static final MapCodec<CrateRecipe> CODEC = RecordCodecBuilder.mapCodec((builder) -> {
//            return builder.group(Ingredient.CODEC.fieldOf("ingredient").forGetter((fertilizerRecipe) -> {
//                return fertilizerRecipe.ingredient;
//            }), Codec.INT.fieldOf("n").forGetter((fertilizerRecipe) -> {
//                return fertilizerRecipe.n;
//            }), Codec.INT.fieldOf("p").forGetter((fertilizerRecipe) -> {
//                return fertilizerRecipe.p;
//            }), Codec.INT.fieldOf("k").forGetter((fertilizerRecipe) -> {
//                return fertilizerRecipe.k;
//            })).apply(builder, CrateRecipe::new);
//        });
//
//
//        private static CrateRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
//            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
//            int[] results = buffer.readVarIntArray();
//            int n = results[0];
//            int p = results[1];
//            int k = results[2];
//            return new CrateRecipe(ingredient, n, p, k);
//        }
//
//        private static void toNetwork(RegistryFriendlyByteBuf buffer, CrateRecipe recipe) {
//            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
//            buffer.writeVarIntArray(new int[]{recipe.n, recipe.p, recipe.k});
//        }
//
//        @Override
//        public @NotNull MapCodec<CrateRecipe> codec() {
//            return CODEC;
//        }
//
//        @Override
//        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CrateRecipe> streamCodec() {
//            return STREAM_CODEC;
//        }
//    }
}
