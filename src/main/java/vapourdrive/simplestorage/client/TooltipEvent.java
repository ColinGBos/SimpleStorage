package vapourdrive.simplestorage.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import vapourdrive.simplestorage.SimpleStorage;

import java.util.List;

@EventBusSubscriber(modid = SimpleStorage.MODID)
public class TooltipEvent {
    @SubscribeEvent
    public static void onToolTipEarly(ItemTooltipEvent event) {
        if (SimpleStorage.isDebugMode()) {
            List<Component> tips = event.getToolTip();
            ItemStack stack = event.getItemStack();
//            tips.add(Component.literal(Arrays.toString(stack.getTags().toArray())));
            String ret = stack.getDescriptionId().replaceAll("item.", "");
            ret = stack.getDescriptionId().replaceAll("block.", "");

            tips.add(Component.literal(ret));
        }
    }
}
