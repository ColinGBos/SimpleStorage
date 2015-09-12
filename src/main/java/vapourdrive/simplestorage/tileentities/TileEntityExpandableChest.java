package vapourdrive.simplestorage.tileentities;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import vapourdrive.simplestorage.blocks.SS_Blocks;
import vapourdrive.simplestorage.inventory.ContainerExpandableChest;
import vapourdrive.simplestorage.items.SS_Items;

public class TileEntityExpandableChest extends TileEntity implements ISidedInventory
{
	private int invSize = 58;
	private int addedRows = 0;
	public ItemStack[] stacks = new ItemStack[130];
	private int ticksSinceSync = -1;
	public float prevLidAngle;
	public float lidAngle;
	private int numUsingPlayers;
	public int dir;

	public TileEntityExpandableChest()
	{
	}

	public void setAddedRows(int rows)
	{
		this.addedRows = rows;
	}

	public int getAddedRows()
	{
		return this.addedRows;
	}

	@Override
	public int getSizeInventory()
	{
		return invSize;
	}
	
	public void setSizeInventory(int inv)
	{
		this.invSize = inv;
	}

	@Override
	public ItemStack getStackInSlot(int id)
	{
		return this.stacks[id];
	}

	public ItemStack decrStackSize(int prevSize, int decr)
	{
		if (this.stacks[prevSize] != null)
		{
			ItemStack itemstack;

			if (this.stacks[prevSize].stackSize <= decr)
			{
				itemstack = this.stacks[prevSize];
				this.stacks[prevSize] = null;
				this.markDirty();
				this.checkUpgrades();
				return itemstack;
			}
			else
			{
				itemstack = this.stacks[prevSize].splitStack(decr);

				if (this.stacks[prevSize].stackSize == 0)
				{
					this.stacks[prevSize] = null;
				}

				this.markDirty();
				this.checkUpgrades();
				return itemstack;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int id)
	{
		if (this.stacks[id] != null)
		{
			ItemStack itemstack = this.stacks[id];
			this.stacks[id] = null;
			checkUpgrades();
			return itemstack;
		}
		else
		{
			checkUpgrades();
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int id, ItemStack stack)
	{
		this.stacks[id] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
		checkUpgrades();
	}

	@Override
	public String getInventoryName()
	{
		return null;
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
	{
		if (worldObj == null)
		{
			return true;
		}
		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this)
		{
			return false;
		}
		return entityplayer.getDistanceSq((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 64D;
	}

	@Override
	public void openInventory()
	{
        if (this.numUsingPlayers < 0)
        {
            this.numUsingPlayers = 0;
        }

        ++this.numUsingPlayers;

		worldObj.addBlockEvent(xCoord, yCoord, zCoord, SS_Blocks.BlockChest, 1, numUsingPlayers);
		checkUpgrades();
	}

	@Override
	public void closeInventory()
	{
       if (worldObj == null)
		{
			return;
		}
		numUsingPlayers--;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, SS_Blocks.BlockChest, 1, numUsingPlayers);
		checkUpgrades();
	}

	@Override
	public boolean isItemValidForSlot(int id, ItemStack stack)
	{
		if (id < 4)
		{
			return false;
		}
		return true;
	}

	public void checkUpgrades()
	{
		int addedrows = 0;
		for (int i = 0; i < 4; i++)
		{
			if (this.stacks[i] != null && this.stacks[i].getItem() != null)
			{
				if (this.stacks[i].getItem() == SS_Items.upgrade)
				{
					addedrows = addedrows + 2;
				}
			}
		}

		this.setAddedRows(addedrows);
		this.setSizeInventory(58 + (this.getAddedRows() * 9));

	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		NBTTagList nbttaglist = tag.getTagList("Items", 10);
		
		this.setSizeInventory(tag.getInteger("invSize"));
		this.setAddedRows(tag.getInteger("addedRows"));
		
		this.stacks = new ItemStack[130];

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < this.stacks.length)
			{
				this.stacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.stacks.length; ++i)
		{
			if (this.stacks[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.stacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		tag.setTag("Items", nbttaglist);
		tag.setInteger("invSize", this.invSize);
		tag.setInteger("addedRows", this.addedRows);
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		this.dir = this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		++this.ticksSinceSync;
        float var1;

        if (worldObj != null && !this.worldObj.isRemote && this.numUsingPlayers != 0 && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200 == 0)
        {
            this.numUsingPlayers = 0;
            var1 = 5.0F;
            @SuppressWarnings("unchecked")
            List<EntityPlayer> var2 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox((double)((float)this.xCoord - var1), (double)((float)this.yCoord - var1), (double)((float)this.zCoord - var1), (double)((float)(this.xCoord + 1) + var1), (double)((float)(this.yCoord + 1) + var1), (double)((float)(this.zCoord + 1) + var1)));

            for (EntityPlayer var4 : var2) {
                if (var4.openContainer instanceof ContainerExpandableChest) {
                    ++this.numUsingPlayers;
                }
            }
        }
        
        if (worldObj != null && !worldObj.isRemote && ticksSinceSync < 0)
        {
            worldObj.addBlockEvent(xCoord, yCoord, zCoord, SS_Blocks.BlockChest, 3, ((numUsingPlayers << 3) & 0xF8) | (dir & 0x7));
        }

        this.ticksSinceSync++;
        prevLidAngle = lidAngle;
        float f = 0.1F;
        if (numUsingPlayers > 0 && lidAngle == 0.0F)
        {
            double d = (double) xCoord + 0.5D;
            double d1 = (double) zCoord + 0.5D;
            worldObj.playSoundEffect(d, (double) yCoord + 0.5D, d1, "random.chestopen", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }
        if (numUsingPlayers == 0 && lidAngle > 0.0F || numUsingPlayers > 0 && lidAngle < 1.0F)
        {
            float f1 = lidAngle;
            if (numUsingPlayers > 0)
            {
                lidAngle += f;
            }
            else
            {
                lidAngle -= f;
            }
            if (lidAngle > 1.0F)
            {
                lidAngle = 1.0F;
            }
            float f2 = 0.5F;
            if (lidAngle < f2 && f1 >= f2)
            {
                double d2 = (double) xCoord + 0.5D;
                double d3 = (double) zCoord + 0.5D;
                worldObj.playSoundEffect(d2, (double) yCoord + 0.5D, d3, "random.chestclosed", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }
            if (lidAngle < 0.0F)
            {
                lidAngle = 0.0F;
            }
        }
	}


    @Override
    public boolean receiveClientEvent(int i, int j)
    {
        if (i == 1)
        {
            numUsingPlayers = j;
        }
        else if (i == 2)
        {
            dir = (byte) j;
        }
        else if (i == 3)
        {
            dir = (byte) (j & 0x7);
            numUsingPlayers = (j & 0xF8) >> 3;
        }
        return true;
    }
    
	public boolean canTakeStack(EntityPlayer player)
	{
		int numUpgrades = 0;
		for (int i = 0; i < 4; i++)
		{
			if (stacks[i] != null)
			{
				numUpgrades++;
			}
		}
		if (numUpgrades > 0)
		{
			for (int i = 58 + (18 * (numUpgrades - 1)); i < (58 + (18 * (numUpgrades))); i++)
			{
				if(stacks[i] != null)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public int getUserNumber()
	{
		return this.numUsingPlayers;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		int[] slots = new int[this.getSizeInventory()];
		for(int i = 0; i < this.getSizeInventory() - 4; i++)
		{
			slots[i] = i + 4;
		}
		return slots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side)
	{
		if(slot > 3)
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side)
	{
		if(slot > 3)
		{
			return true;
		}
		return false;
	}

}
