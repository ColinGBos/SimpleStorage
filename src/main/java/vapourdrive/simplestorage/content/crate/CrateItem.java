package vapourdrive.simplestorage.content.crate;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import vapourdrive.vapourware.shared.utils.WeightedRandom;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class CrateItem extends BaseInfoItemBlock {
    static Map<Integer, Double> variantWeights = Map.ofEntries(
            Map.entry(0, 0.4),
            Map.entry(1, 0.6),
            Map.entry(2, 1.0),
            Map.entry(3, 0.4),
            Map.entry(4, 0.6),
            Map.entry(5, 0.05)
    );
    public static final WeightedRandom<Integer> numberGenerator = new WeightedRandom<>(variantWeights);
    public CrateItem(Block block, Properties properties) {
        super(block, properties, new DeferredComponent(SimpleStorage.MODID, "crate.info"));
    }

    public static float getTier(ItemStack stack) {
        return stack.getOrDefault(Registration.TIER_DATA, 0);
    }
    public static float getVariant(ItemStack stack) {
        return stack.getOrDefault(Registration.VARIANT_DATA, 0);
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
            int variant;
            if(stack.has(Registration.VARIANT_DATA)) {
                variant = stack.getOrDefault(Registration.VARIANT_DATA, 0);
            } else {
                variant = numberGenerator.nextRandomItem();
            }
            boolean warded = stack.has(DataComponents.CONTAINER);
            if(warded || variant==5 || state.getValue(CrateBlock.VARIANT)==5){
                crate.setWardedStatus(true);
            }
//            state.setValue(CrateBlock.TIER, tier);
            crate.setTier(tier);
            crate.setVariant(variant);
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(CrateBlock.VARIANT, variant));

            int i = 0;
            for (ItemStack itemstack : stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).stream().toList()) {
                crate.getItemHandler(null).insertItem(i, itemstack, false);
                i++;
            }

        }

        return true;
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        MutableComponent tooltip = Component.literal("Tier: " + stack.getOrDefault(Registration.TIER_DATA, 0));
        if(stack.has(Registration.VARIANT_DATA)) {
            tooltip.append(" | Variant: " + stack.getOrDefault(Registration.VARIANT_DATA, 0));
        }
        boolean warded = stack.has(DataComponents.CONTAINER);
        if (warded){
            tooltip.append(" | ").append(Component.literal("Warded").withStyle(ChatFormatting.DARK_PURPLE));
        }
        tooltipComponents.add(tooltip);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public ItemStack getWithTier(int tier) {
        ItemStack stack = this.getDefaultInstance();
        stack.set(Registration.TIER_DATA, tier);
        return stack;
    }
}
