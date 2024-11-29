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
    private static final int[] imgWidths = {176,176,176,194,230};
    private static final int[] helpX = {154,154,154,181,217};
    private static final int[] titleX = {9,9,9,0,0};
    private static final int[] imgHeights = {175,211,247,247,247};

    public CrateScreen(CrateMenu menu, Inventory inv, Component name) {
        super(menu, inv, name, new DeferredComponent(SimpleStorage.MODID, "crate_"+menu.getTier()), helpX[menu.getTier()], -14,244,0, titleX[menu.getTier()], false);
        this.crateMenu = menu;
    }

    @Override
    public int getYSize() {
        return imgHeights[menu.getTier()];
    }

    @Override
    public int getXSize() {
        return imgWidths[menu.getTier()];
    }

    @Override
    protected void getAdditionalInfoHover(List<Component> hoveringText) {
        hoveringText.add(CompUtils.getComp("simplestorage", "crate.info"));
        hoveringText.add(CompUtils.getComp("simplestorage", "crate.wrench").withStyle(ChatFormatting.GOLD));
    }


}
