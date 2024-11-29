package vapourdrive.simplestorage.content.crate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.setup.Registration;
import vapourdrive.vapourware.shared.base.BaseInfoItemBlock;
import vapourdrive.vapourware.shared.utils.CompUtils;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

import javax.annotation.Nullable;
import java.util.List;

public class CrateItem extends BaseInfoItemBlock {
    public CrateItem(Block block, Properties properties) {
        super(block, properties, new DeferredComponent(SimpleStorage.MODID, "crate.info"));
    }

    public static float getTier(ItemStack stack) {
        return stack.getOrDefault(Registration.TIER_DATA, 0);
    }
    public static float getVariant(ItemStack stack) {
        return stack.getOrDefault(Registration.VARIANT_DATA, 0);
    }

    public @NotNull Component getName(@NotNull ItemStack stack) {
//        return Component.translatable(this.getDescriptionId(stack)+"_"+(int)getTier(stack));
        return CompUtils.getArgComp(SimpleStorage.MODID, "crate.tier", (int)getTier(stack));
    }

    @Override
    protected boolean updateCustomBlockEntityTag(@NotNull BlockPos pos, Level level, @Nullable Player player, @NotNull ItemStack stack, @NotNull BlockState state) {
        MinecraftServer minecraftserver = level.getServer();
        if (minecraftserver == null) {
            return false;
        }
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity == null || !level.isClientSide && blockentity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks())) {
            return false;
        }

        if (blockentity instanceof CrateTile crate) {
            int tier = stack.getOrDefault(Registration.TIER_DATA, 0);
            int variant = stack.getOrDefault(Registration.VARIANT_DATA, 0);
            if(variant==5){
                crate.setBlessed(true);
            } else {
                boolean isBlessed = state.getValue(CrateBlock.VARIANT)==5;
                if(isBlessed){
                    crate.setBlessed(true);
                }
            }
//            state.setValue(CrateBlock.TIER, tier);
            crate.setTier(tier);


            int i = 0;
            for (ItemStack itemstack : stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).stream().toList()) {
                crate.getItemHandler(null).insertItem(i, itemstack, false);
                i++;
            }

        }

        return true;
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(stack.has(Registration.VARIANT_DATA)) {
            tooltipComponents.add(Component.literal("Variant: " + stack.getOrDefault(Registration.VARIANT_DATA, 0)));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

}
