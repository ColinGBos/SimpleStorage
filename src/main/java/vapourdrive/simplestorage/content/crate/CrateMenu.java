package vapourdrive.simplestorage.content.crate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.network.Network;
import vapourdrive.simplestorage.setup.Registration;
import vapourdrive.vapourware.shared.base.AbstractBaseContainerMenu;

public class CrateMenu extends AbstractBaseContainerMenu {
    // gui position of the player inventory grid
    private final int tier;

    public CrateMenu(int windowId, Level world, BlockPos pos, Inventory inv, Player player, int tier) {
        super(windowId, world, pos, inv, player, Registration.CRATE_CONTAINER.get());
        this.tier = tier;

//        layoutPlayerInventorySlots(PLAYER_INVENTORY_XPOS, PLAYER_INVENTORY_YPOS);

        if (tileEntity != null && tileEntity instanceof CrateTile crateTile) {
            IItemHandler handler = crateTile.getItemHandler(null);
            int index = 0;
            for (int j = 0; j< CrateTile.ROWS_BY_TIER[tier]; j++){
                for (int i = 0; i< CrateTile.COLUMNS_BY_TIER[tier]; i++){
                    addSlot(new SlotItemHandler(handler, index, 8+i*18, 8+j*18));
                    index++;
                }
            }
        }
        layoutPlayerInventorySlots(8, 68+(18*tier));
        //We use this vs the builtin method because we split all the shorts
    }


    @Override
    public boolean stillValid(@NotNull Player player) {
        Level level = tileEntity.getLevel();
        BlockPos pos = tileEntity.getBlockPos();
        assert level != null;
        boolean stillValid = player.canInteractWithBlock(pos, 4.0);

        if (!stillValid){
            tryToCloseCrate();
        }
        return stillValid;
    }

    public void invInteraction(int config) {
        if(config == 0) {
            PacketDistributor.sendToServer(new Network.SortNotification(this.tileEntity.getBlockPos(), 0));
        } else {
            PacketDistributor.sendToServer(new Network.TransferNotification(this.tileEntity.getBlockPos(), config-1));
        }

    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        SimpleStorage.debugLog("in the onRemove block");
        tryToCloseCrate();
    }

    @Override
    protected void clearContainer(@NotNull Player player, @NotNull Container container) {
        super.clearContainer(player, container);
        tryToCloseCrate();

    }

    public int getTier() {
        return tier;
    }

    public void tryToCloseCrate(){
        if(tileEntity instanceof CrateTile crateTile) {
            BlockPos pos = this.tileEntity.getBlockPos();
            crateTile.removeListener();
            if(!crateTile.hasListeners()) {
                this.world.scheduleTick(pos, this.world.getBlockState(pos).getBlock(), this.world.getRandom().nextInt(3) + 4);
            }
        }
    }

    private int getSize(){
        return CrateTile.ROWS_BY_TIER[this.getTier()]*CrateTile.COLUMNS_BY_TIER[this.getTier()];
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        SimpleStorage.debugLog("index: " + index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            //Crate to Player Inventory
            if (index >= 0 && index < getSize()) {
                SimpleStorage.debugLog("From output");
                if (!this.moveItemStackTo(stack, getSize(), getSize()+36, false)) {
                    return ItemStack.EMPTY;
                }
            }

            //Player Inventory
            else if (index >= getSize()) {
                //Inventory to fuel
                if (!this.moveItemStackTo(stack, 0, getSize(), false)) {
                    return ItemStack.EMPTY;
                }

//                //Inventory to hotbar
//                if (index < getSize()+37) {
//                    SimpleStorage.debugLog("From Player inventory to hotbar");
//                    if (!this.moveItemStackTo(stack, 64, 72, false)) {
//                        return ItemStack.EMPTY;
//                    }
//                }
//                //Hotbar to inventory
//                else {
//                    SimpleStorage.debugLog("From Hotbar to inventory");
//                    if (!this.moveItemStackTo(stack, getSize()+1, getSize()+36, false)) {
//                        return ItemStack.EMPTY;
//                    }
//                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
    }
}
