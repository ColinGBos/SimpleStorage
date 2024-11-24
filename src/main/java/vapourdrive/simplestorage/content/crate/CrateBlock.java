package vapourdrive.simplestorage.content.crate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.setup.Registration;
import vapourdrive.vapourware.shared.base.AbstractBaseContainerBlock;

import javax.annotation.Nullable;
import java.util.function.Supplier;


public class CrateBlock extends AbstractBaseContainerBlock {

    public static final MapCodec<CrateBlock> CODEC = simpleCodec(CrateBlock::new);
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
    public int tier;

    public CrateBlock(int tier) {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.SNARE));
        this.tier = tier;
    }

    public CrateBlock(Properties properties) {
        super(properties);
        this.tier = 0;
    }

    public CrateBlock(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CrateTile(pos, state, tier);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        } else {
            return (level1, pos, state1, tile) -> {
                if (tile instanceof CrateTile machine) {
                    machine.tickServer(state1);
                }
            };
        }
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
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos blockPos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = world.getBlockEntity(blockPos);
            if (tileEntity instanceof CrateTile crate) {
                dropContents(world, blockPos, crate.getItemHandler(null));
            }
            super.onRemove(state, world, blockPos, newState, isMoving);
        }
    }

    @Override
    public boolean sneakWrenchMachine(Player player, Level level, BlockPos pos) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof CrateTile crate) {
            if(player.getOffhandItem().is(Items.REDSTONE_TORCH) && crate.getTier()<=2){
                crate.setTier(crate.getTier()+1);
                if (tier == 1){
                    level.setBlockAndUpdate(pos, Registration.CRATE_LG_BLOCK.get().defaultBlockState());
                } else if (tier == 2){
                    level.setBlockAndUpdate(pos, Registration.CRATE_XL_BLOCK.get().defaultBlockState());
                }
            } else {
                SimpleStorage.debugLog("sneaking with a wrench");
                dropContents(level, pos.above(), crate.getItemHandler(null));
            }
        }
        return true;
    }

    @Override
    protected @NotNull MapCodec<? extends CrateBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(OPEN, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(OPEN);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        SimpleStorage.debugLog("going tick");
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CrateTile crateTile) {
//            level.getBlockState(pos).setValue(OPEN, true);
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(OPEN, false));
            crateTile.playSound(SoundEvents.BARREL_CLOSE, 0.4f);
        }
    }
}
