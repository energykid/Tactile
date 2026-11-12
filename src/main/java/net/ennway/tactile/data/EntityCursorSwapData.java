package net.ennway.tactile.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EntityCursorSwapData(String texture) {
    public static final Codec<EntityCursorSwapData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("texture").forGetter(EntityCursorSwapData::texture)
    ).apply(instance, EntityCursorSwapData::new));
}
