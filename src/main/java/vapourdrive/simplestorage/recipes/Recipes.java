package vapourdrive.simplestorage.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import vapourdrive.simplestorage.blocks.SS_Blocks;
import vapourdrive.simplestorage.items.SS_Items;
import cpw.mods.fml.common.registry.GameRegistry;

public class Recipes
{

	public static void init()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(SS_Items.upgrade, 2), new Object[]
		{
				"sps", "pcp", "sps", 's', "stickWood", 'c', "chestWood", 'p', Blocks.piston
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(SS_Blocks.BlockChest), new Object[]
		{
				"cuc", 'u', SS_Items.upgrade, 'c', "chestWood",
		}));

	}

}
