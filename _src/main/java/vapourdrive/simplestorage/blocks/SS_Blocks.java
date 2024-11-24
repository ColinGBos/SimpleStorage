package vapourdrive.simplestorage.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class SS_Blocks
{
	public static Block BlockChest;

	public static void init()
	{
		BlockChest = new BlockExpandableChest();
		
		GameRegistry.registerBlock(BlockChest, "BlockExpandingChest");
	}
}
