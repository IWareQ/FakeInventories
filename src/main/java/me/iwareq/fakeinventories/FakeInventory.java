package me.iwareq.fakeinventories;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import lombok.Setter;
import me.iwareq.fakeinventories.block.FakeBlock;
import me.iwareq.fakeinventories.util.ItemHandler;

import java.util.*;

public class FakeInventory extends BaseInventory {

    private final Map<Integer, ItemHandler> handlers = new HashMap<>();

    private final FakeBlock fakeBlock;
    @Setter
    private String title;
    @Setter
    private ItemHandler defaultItemHandler;

    public FakeInventory(InventoryType inventoryType) {
        this(inventoryType, null);
    }

    public FakeInventory(InventoryType inventoryType, String title) {
        super(null, inventoryType);

        this.title = title == null ? inventoryType.getDefaultTitle() : title;
        this.fakeBlock = FakeInventories.getFakeBlock(inventoryType);
    }

    @Override
    public void onOpen(Player player) {
        this.fakeBlock.create(player, this.getTitle());

        Server.getInstance().getScheduler().scheduleDelayedTask(FakeInventories.getInstance(), () -> {
            ContainerOpenPacket packet = new ContainerOpenPacket();
            packet.windowId = player.getWindowId(this);
            packet.type = this.getType().getNetworkType();

            Vector3 position = this.fakeBlock.getPositions(player).get(0);
            packet.x = position.getFloorX();
            packet.y = position.getFloorY();
            packet.z = position.getFloorZ();
            player.dataPacket(packet);

            super.onOpen(player);

            this.sendContents(player);
        }, 5);
    }

    @Override
    public void onClose(Player player) {
        ContainerClosePacket packet = new ContainerClosePacket();
        packet.windowId = player.getWindowId(this);
        packet.wasServerInitiated = player.getClosingWindowId() != packet.windowId;
        player.dataPacket(packet);

        super.onClose(player);

        Server.getInstance().getScheduler().scheduleDelayedTask(FakeInventories.getInstance(), () -> {
            this.fakeBlock.remove(player);
        }, 5);
    }

    public Item[] addItem(ItemHandler handler, Item... slots) {
        List<Item> itemSlots = new ArrayList<>();
        for (Item slot : slots) {
            if (slot.getId() != 0 && slot.getCount() > 0) {
                itemSlots.add(slot.clone());
            }
        }

        List<Integer> emptySlots = new ArrayList<>();

        for (int i = 0; i < this.getSize(); ++i) {
            Item item = this.getItem(i);
            if (item.getId() == BlockID.AIR || item.getCount() <= 0) {
                emptySlots.add(i);
            }

            for (Item slot : Collections.unmodifiableList(itemSlots)) {
                if (slot.equals(item) && item.getCount() < item.getMaxStackSize()) {
                    int amount = Math.min(item.getMaxStackSize() - item.getCount(), slot.getCount());
                    amount = Math.min(amount, this.getMaxStackSize());
                    if (amount > 0) {
                        slot.setCount(slot.getCount() - amount);
                        item.setCount(item.getCount() + amount);

                        this.setItem(i, item, handler);

                        if (slot.getCount() <= 0) {
                            itemSlots.remove(slot);
                        }
                    }
                }
            }

            if (itemSlots.isEmpty()) {
                break;
            }
        }

        if (!itemSlots.isEmpty() && !emptySlots.isEmpty()) {
            for (int slotIndex : emptySlots) {
                if (!itemSlots.isEmpty()) {
                    Item slot = itemSlots.get(0);

                    int amount = Math.min(slot.getMaxStackSize(), slot.getCount());
                    amount = Math.min(amount, this.getMaxStackSize());

                    slot.setCount(slot.getCount() - amount);

                    Item item = slot.clone();
                    item.setCount(amount);

                    this.setItem(slotIndex, item, handler);

                    if (slot.getCount() <= 0) {
                        itemSlots.remove(slot);
                    }
                }
            }
        }

        return itemSlots.toArray(new Item[0]);
    }

    public void setItem(int index, Item item, ItemHandler handler) {
        if (super.setItem(index, item)) {
            this.handlers.put(index, handler);
        }
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public void handle(int index, Item item, InventoryTransactionEvent event) {
        ItemHandler handler = this.handlers.getOrDefault(index, this.defaultItemHandler);
        handler.handle(item, event);
    }
}
