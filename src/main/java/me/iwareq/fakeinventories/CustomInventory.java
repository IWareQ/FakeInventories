package me.iwareq.fakeinventories;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import me.iwareq.fakeinventories.block.FakeBlock;
import me.iwareq.fakeinventories.util.ItemHandler;

import java.util.*;

public class CustomInventory extends BaseInventory {

	private final Map<Integer, ItemHandler> handlers = new HashMap<>();

	private final FakeBlock fakeBlock;
	private String title;
	private ItemHandler defaultItemHandler;

	public CustomInventory(InventoryType inventoryType) {
		this(inventoryType, null);
	}

	public CustomInventory(InventoryType inventoryType, String title) {
		super(null, inventoryType);

		this.title = title == null ? inventoryType.getDefaultTitle() : title;
		this.fakeBlock = FakeInventories.getFakeBlock(inventoryType);
	}

	@Override
	public void onOpen(Player player) {
		this.fakeBlock.create(player, this.getTitle());

		Server.getInstance().getScheduler().scheduleDelayedTask(() -> {
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
		}, 3);
	}

	@Override
	public void onClose(Player player) {
		ContainerClosePacket packet = new ContainerClosePacket();
		packet.windowId = player.getWindowId(this);
		packet.wasServerInitiated = player.getClosingWindowId() != packet.windowId;
		player.dataPacket(packet);

		super.onClose(player);

		this.fakeBlock.remove(player);
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
			if (item.getId() == Item.AIR || item.getCount() <= 0) {
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
		super.setItem(index, item);

		this.handlers.put(index, handler);
	}

	public void setDefaultItemHandler(ItemHandler handler) {
		this.defaultItemHandler = handler;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void handle(int index, Item item, InventoryTransactionEvent event) {
		ItemHandler handler = this.handlers.getOrDefault(index, this.defaultItemHandler);
		if (handler != null) {
			handler.handle(item, event);
		}
	}
}
