package vapourdrive.simplestorage;

import net.minecraft.creativetab.CreativeTabs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import vapourdrive.simplestorage.blocks.SS_Blocks;
import vapourdrive.simplestorage.creativetab.SSCreativeTab;
import vapourdrive.simplestorage.items.SS_Items;
import vapourdrive.simplestorage.proxies.CommonProxy;
import vapourdrive.simplestorage.recipes.Recipes;
import vapourdrive.simplestorage.tileentities.SS_TileEntities;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = Reference.ModID, version = Reference.Version, name = Reference.Name)
public class SimpleStorage
{
	@Instance(Reference.ModID)
	public static SimpleStorage Instance;
	
	@SidedProxy(clientSide = "vapourdrive.simplestorage.proxies.ClientProxy", serverSide = "vapourdrive.simplestorage.proxies.CommonProxy")
	public static CommonProxy proxy;
	public static CreativeTabs SSTab;
	public static final Logger log = LogManager.getLogger(Reference.ModID);
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		SSTab = new SSCreativeTab(CreativeTabs.getNextID(), "SS_CreativeTab");
		NetworkRegistry.INSTANCE.registerGuiHandler(Instance, new GuiHandler());
		SS_Blocks.init();
		SS_Items.init();
		Recipes.init();
		
		SS_TileEntities.init();
	}
	
    @EventHandler
    public void init(FMLInitializationEvent evt)
    {
        proxy.registerRenderInformation();
        proxy.registerTileEntitySpecialRenderer();
    }
}
