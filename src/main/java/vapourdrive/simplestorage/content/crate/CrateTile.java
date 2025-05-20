package vapourdrive.simplestorage.content.crate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.vapourware.shared.utils.InvUtils;
import vapourdrive.vapourware.shared.utils.ItemStackUtils;
import vapourdrive.vapourware.shared.utils.MachineUtils;

import java.util.List;

import static vapourdrive.simplestorage.setup.Registration.CRATE_BLOCK_ENTITY;

public class CrateTile extends BlockEntity implements MenuProvider {

    public static final int[] COLUMNS_BY_TIER = {8,8,8,10,12};
    public static final int[] ROWS_BY_TIER = {4,6,8,8,8};
    private int tier;
    private int listeners = 0;
    private boolean warded = false;

    private final ItemStackHandler invHandler = new ItemStackHandler(COLUMNS_BY_TIER[0]*ROWS_BY_TIER[0]);

    public CrateTile(BlockPos pos, BlockState state) {
        super(CRATE_BLOCK_ENTITY.get(), pos, state);
        setTier(state.getValue(CrateBlock.TIER));
//        SimpleStorage.debugLog("Creating tile without tier");
    }


    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        setTier(tag.getInt("tier"));
        setWardedStatus(tag.getBoolean("warded"));
        invHandler.deserializeNBT(registries, tag.getCompound("inv"));

    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("tier", getTier());
        tag.put("inv", invHandler.serializeNBT(registries));
        tag.putBoolean("warded", getIsWarded());
    }

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

    public void setWardedStatus(boolean isWarded) {
        this.warded = isWarded;
    }

    public boolean getIsWarded() {
        return this.warded;
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

    public void sortContents() {
        SimpleStorage.debugLog("we are sorting a crate");
        NonNullList<ItemStack> stacks = InvUtils.getIngredientsFromInvHandler(this.getItemHandler(null));
        List<ItemStack> iStacks = MachineUtils.cleanItemStacks(stacks);

        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).isEmpty()) {
                this.getItemHandler(null).extractItem(i, stacks.get(i).getCount(), false);
            }
            SimpleStorage.debugLog("removing item "+stacks.get(i).getDescriptionId());
        }
        iStacks.sort((a, b) -> ItemStackUtils.getModAndName(a).compareToIgnoreCase(ItemStackUtils.getModAndName(b)));

        for (int i = 0; i < iStacks.size(); i++) {
            this.getItemHandler(null).insertItem(i, iStacks.get(i), false);
            SimpleStorage.debugLog("adding back item "+iStacks.get(i).getDescriptionId());
        }
        this.setChanged();

    }

    public void transferContents(ServerPlayer serverPlayer, int config) {
        SimpleStorage.debugLog("we are transferring");
        if(config == 0 || config == 1) {
            IItemHandler inv = this.getItemHandler(null);
            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack crateStack = inv.getStackInSlot(i);
                if (config == 0 || serverPlayer.getInventory().contains(crateStack)) {
                    if (serverPlayer.getInventory().add(crateStack)) {
                        inv.extractItem(i, crateStack.getCount(), false);
                    }
                }
            }
        } else if(config == 2 || config == 3) {
            Inventory playerInv = serverPlayer.getInventory();
            for (int i = 0; i < playerInv.getContainerSize(); i++) {
                ItemStack playerStack = playerInv.getItem(i);
                if (config == 2 || this.containsStack(playerStack)) {
                    if (InvUtils.pushStack(playerStack, true, this.getItemHandler(null))){
                        InvUtils.pushStack(playerStack, false, this.getItemHandler(null));
                        playerInv.removeItem(i, playerStack.getCount());
                    }
                }
            }
        }
        this.setChanged();
    }


    private boolean containsStack(ItemStack stack) {
        for (int i = 0; i < this.getContainerSize(); i++) {
            if (this.getItemHandler(null).getStackInSlot(i).is(stack.getItem())){
                return true;
            }
        }
        return false;
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
