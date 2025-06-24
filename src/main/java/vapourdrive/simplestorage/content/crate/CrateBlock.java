package vapourdrive.simplestorage.content.crate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.setup.Registration;
import vapourdrive.vapourware.VapourWare;
import vapourdrive.vapourware.shared.base.AbstractBaseContainerBlock;
import vapourdrive.vapourware.shared.utils.InvUtils;
import vapourdrive.vapourware.shared.utils.WeightedRandom;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class CrateBlock extends AbstractBaseContainerBlock {
    public static final MapCodec<CrateBlock> CODEC = simpleCodec(CrateBlock::new);

    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final IntegerProperty TIER = IntegerProperty.create("tier", 0, 4);
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 5);
    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);

    public CrateBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.SNARE));
    }

    public CrateBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CrateTile(pos, state);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void attack(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CrateTile crateTile){
            VapourWare.debugLog("Variant: "+crateTile.getVariant()+", Tier: "+crateTile.getTier());
        }

        super.attack(state, level, pos, player);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return null;
    }

    @Override
    protected void openContainer(Level level, @NotNull BlockPos pos, @NotNull Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CrateTile crateTile) {
            player.openMenu((MenuProvider) blockEntity, pos);
//            level.getBlockState(pos).setValue(OPEN, true);
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(OPEN, true));
//            level.scheduleTick(pos, this, level.getRandom().nextInt(40) + 20);
            crateTile.playSound(SoundEvents.BARREL_OPEN, 0.5f);
            crateTile.addListener();
        }
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder); // Get default drops
        BlockEntity blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof CrateTile crate) {
            drops.clear();
            ItemStack stack = new ItemStack(this);
            stack.set(Registration.TIER_DATA, crate.getTier());
            stack.set(Registration.VARIANT_DATA, crate.getVariant());
            if(crate.getIsWarded()) {
                stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(InvUtils.getIngredientsFromInvHandler(crate.getItemHandler(null))));
            } else {
                for (int i = 0; i< crate.getContainerSize(); i++){
                    drops.add(crate.getItemHandler(null).getStackInSlot(i));
                }
            }
            drops.add(stack);
        }

        return drops;
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos blockPos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            super.onRemove(state, world, blockPos, newState, isMoving);
        }
    }

    @Override
    public boolean sneakWrenchMachine(Player player, Level level, BlockPos pos) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof CrateTile crate) {
            if(player.getOffhandItem().is(Registration.STORAGE_COMPARTMENT_ITEM.get())){
                if(crate.getTier()<4) {
                    int newTier = crate.getTier() + 1;
                    player.getOffhandItem().consume(1, player);
                    NonNullList<ItemStack> stacks = InvUtils.getIngredientsFromInvHandler(crate.getItemHandler(null));
                    crate.setTierAndState(newTier, level, pos);
                    for (int i = 0; i < stacks.size(); i++) {
                        crate.getItemHandler(null).insertItem(i, stacks.get(i), false);
                    }
                }
            } else if (player.getOffhandItem().is(Registration.WARDING_CHARM_ITEM.get())) {
                if(!crate.getIsWarded()){
                    crate.setWardedStatus(true);
                    player.getOffhandItem().consume(1, player);
                }
            } else {
                BlockState state = level.getBlockState(pos);
                int variant = state.getValue(VARIANT);
                if (variant == 4){
                    variant = 0;
                } else{
                    variant++;
                }
                level.setBlockAndUpdate(pos, state.setValue(VARIANT, variant));
//                dropContents(level, pos.above(), crate.getItemHandler(null));
            }
        }
        return true;
    }

    @Override
    protected @NotNull MapCodec<? extends CrateBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
//        if (!context.getLevel().isClientSide()) {
            int tier = context.getItemInHand().getOrDefault(Registration.TIER_DATA, 0);
            int variant;
            if (context.getItemInHand().has(Registration.VARIANT_DATA)) {
                variant = context.getItemInHand().getOrDefault(Registration.VARIANT_DATA, 0);
                return this.defaultBlockState().setValue(OPEN, false).setValue(TIER, tier).setValue(VARIANT, variant);
            }
//            else if (!context.getLevel().isClientSide()) {
////              we only set this on the server so there isn't an issue with the client and the server generating a different variant
//                return this.defaultBlockState().setValue(OPEN, false).setValue(TIER, tier).setValue(VARIANT, variant);
//            }
            return this.defaultBlockState().setValue(OPEN, false).setValue(TIER, tier);
//        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(TIER, OPEN, VARIANT);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CrateTile crateTile) {
//            level.getBlockState(pos).setValue(OPEN, true);
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(OPEN, false));
            crateTile.playSound(SoundEvents.BARREL_CLOSE, 0.4f);
        }
    }

    @Override
    protected ItemStack putAdditionalInfo(ItemStack stack, BlockEntity blockEntity) {
        if (blockEntity instanceof CrateTile crateTile) {
            stack.set(Registration.TIER_DATA, crateTile.getTier());
            stack.set(Registration.VARIANT_DATA, crateTile.getVariant());
            if(crateTile.getIsWarded()) {
                stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(InvUtils.getIngredientsFromInvHandler(crateTile.getItemHandler(null))));
            }
//            stack.set(Registration.INV_DATA, crateTile.getItemHandler(null).se);
        }
        return stack;
    }
}
