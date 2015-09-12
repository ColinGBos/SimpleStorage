package vapourdrive.simplestorage.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.items.SS_Items;
import vapourdrive.simplestorage.tileentities.TileEntityExpandableChest;
import vapourdrive.simplestorage.utils.RandomUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockExpandableChest extends BlockContainer
{

	public BlockExpandableChest()
	{
		super(Material.wood);
		this.setStepSound(soundTypeWood);
		this.setBlockName("BlockExpandableChest");
		this.setHardness(1.0f);
		this.setCreativeTab(SimpleStorage.SSTab);
		this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int id)
	{
		return new TileEntityExpandableChest();
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float hitx, float hity, float hitz)
	{

		TileEntity te = world.getTileEntity(x, y, z);

		if (te == null || !(te instanceof TileEntityExpandableChest))
		{
			return true;
		}
		TileEntityExpandableChest chest = (TileEntityExpandableChest) te;

		if (!player.isSneaking())
		{
			if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.stick && !world.isRemote)
			{
				SimpleStorage.log.log(Level.INFO, chest.getSizeInventory());
				return true;
			}
			else
			{
				return handleOpen(world, x, y, z, player);
			}
		}
		else
		{
			return handleRemoval(chest, world, x, y, z, player);
		}
	}

	public boolean handleRemoval(TileEntityExpandableChest chest, World world, int x, int y, int z, EntityPlayer player)
	{
		ItemStack playerStack = player.getCurrentEquippedItem();
		if (playerStack == null)
		{
			if (chest.canTakeStack(player))
			{
				for (int i = 3; i >= 0; i--)
				{
					ItemStack chestStack = chest.getStackInSlot(i);
					if (chestStack != null && chestStack.getItem() == SS_Items.upgrade)
					{
						chest.setInventorySlotContents(i, null);
						i = -1;
						player.inventory.addItemStackToInventory(new ItemStack(SS_Items.upgrade));
						double d2 = (double) x + 0.5D;
						double d3 = (double) z + 0.5D;
						world.playSoundEffect(d2, (double) y + 0.5D, d3, "step.wood", 0.4F, world.rand.nextFloat() * 0.2F + 2.0F);
					}
				}
			}
		}

		return true;
	}

	public boolean handleOpen(World world, int x, int y, int z, EntityPlayer player)
	{
		if (world.isRemote || world.isSideSolid(x, y + 1, z, ForgeDirection.DOWN))
		{
			return true;
		}
		else
		{
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof IInventory)
			{
				player.openGui(SimpleStorage.Instance, 0, world, x, y, z);
			}

			return true;
		}
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k)
	{
		super.onBlockAdded(world, i, j, k);
		world.markBlockForUpdate(i, j, k);
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack itemStack)
	{
		int facing = MathHelper.floor_double((double) ((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		world.setBlockMetadataWithNotify(i, j, k, facing, 3);
		TileEntity te = world.getTileEntity(i, j, k);
		if (te != null && te instanceof TileEntityExpandableChest)
		{
			world.markBlockForUpdate(i, j, k);
		}
	}

	@Override
	public int damageDropped(int i)
	{
		return i;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null && te instanceof IInventory)
		{
			IInventory inv = (IInventory) te;
			for (int i = 0; i < inv.getSizeInventory(); i++)
			{
				ItemStack stack = inv.getStackInSlotOnClosing(i);

				if (stack != null)
				{
					RandomUtils.spawnItem(world, x, y, z, stack, 0.7F);
				}
			}
		}

		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False
	 * (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	/**
	 * The type of render function that is called for this block
	 */
	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis)
	{
		if (worldObj.isRemote)
		{
			return false;
		}
		if (axis == ForgeDirection.UP || axis == ForgeDirection.DOWN)
		{
			TileEntity tileEntity = worldObj.getTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityExpandableChest)
			{
				int meta = worldObj.getBlockMetadata(x, y, z);
				if (meta == 3)
				{
					meta = 0;
				}
				else
					meta++;
			}
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister icon)
	{
		this.blockIcon = icon.registerIcon("planks_big_oak");
	}

}
