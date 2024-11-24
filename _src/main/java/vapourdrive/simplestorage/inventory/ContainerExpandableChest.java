package vapourdrive.simplestorage.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import vapourdrive.simplestorage.inventory.slots.SlotSearchable;
import vapourdrive.simplestorage.inventory.slots.UpgradeSlot;
import vapourdrive.simplestorage.tileentities.TileEntityExpandableChest;

public class ContainerExpandableChest extends ContainerSimpleStorage
{
	private final TileEntityExpandableChest tile;

	public ContainerExpandableChest(InventoryPlayer invPlayer, TileEntityExpandableChest tileEntity)
	{
		this.tile = tileEntity;
		tile.openInventory();
		this.addSlots(invPlayer, tileEntity);
		this.addPlayerSlots(invPlayer, 31, 134);
	}

	public void addSlots(InventoryPlayer player, TileEntityExpandableChest tileEntity)
	{
		int i = 0;
		int j = 0;
		int k = 0;
		for (k = 0; k < 4; k++)
		{
			this.addSlotToContainer(new UpgradeSlot(tileEntity, (k), 9, (k * 18) + 18));
		}
		for (i = 0; i < 14; ++i)
		{
			for (j = 0; j < 9; ++j)
			{
				if (i < 6)
				{
					this.addSlotToContainer(new SlotSearchable(tileEntity, i * 9 + j + k, (j * 18) + 31, (i * 18) + 18));
				}
				else
				{
					this.addSlotToContainer(new SlotSearchable(tileEntity, i * 9 + j + k, -1000, -1000));
				}
			}
		}

	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return tile.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int number)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(number);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (number < ((6 + tile.getAddedRows()) * 9) + 4)
			{
				if (!this.mergeItemStack(itemstack1, ((6 + tile.getAddedRows()) * 9) + 4, this.inventorySlots.size(), true))
				{
					tile.checkUpgrades();
					this.detectAndSendChanges();
					return null;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 4, ((6 + tile.getAddedRows()) * 9) + 4, false))
			{
				return null;
			}

			if (itemstack1.stackSize == 0)
			{
				tile.checkUpgrades();
				this.detectAndSendChanges();
				slot.putStack((ItemStack) null);
			}
			else
			{
				tile.checkUpgrades();
				this.detectAndSendChanges();
				slot.onSlotChanged();
			}
		}

		tile.checkUpgrades();
		this.detectAndSendChanges();
		return itemstack;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		this.tile.closeInventory();
	}

	@Override
	public void detectAndSendChanges()
	{
		for (int i = 0; i < this.inventorySlots.size(); ++i)
		{
			ItemStack itemstack = ((Slot) this.inventorySlots.get(i)).getStack();
			ItemStack itemstack1 = (ItemStack) this.inventoryItemStacks.get(i);

			if (!ItemStack.areItemStacksEqual(itemstack1, itemstack))
			{
				itemstack1 = itemstack == null ? null : itemstack.copy();
				this.inventoryItemStacks.set(i, itemstack1);

				for (int j = 0; j < this.crafters.size(); ++j)
				{
					((ICrafting) this.crafters.get(j)).sendSlotContents(this, i, itemstack1);
				}
			}
		}
	}

}
