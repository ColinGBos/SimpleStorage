package vapourdrive.simplestorage.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class SSCreativeTab extends CreativeTabs
{

	public SSCreativeTab(int id, String name)
	{
		super(id, name);
	}

	@Override
	public Item getTabIconItem()
	{
		return Items.apple;
	}

}
