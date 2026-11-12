package net.ennway.tactile.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record BlockCursorSwapData(String texture, Optional<List<String>> blockStates) {
    public static final Codec<BlockCursorSwapData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("texture").forGetter(BlockCursorSwapData::texture),
            Codec.list(Codec.STRING).optionalFieldOf("blockstates").forGetter(BlockCursorSwapData::blockStates)
    ).apply(instance, BlockCursorSwapData::new));
}
