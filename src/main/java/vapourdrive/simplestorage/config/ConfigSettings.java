package vapourdrive.simplestorage.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import vapourdrive.simplestorage.SimpleStorage;

public class ConfigSettings {
    public static final String CATEGORY_MOD = "simple_storage";

    public static final ModConfigSpec SERVER_CONFIG;
    public static final ModConfigSpec CLIENT_CONFIG;
    public static final String SUBCATEGORY_CRATE = "crate";
    public static ModConfigSpec.BooleanValue ENABLE_CRATE;
    public static ModConfigSpec.BooleanValue ENABLE_BUTTONS;
//    public static ModConfigSpec.IntValue PRIMITIVE_QUARRY_FUEL_TO_WORK;
//    public static ModConfigSpec.IntValue PRIMITIVE_QUARRY_PROCESS_TIME;
//    public static ModConfigSpec.IntValue PRIMITIVE_QUARRY_MAX_RADIUS;

    static {
        SimpleStorage.LOGGER.info("Initiating Config for Simple Storage");
        ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

        SERVER_BUILDER.comment("Simple Storage Settings").push(CATEGORY_MOD);
        CLIENT_BUILDER.comment("Simple Storage Client Settings").push(CATEGORY_MOD);

//        setupFirstBlockConfig(SERVER_BUILDER);
        setupFirstBlockConfig(SERVER_BUILDER, CLIENT_BUILDER);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupFirstBlockConfig(ModConfigSpec.Builder SERVER_BUILDER, ModConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Crate Settings").push(SUBCATEGORY_CRATE);
        ENABLE_CRATE = SERVER_BUILDER.comment("Is the Crate Enabled").define("enableCrate", true);
        SERVER_BUILDER.pop();

        CLIENT_BUILDER.comment("Crate Settings").push(SUBCATEGORY_CRATE);
        ENABLE_BUTTONS = CLIENT_BUILDER.comment("Are the crate inventory buttons enabled").define("enableCrateButtons", true);
        CLIENT_BUILDER.pop();

    }

}
