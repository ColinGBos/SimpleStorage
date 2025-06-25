package vapourdrive.simplestorage.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.content.crate.CrateTile;
import vapourdrive.vapourware.shared.base.BaseInfoItem;
import vapourdrive.vapourware.shared.utils.DeferredComponent;
import vapourdrive.vapourware.shared.utils.InvUtils;

import java.util.Objects;

public class StorageCompartmentItem extends BaseInfoItem {
    public StorageCompartmentItem(Properties properties, DeferredComponent component) {
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
                if (crate.getTier() < 4) {
                    NonNullList<ItemStack> stacks = InvUtils.getIngredientsFromInvHandler(crate.getItemHandler(null));
                    level.playSound(player, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f,1f-(0.06f*(crate.getTier()+1)));
                    BlockState state = crate.setTierAndState(crate.getTier() + 1, level, pos);
                    level.sendBlockUpdated(pos, state,state,1);
                    for (int i = 0; i < stacks.size(); i++) {
                        crate.getItemHandler(null).insertItem(i, stacks.get(i), false);
                    }
                    context.getItemInHand().consume(1, player);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
