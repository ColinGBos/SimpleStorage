package vapourdrive.simplestorage.integrations.emi;

import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ShapelessCraftingRecipe extends EmiCraftingRecipe {
    public ShapelessCraftingRecipe(List<EmiIngredient> input, EmiStack output, ResourceLocation id) {
        super(input, output, id, true);
    }

    @Override
    public boolean canFit(int width, int height) {
        return input.size() <= width * height;
    }
}
