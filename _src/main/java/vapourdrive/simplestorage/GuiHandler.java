package vapourdrive.simplestorage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import vapourdrive.simplestorage.client.gui.GuiExpandableChest;
import vapourdrive.simplestorage.inventory.ContainerExpandableChest;
import vapourdrive.simplestorage.tileentities.TileEntityExpandableChest;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	public enum GUIIDs
	{
		EXPANDABLE_CHEST;
	}
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch(GUIIDs.values()[ID])
		{
			case EXPANDABLE_CHEST:
			{
				return new ContainerExpandableChest(player.inventory, (TileEntityExpandableChest)world.getTileEntity(x, y, z));
			}
		}
		throw new IllegalArgumentException("NO GUI WITH ID: " + ID);
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch(GUIIDs.values()[ID])
		{
			case EXPANDABLE_CHEST:
			{
				return new GuiExpandableChest(player.inventory, (TileEntityExpandableChest)world.getTileEntity(x, y, z));
			}
		}
		throw new IllegalArgumentException("NO GUI WITH ID: " + ID);
	}

}
