package vapourdrive.simplestorage.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import vapourdrive.simplestorage.tileentities.TileEntityExpandableChest;

public class TileEntityRendererExpandableChestHelper
{
    TileEntityExpandableChest tile = new TileEntityExpandableChest();
    public static TileEntityRendererExpandableChestHelper instance = new TileEntityRendererExpandableChestHelper();

    public void renderChest(Block block, int i, float f)
    {
       TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, 0.0D, 0.0D, 0.0D, 0.0F);
    }
}
