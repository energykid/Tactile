package net.ennway.tactile.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record EntityCursorSwapData(Optional<String> texture, Optional<List<String>> dataValues, Optional<List<Condition>> conditions) {
    public static final Codec<EntityCursorSwapData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("texture").forGetter(EntityCursorSwapData::texture),
            Codec.list(Codec.STRING).optionalFieldOf("data").forGetter(EntityCursorSwapData::dataValues),
            Codec.list(Condition.CODEC).optionalFieldOf("conditions").forGetter(EntityCursorSwapData::conditions)
    ).apply(instance, EntityCursorSwapData::new));

    public record Condition(String texture, Optional<List<String>> dataValues) {
        public static final Codec<Condition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("texture").forGetter(Condition::texture),
                Codec.list(Codec.STRING).optionalFieldOf("data").forGetter(Condition::dataValues)
        ).apply(instance, Condition::new));
    }
}
