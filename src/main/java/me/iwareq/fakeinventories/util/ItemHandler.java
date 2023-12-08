package me.iwareq.fakeinventories.util;

import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.item.Item;

@FunctionalInterface
public interface ItemHandler {

    void handle(Item item, InventoryTransactionEvent event);
}
