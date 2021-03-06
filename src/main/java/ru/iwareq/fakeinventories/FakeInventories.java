package ru.iwareq.fakeinventories;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.plugin.PluginBase;
import ru.iwareq.fakeinventories.block.AFakeBlock;
import ru.iwareq.fakeinventories.block.DoubleFakeBlock;
import ru.iwareq.fakeinventories.block.SingleFakeBlock;

import java.util.HashMap;
import java.util.Map;

public class FakeInventories extends PluginBase {

	private static final Map<InventoryType, AFakeBlock> FAKE_BLOCKS = new HashMap<>();

	@Override()
	public void onEnable() {
		FAKE_BLOCKS.put(InventoryType.CHEST, new SingleFakeBlock(Block.get(Block.CHEST), BlockEntity.CHEST));
		FAKE_BLOCKS.put(InventoryType.DOUBLE_CHEST, new DoubleFakeBlock(Block.get(Block.CHEST), BlockEntity.CHEST));
		FAKE_BLOCKS.put(InventoryType.HOPPER, new SingleFakeBlock(Block.get(Block.HOPPER_BLOCK), BlockEntity.HOPPER));
	}

	public static AFakeBlock getFakeBlock(InventoryType inventoryType) {
		return FAKE_BLOCKS.getOrDefault(inventoryType, new SingleFakeBlock(Block.get(Block.CHEST), BlockEntity.CHEST));
	}
}
