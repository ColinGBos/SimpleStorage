package vapourdrive.simplestorage.content.crate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vapourdrive.simplestorage.SimpleStorage;

import static vapourdrive.simplestorage.setup.Registration.CRATE_BLOCK_ENTITY;

public class CrateTile extends BlockEntity implements MenuProvider {

    public static final int[] COLUMNS_BY_TIER = {8,8,8,10,12};
    public static final int[] ROWS_BY_TIER = {4,6,8,8,8};
    private int tier;
    private int listeners = 0;
    private boolean blessed = false;

    private final ItemStackHandler invHandler = new ItemStackHandler(COLUMNS_BY_TIER[0]*ROWS_BY_TIER[0]);

    public CrateTile(BlockPos pos, BlockState state) {
        super(CRATE_BLOCK_ENTITY.get(), pos, state);
        setTier(state.getValue(CrateBlock.TIER));
//        SimpleStorage.debugLog("Creating tile without tier");
    }

    public void tickServer(BlockState state) {

    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        setTier(tag.getInt("tier"));
        setBlessed(tag.getBoolean("blessed"));
        invHandler.deserializeNBT(registries, tag.getCompound("inv"));

    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("tier", getTier());
        tag.put("inv", invHandler.serializeNBT(registries));
        tag.putBoolean("blessed", getBlessed());
    }

//    @Nonnull
//    public getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
//        if (capability == ForgeCapabilities.ITEM_HANDLER) {
//            if (side == Direction.DOWN) {
//                return lazyOutputHandler.cast();
//            }
//            return combinedHandler.cast();
//        }
//        return super.getCapability(capability, side);
//    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        return invHandler;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new CrateMenu(id, this.level, this.worldPosition, player.getInventory(), player, tier);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(SimpleStorage.MODID + ".crate");
    }

    public int getContainerSize() {
        return COLUMNS_BY_TIER[tier]*ROWS_BY_TIER[tier];
    }

    public int getTier() {
        return tier;
    }

    public int getVariant() {
        assert this.level != null;
        return this.level.getBlockState(this.worldPosition).getValue(CrateBlock.VARIANT);
    }

    public void setTier(int tier) {
        int newSize = COLUMNS_BY_TIER[tier]*ROWS_BY_TIER[tier];
        invHandler.setSize(newSize);
//        NonNullList<ItemStack> stacks = NonNullList.withSize(newSize, ItemStack.EMPTY);
//        for (int i=0;i<invHandler.getSlots();i++){
//            stacks.set(i,invHandler.getStackInSlot(i).copy());
//        }
        this.tier = tier;
//        this.invHandler = new ItemStackHandler(stacks);
    }

    public void setBlessed(boolean isBlessed) {
        this.blessed = true;
    }

    public boolean getBlessed() {
        return this.blessed;
    }

    public void setTierAndState(int tier, Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(CrateBlock.TIER, tier));
        int newSize = COLUMNS_BY_TIER[tier]*ROWS_BY_TIER[tier];
        invHandler.setSize(newSize);
//        NonNullList<ItemStack> stacks = NonNullList.withSize(newSize, ItemStack.EMPTY);
//        for (int i=0;i<invHandler.getSlots();i++){
//            stacks.set(i,invHandler.getStackInSlot(i).copy());
//        }
        this.tier = tier;
//        this.invHandler = new ItemStackHandler(stacks);
    }

    public void addListener() {
        this.listeners++;
    }

    public void removeListener() {
        if(this.listeners > 0) {
            this.listeners--;
        }
    }

    public boolean hasListeners() {
        return this.listeners>0;
    }

    void playSound(SoundEvent sound, float volume) {
        double d0 = (double)this.worldPosition.getX() + 0.5;
        double d1 = (double)this.worldPosition.getY() + 0.5;
        double d2 = (double)this.worldPosition.getZ() + 0.5;
        assert this.level != null;
        this.level.playSound(null, d0, d1, d2, sound, SoundSource.BLOCKS, volume, this.level.random.nextFloat() * 0.2F + 0.8F);
    }

}
