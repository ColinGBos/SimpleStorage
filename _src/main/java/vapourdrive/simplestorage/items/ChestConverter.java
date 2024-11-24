package vapourdrive.simplestorage.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import vapourdrive.simplestorage.Reference;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.blocks.SS_Blocks;
import vapourdrive.simplestorage.tileentities.TileEntityExpandableChest;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChestConverter extends Item
{
	public ChestConverter()
	{
		this.setCreativeTab(SimpleStorage.SSTab);
		this.setUnlocalizedName("ChestConverter");
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int meta, float fx, float fy, float fz)
	{
		ItemStack[] Inventory = new ItemStack[36];
		TileEntity tile = world.getTileEntity(x, y, z);

		if (world.isRemote || tile == null || !(tile instanceof TileEntityChest))
		{
			return true;
		}

		TileEntityChest chest = (TileEntityChest) tile;

		for (int i = 0; i < chest.getSizeInventory(); i++)
		{
			Inventory[i] = chest.getStackInSlot(i);
		}

		world.removeTileEntity(x, y, z);
		int blockMeta = world.getBlockMetadata(x, y, z);
		int expChestMeta = 0;
		switch (blockMeta)
		{
			case 2:
				expChestMeta = 0;
				break;
			case 5:
				expChestMeta = 1;
				break;
			case 3:
				expChestMeta = 2;
				break;
			case 4:
				expChestMeta = 3;
				break;
			default:
				expChestMeta = 0;
		}

		world.setBlock(x, y, z, SS_Blocks.BlockChest, expChestMeta, 3);
		player.inventory.consumeInventoryItem(SS_Items.chestConverter);
        double d2 = (double) x + 0.5D;
        double d3 = (double) z + 0.5D;
        world.playSoundEffect(d2, (double) y + 0.5D, d3, "step.wood", 0.4F, world.rand.nextFloat() * 0.1F + 0.8F);

		tile = world.getTileEntity(x, y, z);

		if (tile == null || !(tile instanceof TileEntityExpandableChest))
		{
			return true;
		}
		TileEntityExpandableChest expandableChest = (TileEntityExpandableChest) tile;
		for (int i = 0; i < 36; i++)
		{
			expandableChest.setInventorySlotContents(i + 4, Inventory[i]);
		}

		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		itemIcon = register.registerIcon(Reference.ResourcePath + "ChestConverter");
	}
}
