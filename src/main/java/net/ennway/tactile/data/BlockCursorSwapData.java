package net.ennway.tactile.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record BlockCursorSwapData(Optional<String> texture, Optional<List<String>> blockStates, Optional<List<Condition>> conditions) {
    public static final Codec<BlockCursorSwapData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("texture").forGetter(BlockCursorSwapData::texture),
            Codec.list(Codec.STRING).optionalFieldOf("blockstates").forGetter(BlockCursorSwapData::blockStates),
            Codec.list(Condition.CODEC).optionalFieldOf("conditions").forGetter(BlockCursorSwapData::conditions)
    ).apply(instance, BlockCursorSwapData::new));

    public record Condition(String texture, Optional<List<String>> blockStates) {
        public static final Codec<Condition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("texture").forGetter(Condition::texture),
                Codec.list(Codec.STRING).optionalFieldOf("blockstates").forGetter(Condition::blockStates)
        ).apply(instance, Condition::new));
    }
}
