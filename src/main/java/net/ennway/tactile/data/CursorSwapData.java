package net.ennway.tactile.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CursorSwapData(String texture) {
    public static final Codec<CursorSwapData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("texture").forGetter(CursorSwapData::texture)
    ).apply(instance, CursorSwapData::new));
}
