package vapourdrive.simplestorage.utils;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RandomUtils
{
	/**
	 * Called to spawn an itemstack in the world Randomly generates location
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param stack
	 * @param multiplyer
	 */
	public static void spawnItem(World world, int x, int y, int z, ItemStack stack, Float multiplyer)
	{
		if (!world.isRemote)
		{
			double spawnX = (double) (world.rand.nextFloat() * multiplyer) + (double) (1.0F - multiplyer) * 0.5D;
			double spawnY = (double) (world.rand.nextFloat() * multiplyer) + (double) (1.0F - multiplyer) * 0.5D;
			double spawnZ = (double) (world.rand.nextFloat() * multiplyer) + (double) (1.0F - multiplyer) * 0.5D;

			EntityItem entityitem = new EntityItem(world, (double) x + spawnX, (double) y + spawnY, (double) z + spawnZ, stack);
			entityitem.delayBeforeCanPickup = 10;

			world.spawnEntityInWorld(entityitem);
		}
	}
}
