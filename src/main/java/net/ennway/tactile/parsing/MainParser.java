package net.ennway.tactile.parsing;

import net.ennway.tactile.Tactile;
import net.ennway.tactile.data.DataTypes;
import net.ennway.tactile.data.EntityCursorSwapData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class MainParser {
    public static String debug = "";
    private static ResourceLocation loc;
    public static ResourceLocation findFrom(CompoundTag tag, EntityCursorSwapData entityData, Entity ent)
    {
        loc = null;
        if (entityData != null) {
            if (!entityData.texture().isEmpty()) {
                if (!entityData.dataValues().isEmpty()) {
                    var allStates = entityData.dataValues().get();
                    for (String str : allStates) {
                        String[] states = BasicParsing.entityStateStrings(str);

                        String dataName = states[0];
                        Object target = BasicParsing.typeOfString(states[1]);
                        String operation = states[2];


                        tag.getAllKeys().forEach(T -> {
                            if (Objects.equals(T.toLowerCase(), dataName.toLowerCase()))
                            {
                                //debug = T + " .. " + dataName + " / " + tag.get(T).toString() + " .. " + target.toString();
                                if (Objects.equals(operation, "="))
                                {
                                    if (tag.get(T) instanceof NumericTag numericTag) {
                                        if (Objects.equals(numericTag.getAsDouble(), BasicParsing.typeOfString(states[1])))
                                            loc = ResourceLocation.fromNamespaceAndPath(Tactile.MODID, entityData.texture().get());
                                    }
                                }
                                else if (Objects.equals(operation, "!="))
                                {
                                    if (tag.get(T) instanceof NumericTag numericTag) {
                                        if (!Objects.equals(numericTag.getAsDouble(), BasicParsing.typeOfString(states[1])))
                                            loc = ResourceLocation.fromNamespaceAndPath(Tactile.MODID, entityData.texture().get());
                                    }
                                }
                                else
                                {
                                    if (tag.get(T) instanceof NumericTag numericTag)
                                    {
                                        int operationTarget = Objects.equals(operation, ">") ? 1 : -1;
                                        if (BasicParsing.compareNumericValues(numericTag.getAsDouble(), target, target.getClass()) == operationTarget)
                                            loc = ResourceLocation.fromNamespaceAndPath(Tactile.MODID, entityData.texture().get());
                                    }
                                }
                            }
                        });

                        if (loc != null) return loc;
                    }
                } else
                    return ResourceLocation.fromNamespaceAndPath(Tactile.MODID, entityData.texture().get());
            }
        }
        return null;
    }
}
