package vapourdrive.simplestorage.tileentities;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.registry.GameRegistry;
import vapourdrive.simplestorage.SimpleStorage;

public class SS_TileEntities
{

	public static void init()
	{
		SimpleStorage.log.log(Level.INFO, "Registering Tile Entities");
		GameRegistry.registerTileEntity(TileEntityExpandableChest.class, "ExpandableChestTile");
	}

}
