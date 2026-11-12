package net.ennway.tactile.data;

import net.ennway.tactile.Tactile;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;

public class DataTypes {
    public static final AdvancedDataMapType<Block, BlockCursorSwapData, DataMapValueRemover.Default<BlockCursorSwapData, Block>> BLOCK_CURSOR_SWAPS
            = AdvancedDataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(Tactile.MODID, "cursor"),
            Registries.BLOCK,
            BlockCursorSwapData.CODEC
    ).build();

    public static final AdvancedDataMapType<EntityType<?>, EntityCursorSwapData, DataMapValueRemover.Default<EntityCursorSwapData, EntityType<?>>> ENTITY_CURSOR_SWAPS
            = AdvancedDataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(Tactile.MODID, "cursor"),
            Registries.ENTITY_TYPE,
            EntityCursorSwapData.CODEC
    ).build();
}
