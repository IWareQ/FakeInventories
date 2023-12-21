package me.iwareq.fakeinventories;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import me.iwareq.fakeinventories.block.DoubleFakeBlock;
import me.iwareq.fakeinventories.block.FakeBlock;
import me.iwareq.fakeinventories.block.SingleFakeBlock;

import java.util.EnumMap;
import java.util.Map;

public class FakeInventories extends PluginBase implements Listener {

    private static final Map<InventoryType, FakeBlock> FAKE_BLOCKS = new EnumMap<>(InventoryType.class);

    @Getter
    private static FakeInventories instance;

    public static FakeBlock getFakeBlock(InventoryType inventoryType) {
        FakeBlock fakeBlock = FAKE_BLOCKS.get(inventoryType);
        if (fakeBlock == null)
            throw new NullPointerException("FakeBlock for " + inventoryType.name() + " inventory not found!");

        return fakeBlock;
    }

    @Override
    public void onLoad() {
        FAKE_BLOCKS.put(InventoryType.CHEST, new SingleFakeBlock(BlockID.CHEST, BlockEntity.CHEST));
        FAKE_BLOCKS.put(InventoryType.ENDER_CHEST, new SingleFakeBlock(BlockID.ENDER_CHEST, BlockEntity.ENDER_CHEST));
        FAKE_BLOCKS.put(InventoryType.DOUBLE_CHEST, new DoubleFakeBlock(BlockID.CHEST, BlockEntity.CHEST));
        FAKE_BLOCKS.put(InventoryType.FURNACE, new SingleFakeBlock(BlockID.FURNACE, BlockEntity.FURNACE));
        FAKE_BLOCKS.put(InventoryType.WORKBENCH, new SingleFakeBlock(BlockID.WORKBENCH, InventoryType.WORKBENCH.getDefaultTitle()));
        FAKE_BLOCKS.put(InventoryType.BREWING_STAND, new SingleFakeBlock(BlockID.BREWING_STAND_BLOCK, BlockEntity.BREWING_STAND));
        FAKE_BLOCKS.put(InventoryType.DISPENSER, new SingleFakeBlock(BlockID.DISPENSER, InventoryType.DISPENSER.getDefaultTitle()));
        FAKE_BLOCKS.put(InventoryType.DROPPER, new SingleFakeBlock(BlockID.DROPPER, InventoryType.DROPPER.getDefaultTitle()));
        FAKE_BLOCKS.put(InventoryType.HOPPER, new SingleFakeBlock(BlockID.HOPPER_BLOCK, BlockEntity.HOPPER));
        FAKE_BLOCKS.put(InventoryType.SHULKER_BOX, new SingleFakeBlock(BlockID.SHULKER_BOX, BlockEntity.SHULKER_BOX));
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryTransaction(InventoryTransactionEvent event) {
        event.getTransaction().getActions().forEach(action -> {
            if (action instanceof SlotChangeAction) {
                SlotChangeAction slotChange = (SlotChangeAction) action;
                if (slotChange.getInventory() instanceof FakeInventory) {
                    FakeInventory inventory = (FakeInventory) slotChange.getInventory();

                    int slot = slotChange.getSlot();
                    Item sourceItem = action.getSourceItem();
                    inventory.handle(slot, sourceItem, event);
                }
            }
        });
    }
}
