package vapourdrive.simplestorage.setup;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import vapourdrive.simplestorage.content.crate.*;

import java.util.function.Supplier;

import static vapourdrive.simplestorage.SimpleStorage.MODID;

public class Registration {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, MODID);

    public static final Supplier<Block> CRATE_BLOCK = BLOCKS.register("crate", () -> new CrateBlock(0));
    public static final Supplier<Block> CRATE_LG_BLOCK = BLOCKS.register("crate_lg", () -> new CrateBlock(1));
    public static final Supplier<Block> CRATE_XL_BLOCK = BLOCKS.register("crate_xl", () -> new CrateBlock(2));
    public static final Supplier<Item> CRATE_ITEM = ITEMS.register("crate", () -> new CrateItem(CRATE_BLOCK.get(), new Item.Properties()));
    public static final Supplier<Item> CRATE_LG_ITEM = ITEMS.register("crate_lg", () -> new CrateItem(CRATE_LG_BLOCK.get(), new Item.Properties()));
    public static final Supplier<Item> CRATE_XL_ITEM = ITEMS.register("crate_xl", () -> new CrateItem(CRATE_XL_BLOCK.get(), new Item.Properties()));
    @SuppressWarnings("all")
    public static final Supplier<BlockEntityType<CrateTile>> CRATE_BLOCK_ENTITY = TILES.register("crate",
            () -> BlockEntityType.Builder.of(CrateTile::new, CRATE_BLOCK.get(), CRATE_LG_BLOCK.get(), CRATE_XL_BLOCK.get()).build(null));
//    @SuppressWarnings("all")
//    public static final Supplier<BlockEntityType<CrateTile>> CRATE_LG_BLOCK_ENTITY = TILES.register("crate_lg",
//            () -> BlockEntityType.Builder.of(CrateTile::new, CRATE_LG_BLOCK.get()).build(null));
//    @SuppressWarnings("all")
//    public static final Supplier<BlockEntityType<CrateTile>> CRATE_XL_BLOCK_ENTITY = TILES.register("crate_xl",
//            () -> BlockEntityType.Builder.of(CrateTile::new, CRATE_XL_BLOCK.get()).build(null));

    public static final Supplier<MenuType<CrateMenu>> CRATE_CONTAINER = MENUS.register("crate",
        () -> IMenuTypeExtension.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level world = inv.player.getCommandSenderWorld();
            BlockEntity blockEntity = world.getBlockEntity(pos);
            int tier = 0;
            if(blockEntity instanceof CrateTile crateTile) {
                tier = crateTile.getTier();
            }
            return new CrateMenu(windowId, world, pos, inv, inv.player, tier);
        }));

    public static void init(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        TILES.register(eventBus);
        MENUS.register(eventBus);
    }

    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        // Add to ingredients tab
        if (event.getTab() == vapourdrive.vapourware.setup.Registration.VAPOUR_GROUP.get() || event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(CRATE_ITEM.get().getDefaultInstance());
            event.accept(CRATE_LG_ITEM.get().getDefaultInstance());
            event.accept(CRATE_XL_ITEM.get().getDefaultInstance());
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CRATE_BLOCK_ENTITY.get(), CrateTile::getItemHandler);
    }
}
