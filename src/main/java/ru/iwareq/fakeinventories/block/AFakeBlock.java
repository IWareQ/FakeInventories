package ru.iwareq.fakeinventories.block;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

import java.util.Arrays;
import java.util.List;

public abstract class AFakeBlock {

	public abstract void sendBlocks(Player player, String title);

	public abstract void removeBlocks(Player player);

	public List<Vector3> getPositions(Player player) {
		Vector3 blockPosition = player.getPosition().add(this.getOffset(player)).floor();
		if (blockPosition.getFloorY() >= 0 && blockPosition.getFloorY() < 256) {
			return Arrays.asList(blockPosition);
		}
		return null;
	}

	protected Vector3 getOffset(Player player) {
		Vector3 offset = player.getDirectionVector();
		offset.x *= -(1 + player.getWidth());
		offset.y *= -(1 + player.getHeight());
		offset.z *= -(1 + player.getWidth());
		return offset;
	}
}