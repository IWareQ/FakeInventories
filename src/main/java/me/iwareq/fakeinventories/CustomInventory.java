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
import me.iwareq.fakeinventories.util.TriConsumer;

import java.util.HashMap;
import java.util.Map;

public class CustomInventory extends BaseInventory {

	private final Map<Integer, TriConsumer<Item, CustomInventory, InventoryTransactionEvent>> listeners = new HashMap<>();

	private final FakeBlock fakeBlock;
	private String title;
	private TriConsumer<Item, CustomInventory, InventoryTransactionEvent> defaultListener;

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

			// Bad idea
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

	public void setItem(int slot, Item item, TriConsumer<Item, CustomInventory, InventoryTransactionEvent> listener) {
		this.setItem(slot, item);

		this.listeners.put(slot, listener);
	}

	public void setDefaultListener(TriConsumer<Item, CustomInventory, InventoryTransactionEvent> listener) {
		this.defaultListener = listener;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void handle(int slot, Item item, InventoryTransactionEvent event) {
		this.listeners.getOrDefault(slot, this.defaultListener).accept(item, this, event);
	}
}