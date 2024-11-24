package vapourdrive.simplestorage.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public abstract class ContainerSimpleStorage extends Container
{
	protected void addPlayerSlots(InventoryPlayer invPlayer, int x, int y)
	{
		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, x + j * 18, y + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i)
		{
			addSlotToContainer(new Slot(invPlayer, i, x + i * 18, y + 58));
		}
	}


}
