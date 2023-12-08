package me.iwareq.fakeinventories.block;

import cn.nukkit.Player;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

public class SingleFakeBlock extends FakeBlock {

    protected final int blockId;
    protected final String tileId;
    protected List<Vector3> lastPositions;

    public SingleFakeBlock(int blockId, String tileId) {
        this.blockId = blockId;
        this.tileId = tileId;
    }

    @Override
    public void create(Player player, String title) {
        List<Vector3> positions = this.getPositions(player);

        this.lastPositions = positions;

        positions.forEach(position -> {
            UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
            updateBlockPacket.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(this.blockId, 0);
            updateBlockPacket.flags = UpdateBlockPacket.FLAG_ALL;
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
        });
    }

    @Override
    public void remove(Player player) {
        this.lastPositions.forEach(position -> {
            UpdateBlockPacket packet = new UpdateBlockPacket();
            packet.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(player.getLevel().getBlock(position).getFullId());
            packet.flags = UpdateBlockPacket.FLAG_ALL;
            packet.x = position.getFloorX();
            packet.y = position.getFloorY();
            packet.z = position.getFloorZ();
            player.dataPacket(packet);
        });
    }

    protected CompoundTag getBlockEntityDataAt(Vector3 position, String title) {
        return new CompoundTag()
                .putString("id", tileId)
                .putString("CustomName", title);
    }
}
