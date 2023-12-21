package me.iwareq.fakeinventories.block;

import cn.nukkit.Player;
import cn.nukkit.level.DimensionData;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DoubleFakeBlock extends SingleFakeBlock {

    public DoubleFakeBlock(int blockId, String tileId) {
        super(blockId, tileId);
    }

    @Override
    public List<Vector3> getPositions(Player player) {
        Vector3 blockPosition = player.getPosition().add(this.getOffset(player)).floor();
        DimensionData dimensionData = player.getLevel().getDimensionData();
        if (blockPosition.getFloorY() >= dimensionData.getMinHeight() && blockPosition.getFloorY() < dimensionData.getMaxHeight()) {
            if ((blockPosition.getFloorX() & 1) == 1) return Arrays.asList(blockPosition, blockPosition.east());

            return Arrays.asList(blockPosition, blockPosition.west());
        }

        return Collections.emptyList();
    }

    @Override
    protected Vector3 getOffset(Player player) {
        Vector3 offset = super.getOffset(player);
        offset.x *= 1.5;
        offset.z *= 1.5;
        return offset;
    }

    @Override
    protected CompoundTag getBlockEntityDataAt(Vector3 position, String title) {
        int pairX = (position.getFloorX() & 1) == 1 ? 1 : -1;
        return super.getBlockEntityDataAt(position, title)
                .putInt("pairx", position.getFloorX() + pairX)
                .putInt("pairz", position.getFloorZ());
    }
}
