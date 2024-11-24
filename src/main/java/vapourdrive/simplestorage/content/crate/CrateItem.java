package vapourdrive.simplestorage.content.crate;

import net.minecraft.world.level.block.Block;
import vapourdrive.simplestorage.SimpleStorage;
import vapourdrive.vapourware.shared.base.BaseInfoItemBlock;
import vapourdrive.vapourware.shared.base.BaseMachineItem;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

public class CrateItem extends BaseInfoItemBlock {
    public CrateItem(Block block, Properties properties) {
        super(block, properties, new DeferredComponent(SimpleStorage.MODID, "crate.info"));
    }

}
