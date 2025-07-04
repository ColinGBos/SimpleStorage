package vapourdrive.simplestorage.content.crate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.setup.Registration;
import vapourdrive.vapourware.VapourWare;
import vapourdrive.vapourware.shared.base.AbstractBaseContainerBlock;
import vapourdrive.vapourware.shared.utils.InvUtils;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.world.Containers.dropItemStack;

public class CrateBlock extends AbstractBaseContainerBlock {
    public static final MapCodec<CrateBlock> CODEC = simpleCodec(CrateBlock::new);

    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final IntegerProperty TIER = IntegerProperty.create("tier", 0, 4);
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 5);
    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);

    public CrateBlock() {
        super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(3f,4f).mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.SNARE));
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

//    @Override
//    public void attack(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player) {
//        BlockEntity blockEntity = level.getBlockEntity(pos);
//        if (blockEntity instanceof CrateTile crateTile){
//            VapourWare.debugLog("Variant: "+crateTile.getVariant()+", Tier: "+crateTile.getTier());
//        }
//
//        super.attack(state, level, pos, player);
//    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
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
    protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder); // Get default drops
        BlockEntity blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof CrateTile crate) {
            drops.clear();
            ItemStack stack = putAdditionalInfo(new ItemStack(this), blockEntity);
            drops.add(stack);
            if(!crate.getIsWarded()) {
                for (int i = 0; i< crate.getContainerSize(); i++){
                    drops.add(crate.getItemHandler(null).getStackInSlot(i));
                }
            }
        }

        return drops;
    }

    @Override
    public void disassemble(BlockState state, @NotNull Level level, @NotNull BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof CrateTile be) {
            ItemStack self = putAdditionalInfo(new ItemStack(this), blockEntity);
            dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), self);
            if(!be.getIsWarded()) {
                for (int i = 0; i < be.getItemHandler(null).getSlots(); i++) {
                    dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), be.getItemHandler(null).getStackInSlot(i));
                }
            }
        }
        onRemove(state, level, blockPos, Blocks.AIR.defaultBlockState(), false);
        level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
        VapourWare.debugLog("in th end of the disassembly");

    }

    @Override
    public boolean sneakWrenchMachine(Player player, Level level, BlockPos pos) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof CrateTile) {
            BlockState state = level.getBlockState(pos);
            int variant = state.getValue(VARIANT);
            if (variant == 4){
                variant = 0;
            } else{
                variant++;
            }
            level.setBlockAndUpdate(pos, state.setValue(VARIANT, variant));
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
        super.putAdditionalInfo(stack, blockEntity);
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
