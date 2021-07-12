package ru.iwareq.fakeinventories.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

public class SingleFakeBlock extends AFakeBlock {

	private final Block block;
	private List<Vector3> lastPosition;
	private final String tileId;

	public SingleFakeBlock(Block block, String tileId) {
		this.block = block;
		this.tileId = tileId;
	}

	@Override
	public void sendBlocks(Player player, String title) {
		List<Vector3> positions = this.getPositions(player);
		if (positions != null) {
			this.lastPosition = positions;
			for (Vector3 position : positions) {
				UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
				updateBlockPacket.blockRuntimeId = this.block.getFullId();
				updateBlockPacket.flags = UpdateBlockPacket.FLAG_NETWORK;
				updateBlockPacket.x = position.getFloorX();
				updateBlockPacket.y = position.getFloorY();
				updateBlockPacket.z = position.getFloorZ();
				player.dataPacket(updateBlockPacket);

				BlockEntityDataPacket blockEntityDataPacket = new BlockEntityDataPacket();
				blockEntityDataPacket.x = position.getFloorX();
				blockEntityDataPacket.y = position.getFloorY();
				blockEntityDataPacket.z = position.getFloorZ();
				try {
					blockEntityDataPacket.namedTag = NBTIO.write(this.getBlockEntityDataAt(position, title), ByteOrder.LITTLE_ENDIAN, true);
				} catch (IOException exception) {
					exception.printStackTrace();
				}
				player.dataPacket(blockEntityDataPacket);
			}
		}
	}

	@Override
	public void removeBlocks(Player player) {
		if (this.lastPosition != null) {
			for (Vector3 position : this.lastPosition) {
				UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
				updateBlockPacket.blockRuntimeId = player.getLevel().getBlock(position).getFullId();
				updateBlockPacket.flags = UpdateBlockPacket.FLAG_NETWORK;
				updateBlockPacket.x = position.getFloorX();
				updateBlockPacket.y = position.getFloorY();
				updateBlockPacket.z = position.getFloorZ();
				player.dataPacket(updateBlockPacket);
			}
		}
	}

	protected CompoundTag getBlockEntityDataAt(Vector3 position, String title) {
		return new CompoundTag()
			   .putString("id", tileId)
			   .putString("CustomName", title);
	}
}
