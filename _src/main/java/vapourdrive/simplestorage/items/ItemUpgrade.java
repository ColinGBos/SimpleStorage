package vapourdrive.simplestorage.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vapourdrive.simplestorage.Reference;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.tileentities.TileEntityExpandableChest;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemUpgrade extends Item
{
	public ItemUpgrade()
	{
		this.setCreativeTab(SimpleStorage.SSTab);
		this.setUnlocalizedName("ItemUpgrade");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		itemIcon = register.registerIcon(Reference.ResourcePath + "ItemUpgrade");
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int meta, float floatx, float floaty,
			float floatz)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if(world.isRemote || tile == null || !(tile instanceof TileEntityExpandableChest))
		{
			return true;
		}
		TileEntityExpandableChest chest = (TileEntityExpandableChest) tile;
		if(chest.getUserNumber() < 0)
		{
			return true;
		}

		if(stack != null)
		{
			if (stack.getItem() == SS_Items.upgrade)
			{
				for (int i = 0; i <= 3; i++)
				{
					ItemStack chestStack = chest.getStackInSlot(i);
					if (chestStack == null)
					{
						chest.setInventorySlotContents(i, new ItemStack(SS_Items.upgrade));
						player.inventory.consumeInventoryItem(SS_Items.upgrade);
				        double d2 = (double) x + 0.5D;
				        double d3 = (double) z + 0.5D;
			            world.playSoundEffect(d2, (double) y + 0.5D, d3, "step.wood", 0.4F, world.rand.nextFloat() * 0.2F + 1.0F);
						i = 4;
					}
				}
			}
		}
		return true;
	}
}
