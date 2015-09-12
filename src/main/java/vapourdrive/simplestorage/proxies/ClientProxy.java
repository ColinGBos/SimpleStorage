package vapourdrive.simplestorage.proxies;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import vapourdrive.simplestorage.blocks.SS_Blocks;
import vapourdrive.simplestorage.client.render.ItemExpandableChestRenderer;
import vapourdrive.simplestorage.client.render.TileEntityExpandableChestRenderer;
import vapourdrive.simplestorage.client.render.TileEntityRendererExpandableChestHelper;
import vapourdrive.simplestorage.tileentities.TileEntityExpandableChest;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderInformation()
    {
        TileEntityRendererExpandableChestHelper.instance = new TileEntityRendererExpandableChestHelper();
    }

    @Override
    public void registerTileEntitySpecialRenderer()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExpandableChest.class, new TileEntityExpandableChestRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(SS_Blocks.BlockChest), new ItemExpandableChestRenderer());
    }
}
