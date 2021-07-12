package ru.iwareq.fakeinventories;

import cn.nukkit.Player;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import ru.iwareq.fakeinventories.block.AFakeBlock;

public class CustomInventory extends BaseInventory {

	private String title;
	private final AFakeBlock fakeBlock;

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
		this.fakeBlock.sendBlocks(player, this.getTitle());
		ContainerOpenPacket containerOpenPacket = new ContainerOpenPacket();
		containerOpenPacket.windowId = player.getWindowId(this);
		containerOpenPacket.type = this.getType().getNetworkType();
		Vector3 pos = this.fakeBlock.getPositions(player).get(0);
		containerOpenPacket.x = pos.getFloorX();
		containerOpenPacket.y = pos.getFloorY();
		containerOpenPacket.z = pos.getFloorZ();
		player.dataPacket(containerOpenPacket);
		super.onOpen(player);
		this.sendContents(player);
	}

	@Override
	public void onClose(Player player) {
		ContainerClosePacket containerClosePacket = new ContainerClosePacket();
		containerClosePacket.windowId = player.getWindowId(this);
		containerClosePacket.wasServerInitiated = player.getClosingWindowId() != containerClosePacket.windowId;
		player.dataPacket(containerClosePacket);
		super.onClose(player);
		this.fakeBlock.removeBlocks(player);
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		if (title == null) {
			this.title = this.getType().getDefaultTitle();
		} else {
			this.title = title;
		}
	}
}