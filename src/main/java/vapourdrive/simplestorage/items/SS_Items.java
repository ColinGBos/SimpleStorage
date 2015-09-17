package vapourdrive.simplestorage.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class SS_Items
{
	public static Item upgrade;
	public static Item chestConverter;
	
	public static void init()
	{
		upgrade = new ItemUpgrade();
		chestConverter = new ChestConverter();
		
		GameRegistry.registerItem(upgrade, "ItemUpgrade");
		GameRegistry.registerItem(chestConverter, "ChestConverter");
	}
}
