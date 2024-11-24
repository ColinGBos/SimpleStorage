package vapourdrive.simplestorage.content.crate;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.vapourware.shared.base.AbstractBaseContainerScreen;
import vapourdrive.vapourware.shared.utils.CompUtils;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

import java.util.List;

public class CrateScreen extends AbstractBaseContainerScreen<CrateMenu> {
    protected final CrateMenu crateMenu;
    private static final int[] imgWidths = {175,175,229};
    private static final int[] imgHeights = {165,221,239};
    private final int tier;
    private static final String[] backgrounds = {"crate", "crate_lg", "crate_xl"};

    public CrateScreen(CrateMenu menu, Inventory inv, Component name) {
        super(menu, inv, name, new DeferredComponent(SimpleStorage.MODID, backgrounds[menu.getTier()]), imgWidths[menu.getTier()]-30, 3, 0);
        SimpleStorage.debugLog(comp.getMod());
        this.crateMenu = menu;
        this.tier = menu.getTier();
    }

    @Override
    public int getYSize() {
        return imgHeights[tier];
    }

    @Override
    public int getXSize() {
        return imgWidths[tier];
    }

    @Override
    protected void getAdditionalInfoHover(List<Component> hoveringText) {
        super.getAdditionalInfoHover(hoveringText);
        hoveringText.add(CompUtils.getComp(comp.getMod(), comp.getTail() + ".wrench").withStyle(ChatFormatting.GOLD));
    }

}
