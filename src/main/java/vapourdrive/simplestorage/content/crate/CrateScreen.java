package vapourdrive.simplestorage.content.crate;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.simplestorage.config.ConfigSettings;
import vapourdrive.vapourware.shared.base.AbstractBaseContainerScreen;
import vapourdrive.vapourware.shared.utils.CompUtils;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

import java.util.List;

public class CrateScreen extends AbstractBaseContainerScreen<CrateMenu> {
    protected final CrateMenu crateMenu;
    private static final int[] imgHeights = {150,166,184,202,220};
    protected final Button sortButton_alpha;
    protected final Button transferAllToPlayer;
    protected final Button transferMatchingToPlayer;
    protected final Button transferAllToCrate;
    protected final Button transferMatchingToCrate;

    public CrateScreen(CrateMenu menu, Inventory inv, Component name) {
        super(menu, inv, name, new DeferredComponent(SimpleStorage.MODID, "crate_"+menu.getTier()), 162, -13,244,0, 0, false);
        this.crateMenu = menu;
        SimpleStorage.debugLog("Info x"+ this.INFO_XPOS + " Info y"+ this.INFO_YPOS);
        this.sortButton_alpha = Button.builder(Component.literal("☰"), b -> this.crateMenu.invInteraction(0))
                .size(14, 14)
                .tooltip(Tooltip.create(CompUtils.getComp(SimpleStorage.MODID, "sort_tooltip"))).build();
        this.transferAllToPlayer = Button.builder(Component.literal("↓"), b -> this.crateMenu.invInteraction(1))
                .size(14, 14)
                .tooltip(Tooltip.create(CompUtils.getComp(SimpleStorage.MODID, "take_all_toolip"))).build();
        this.transferMatchingToPlayer = Button.builder(Component.literal("↓*"), b -> this.crateMenu.invInteraction(2))
                .size(14, 14)
                .tooltip(Tooltip.create(CompUtils.getComp(SimpleStorage.MODID, "take_matching_toolip"))).build();
        this.transferAllToCrate = Button.builder(Component.literal("↑"), b -> this.crateMenu.invInteraction(3))
                .size(14, 14)
                .tooltip(Tooltip.create(CompUtils.getComp(SimpleStorage.MODID, "deposit_all_toolip"))).build();
        this.transferMatchingToCrate = Button.builder(Component.literal("↑*"), b -> this.crateMenu.invInteraction(4))
                .size(14, 14)
                .tooltip(Tooltip.create(CompUtils.getComp(SimpleStorage.MODID, "deposit_matching_toolip"))).build();
//        addRenderableWidget(sortButton_alpha);
//        addRenderableWidget(transferAllToPlayer);
//        addRenderableWidget(transferMatchingToPlayer);
//        addRenderableWidget(transferAllToCrate);
//        addRenderableWidget(transferMatchingToCrate);
    }

    protected void init() {
        this.leftPos = (this.width - 176) / 2;
        this.topPos = (this.height - imgHeights[crateMenu.getTier()]) / 2;
        if(ConfigSettings.ENABLE_BUTTONS.get()) {
            int xPos = (this.width/2) + 88;
            int yPos = (this.height - this.getYSize()) / 2 + 1;
            int spacing = 15;
            this.sortButton_alpha.setPosition(xPos, yPos);
            this.transferAllToPlayer.setPosition(xPos, yPos + spacing);
            this.transferMatchingToPlayer.setPosition(xPos, yPos + spacing * 2);
            this.transferAllToCrate.setPosition(xPos, yPos + spacing * 3);
            this.transferMatchingToCrate.setPosition(xPos, yPos + spacing * 4);
            addRenderableWidget(sortButton_alpha);
            addRenderableWidget(transferAllToPlayer);
            addRenderableWidget(transferMatchingToPlayer);
            addRenderableWidget(transferAllToCrate);
            addRenderableWidget(transferMatchingToCrate);
        }
    }

    @Override
    public int getYSize() {
        return imgHeights[crateMenu.getTier()];
    }

    @Override
    public int getXSize() {
        return 176;
    }

    @Override
    protected void getAdditionalInfoHover(List<Component> hoveringText) {
        hoveringText.add(CompUtils.getComp("simplestorage", "crate.info"));
        hoveringText.add(CompUtils.getComp("simplestorage", "crate.wrench").withStyle(ChatFormatting.GOLD));
    }

    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(graphics,partialTicks,mouseX,mouseY);

//        int xPos = (this.width - this.getXSize()) / 2 + INFO_XPOS+13;
//        int yPos = (this.height - this.getYSize()) / 2 + 1;
//        int spacing = 15;
//
//        this.sortButton_alpha.setPosition(xPos, yPos);
//        this.transferAllToPlayer.setPosition(xPos, yPos+spacing);
//        this.transferMatchingToPlayer.setPosition(xPos, yPos+spacing*2);
//        this.transferAllToCrate.setPosition(xPos, yPos+spacing*3);
//        this.transferMatchingToCrate.setPosition(xPos, yPos+spacing*4);
//        sortButton_alpha.render(graphics,mouseX,mouseY,partialTicks);
//        transferAllToPlayer.render(graphics,mouseX,mouseY,partialTicks);
//        transferMatchingToPlayer.render(graphics,mouseX,mouseY,partialTicks);
//        transferAllToCrate.render(graphics,mouseX,mouseY,partialTicks);
//        transferMatchingToCrate.render(graphics,mouseX,mouseY,partialTicks);
//        addRenderableWidget(sortButton_alpha);
//        addRenderableWidget(transferAllToPlayer);
//        addRenderableWidget(transferMatchingToPlayer);
//        addRenderableWidget(transferAllToCrate);
//        addRenderableWidget(transferMatchingToCrate);

    }


}
