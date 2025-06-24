package vapourdrive.simplestorage.setup;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import vapourdrive.simplestorage.content.crate.CrateBlock;
import vapourdrive.simplestorage.content.crate.CrateItem;
import vapourdrive.simplestorage.content.crate.CrateMenu;
import vapourdrive.simplestorage.content.crate.CrateTile;
import vapourdrive.simplestorage.data.recipes.CrateRecipe;
import vapourdrive.vapourware.VapourWare;
import vapourdrive.vapourware.shared.base.BaseInfoItem;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

import java.util.function.Supplier;

import static vapourdrive.simplestorage.SimpleStorage.MODID;

public class Registration {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MODID);
    public static final Supplier<RecipeSerializer<CrateRecipe>> CRATE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("crate",() -> new SimpleCraftingRecipeSerializer<>(CrateRecipe::new));
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, VapourWare.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TIER_DATA = DATA_COMPONENTS.registerComponentType(
            "tier", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> VARIANT_DATA = DATA_COMPONENTS.registerComponentType(
            "variant", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, MODID);

    public static final Supplier<Block> CRATE_BLOCK = BLOCKS.register("crate", () -> new CrateBlock());
    public static final Supplier<CrateItem> CRATE_ITEM = ITEMS.register("crate", () -> new CrateItem(CRATE_BLOCK.get(), new Item.Properties()));
    public static final Supplier<Item> STORAGE_COMPARTMENT_ITEM = ITEMS.register("storage_compartment",
            () -> new BaseInfoItem(new Item.Properties(), new DeferredComponent(MODID,"storage_compartment.info")));
    public static final Supplier<Item> WARDING_CHARM_ITEM = ITEMS.register("warding_charm",
            () -> new BaseInfoItem(new Item.Properties(), new DeferredComponent(MODID,"warding_charm.info")));

    @SuppressWarnings("all")
    public static final Supplier<BlockEntityType<CrateTile>> CRATE_BLOCK_ENTITY = TILES.register("crate",
            () -> BlockEntityType.Builder.of(CrateTile::new, CRATE_BLOCK.get()).build(null));
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
        DATA_COMPONENTS.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
    }

    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        // Add to ingredients tab
        if (event.getTab() == vapourdrive.vapourware.setup.Registration.VAPOUR_GROUP.get() || event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(CRATE_ITEM.get().getDefaultInstance());
            event.accept(STORAGE_COMPARTMENT_ITEM.get().getDefaultInstance());
            event.accept(WARDING_CHARM_ITEM.get().getDefaultInstance());
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CRATE_BLOCK_ENTITY.get(), CrateTile::getItemHandler);
    }
}
