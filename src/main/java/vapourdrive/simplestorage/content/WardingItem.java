package vapourdrive.simplestorage.content;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.content.crate.CrateTile;
import vapourdrive.vapourware.shared.base.BaseInfoItem;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

import java.util.Objects;

public class WardingItem extends BaseInfoItem {
    public WardingItem(Properties properties, DeferredComponent component) {
        super(properties, component);
    }


    @Override
    public @NotNull InteractionResult onItemUseFirst(@NotNull ItemStack stack, @NotNull UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (Objects.requireNonNull(player).isCrouching()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof CrateTile crate) {
                if (!crate.getIsWarded()) {
                    level.playSound(player, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 0.8f,1f-(0.06f*(crate.getTier()+1)));
                    crate.setWardedStatus(true);
                    context.getItemInHand().consume(1, context.getPlayer());
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
