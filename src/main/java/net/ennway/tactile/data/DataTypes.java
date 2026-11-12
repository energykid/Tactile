package net.ennway.tactile.data;

import net.ennway.tactile.Tactile;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import javax.swing.text.html.parser.Entity;
import java.awt.*;

public class DataTypes {
    public static final DataMapType<Block, CursorSwapData> BLOCK_CURSOR_SWAPS = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(Tactile.MODID, "cursor_icons"),
            Registries.BLOCK,
            CursorSwapData.CODEC
    ).build();

    public static final DataMapType<EntityType<?>, CursorSwapData> ENTITY_CURSOR_SWAPS = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(Tactile.MODID, "cursor_icons"),
            Registries.ENTITY_TYPE,
            CursorSwapData.CODEC
    ).build();
}
