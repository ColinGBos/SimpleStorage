package vapourdrive.simplestorage.integrations.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.content.crate.CrateTile;
import vapourdrive.vapourware.shared.utils.CompUtils;

import java.text.DecimalFormat;

public enum CrateContentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;
    private final DecimalFormat df = new DecimalFormat("#,###");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
        if (blockAccessor.getServerData().contains("tier")) {
            int i = blockAccessor.getServerData().getInt("tier");
            tooltip.add(CompUtils.getArgComp(SimpleStorage.MODID,"tier", df.format(i)));
        }
        if (blockAccessor.getServerData().contains("blessed")) {
            if (blockAccessor.getServerData().getBoolean("blessed")) {
                tooltip.add(CompUtils.getComp(SimpleStorage.MODID, "blessed").withStyle(ChatFormatting.GOLD));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return JadePlugin.CRATES;
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof CrateTile crateTile) {
            tag.putInt("tier", crateTile.getTier());
            tag.putBoolean("blessed", crateTile.getBlessed());
        }
    }
}
