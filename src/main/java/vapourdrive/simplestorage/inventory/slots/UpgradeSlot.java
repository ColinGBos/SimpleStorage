package vapourdrive.simplestorage.inventory.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import vapourdrive.simplestorage.tileentities.TileEntityExpandableChest;

public class UpgradeSlot extends Slot
{
	public TileEntityExpandableChest chest;
	
	public UpgradeSlot(IInventory IInv, int index, int xPos, int yPos)
	{
		super(IInv, index, xPos, yPos);
		if(IInv instanceof TileEntityExpandableChest)
		{
			chest = (TileEntityExpandableChest) IInv;
		}
	}

	public boolean isItemValid(ItemStack stack)
	{
		return false;
	}

	public boolean canTakeStack(EntityPlayer player)
	{
		return false;
	}

}
