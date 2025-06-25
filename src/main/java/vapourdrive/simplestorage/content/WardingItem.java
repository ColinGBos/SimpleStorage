package vapourdrive.simplestorage.content;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
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
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if(!context.getLevel().isClientSide()) {
            if (Objects.requireNonNull(context.getPlayer()).isCrouching()) {
                BlockEntity tileEntity = context.getLevel().getBlockEntity(context.getClickedPos());
                if (Objects.requireNonNull(context.getPlayer()).isCrouching() && tileEntity instanceof CrateTile crate) {
                    if (!crate.getIsWarded()) {
                        crate.setWardedStatus(true);
                        context.getItemInHand().consume(1, context.getPlayer());
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }
}
