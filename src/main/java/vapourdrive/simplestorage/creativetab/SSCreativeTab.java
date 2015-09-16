package vapourdrive.simplestorage.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import vapourdrive.simplestorage.blocks.SS_Blocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SSCreativeTab extends CreativeTabs
{

	public SSCreativeTab(int id, String name)
	{
		super(id, name);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getTabLabel()
	{
		return "SimpleStorage";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel()
	{
		return "Simple Storage";
	}

	@Override
	public Item getTabIconItem()
	{
		return Item.getItemFromBlock(SS_Blocks.BlockChest);
	}

}
