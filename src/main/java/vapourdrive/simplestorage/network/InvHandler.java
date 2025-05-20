package vapourdrive.simplestorage.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import vapourdrive.simplestorage.content.crate.CrateTile;

public class InvHandler
{
    static void sort(final Network.SortNotification data, IPayloadContext ctx)
    {
        final Player sender = ctx.player();
        if (sender instanceof ServerPlayer serverPlayer) {
            ctx.enqueueWork(() -> {
                BlockEntity tileEntity = serverPlayer.level().getBlockEntity(data.blockPos());
                if (tileEntity instanceof CrateTile crate) {
                    crate.sortContents();
                }
            });
        }
    }
    static void transfer(final Network.TransferNotification data, IPayloadContext ctx)
    {
        final Player sender = ctx.player();
        if (sender instanceof ServerPlayer serverPlayer) {
            ctx.enqueueWork(() -> {
                BlockEntity tileEntity = serverPlayer.level().getBlockEntity(data.blockPos());
                if (tileEntity instanceof CrateTile crate) {
                    crate.transferContents(serverPlayer, data.means());
                }
            });
        }
    }
}