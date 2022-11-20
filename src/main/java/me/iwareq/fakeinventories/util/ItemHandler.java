package me.iwareq.fakeinventories.util;

import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.item.Item;
import me.iwareq.fakeinventories.CustomInventory;

@FunctionalInterface
public interface ItemHandler {

	void handle(Item item, CustomInventory inventory, InventoryTransactionEvent event);
}
