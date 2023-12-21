package me.iwareq.fakeinventories.block;

import cn.nukkit.Player;
import cn.nukkit.level.DimensionData;
import cn.nukkit.math.Vector3;

import java.util.Collections;
import java.util.List;

public abstract class FakeBlock {

    public abstract void create(Player player, String title);

    public abstract void remove(Player player);

    public List<Vector3> getPositions(Player player) {
        Vector3 blockPosition = player.getPosition().add(this.getOffset(player)).floor();
        DimensionData dimensionData = player.getLevel().getDimensionData();
        if (blockPosition.getFloorY() >= dimensionData.getMinHeight() && blockPosition.getFloorY() < dimensionData.getMaxHeight())
            return Collections.singletonList(blockPosition);

        return Collections.emptyList();
    }

    protected Vector3 getOffset(Player player) {
        Vector3 offset = player.getDirectionVector();
        offset.x *= -(1 + player.getWidth());
        offset.y *= -(1 + player.getHeight());
        offset.z *= -(1 + player.getWidth());
        return offset;
    }
}
