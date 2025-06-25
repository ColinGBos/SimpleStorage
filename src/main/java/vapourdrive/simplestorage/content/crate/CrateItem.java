package vapourdrive.simplestorage.content.crate;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.config.ConfigSettings;
import vapourdrive.simplestorage.setup.Registration;
import vapourdrive.vapourware.shared.base.BaseInfoItemBlock;
import vapourdrive.vapourware.shared.utils.CompUtils;
import vapourdrive.vapourware.shared.utils.DeferredComponent;
import vapourdrive.vapourware.shared.utils.WeightedRandom;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if(!ConfigSettings.ENABLE_CHEST_SWAP.get() || tryConvertChest(context) == InteractionResult.PASS) {
            InteractionResult interactionresult = this.place(new BlockPlaceContext(context));
            if (!interactionresult.consumesAction() && context.getItemInHand().has(DataComponents.FOOD)) {
                InteractionResult interactionresult1 = super.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
                return interactionresult1 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : interactionresult1;
            } else {
                return interactionresult;
            }
        }
        return InteractionResult.PASS;
    }

    public @NotNull InteractionResult tryConvertChest(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (Objects.requireNonNull(player).isCrouching()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof ChestBlockEntity chest) {
                SimpleStorage.debugLog("Chest size: "+chest.getContainerSize());
                int tier = context.getItemInHand().getOrDefault(Registration.TIER_DATA,0);
                if (ChestBlockEntity.getOpenCount(level, pos) > 0 || chest.getContainerSize() > 27 + tier*9) {
                    return InteractionResult.PASS;
                }
                NonNullList<ItemStack> stacks = NonNullList.withSize(chest.getContainerSize(), ItemStack.EMPTY);
                for (int slot = 0; slot < stacks.size(); slot++) {
                    stacks.set(slot, chest.getItem(slot));
                }
                ItemStack drop = new ItemStack(chest.getBlockState().getBlock().asItem());
                level.removeBlockEntity(pos);
                level.removeBlock(pos, false);
                Containers.dropItemStack(level, pos.getX(), pos.getY()+1, pos.getZ(), drop);
                BlockState newState = Registration.CRATE_BLOCK.get().defaultBlockState().setValue(CrateBlock.OPEN, false);
                int variant;
                boolean warded = false;
                variant = CrateItem.numberGenerator.nextRandomItem();
                newState = newState.setValue(CrateBlock.VARIANT, variant);
                if(variant == 5){
                    warded = true;
                }
                level.setBlockAndUpdate(pos, newState);
                level.setBlockEntity(new CrateTile(pos, newState));
                level.setBlockAndUpdate(pos, newState);
//                level.sendBlockUpdated(pos, newState,newState,1);
                level.playSound(player, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS);
                tileEntity = level.getBlockEntity(pos);
                if (tileEntity instanceof CrateTile crateTile) {
                    crateTile.setTierAndState(tier, level, pos);
                    crateTile.setVariant(variant);
                    crateTile.setWardedStatus(warded);

                    for (int i = 0; i < stacks.size(); i++) {
                        SimpleStorage.debugLog("Adding: "+stacks.get(i));
                        crateTile.getItemHandler(null).insertItem(i, stacks.get(i), false);
                    }
                    context.getItemInHand().consume(1, player);
                    return InteractionResult.SUCCESS;
                }

            }
        }
        return InteractionResult.PASS;
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
        MutableComponent tooltip = CompUtils.getArgComp(SimpleStorage.MODID,"tier", stack.getOrDefault(Registration.TIER_DATA, 0));
        if(stack.has(Registration.VARIANT_DATA)) {
            tooltip.append(" | " + CompUtils.getArgComp(SimpleStorage.MODID, "variant",stack.getOrDefault(Registration.VARIANT_DATA, 0)));
        }
        boolean warded = stack.has(DataComponents.CONTAINER);
        if (warded){
            tooltip.append(" | ").append(CompUtils.getComp(SimpleStorage.MODID,"warded").withStyle(ChatFormatting.DARK_PURPLE));
        }
        tooltipComponents.add(tooltip);

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(ConfigSettings.ENABLE_CHEST_SWAP.get() && Screen.hasShiftDown()){
            tooltipComponents.add(CompUtils.getComp(SimpleStorage.MODID, "crate.chest_swap"));
        }
    }

    public ItemStack getWithTier(int tier) {
        ItemStack stack = this.getDefaultInstance();
        stack.set(Registration.TIER_DATA, tier);
        return stack;
    }
}
