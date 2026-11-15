package net.ennway.tactile.mixin;

import com.mojang.brigadier.Command;
import net.ennway.tactile.Tactile;
import net.ennway.tactile.data.BlockCursorSwapData;
import net.ennway.tactile.data.DataTypes;
import net.ennway.tactile.data.EntityCursorSwapData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.NumericTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.attachment.AttachmentInternals;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.fixes.NeoForgeEntityLegacyAttributesFix;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.ibm.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static java.util.Comparator.comparingDouble;

@Mixin(Gui.class)
public abstract class CursorTextureMixin
{
    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    public Object typeOfString(String str)
    {
        if (Objects.equals(str, "false"))
            return false;
        if (Objects.equals(str, "true"))
            return true;
        if (isNumeric(str))
            return Double.parseDouble(str);

        return "Could not find value";
    }
    public String blockStateString(String str)
    {
        if (str.charAt(str.length() - 1) == ']')
        {
            int ch = str.length() - 1;
            for (int i = 0; i < 50; i++)
            {
                if (str.charAt(ch) == '[') break;
                ch--;
            }
            return str.substring(ch + 1, str.length() - 1);
        }
        return "";
    }
    // Use this to separate the string for an entity data entry in cursor.json.
    // Will separate "data=3" into ["data", "3", "="].
    public String[] entityStateStrings(String str)
    {
        if (str.contains("="))
        {
            return new String[] {str.substring(0, str.indexOf("=")), str.substring(str.indexOf("=") + 1), "="};
        }
        if (str.contains(">"))
        {
            return new String[] {str.substring(0, str.indexOf(">")), str.substring(str.indexOf(">") + 1), ">"};
        }
        if (str.contains("<"))
        {
            return new String[] {str.substring(0, str.indexOf("<")), str.substring(str.indexOf("<") + 1), "<"};
        }
        return new String[] {"", "", ""};
    }
    public int compareValues(Object first, Object second, Class clazz)
    {
        if (clazz == Integer.class)
        {
            return ((Integer)first).compareTo((Integer) second);
        }
        if (clazz == Float.class)
        {
            return ((Float)first).compareTo((Float) second);
        }
        if (clazz == Double.class)
        {
            return ((Double)first).compareTo((Double) second);
        }
        if (clazz == Long.class)
        {
            return ((Long)first).compareTo((Long) second);
        }

        return 0;
    }


    @Inject(method = "renderCrosshair", at = @At("HEAD"))
    public void renderDebug(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        Level level = player.level();

        HitResult p = Minecraft.getInstance().hitResult;

        var pos = BlockPos.containing(p.getLocation());

        if (p.getType() == HitResult.Type.BLOCK)
        {
            pos = ((BlockHitResult)p).getBlockPos();
        }

        BlockState bS = level.getBlockState(pos);

        var bSH = bS.getBlockHolder();

        var data = bSH.getData(DataTypes.BLOCK_CURSOR_SWAPS);

        //guiGraphics.drawCenteredString(Minecraft.getInstance().font, debug, 200, 100, 16777215);
    }

    private static String debug;
    private static ResourceLocation loc;

    private static final ResourceLocation tactile$CROSSHAIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair");
    @ModifyArg(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    public ResourceLocation extraRendering(ResourceLocation p_294549_)
    {
        if (p_294549_.equals(tactile$CROSSHAIR_SPRITE))
        {
            LocalPlayer player = Minecraft.getInstance().player;
            Level level = player.level();

            HitResult p = Minecraft.getInstance().hitResult;

            var pos = BlockPos.containing(p.getLocation());

            if (p.getType() == HitResult.Type.BLOCK)
            {
                pos = ((BlockHitResult)p).getBlockPos();
            }

            BlockState bS = level.getBlockState(pos);

            var bSH = bS.getBlockHolder();

            BlockCursorSwapData blockData = bSH.getData(DataTypes.BLOCK_CURSOR_SWAPS);

            EntityCursorSwapData entityData;

            if (p.getType() == HitResult.Type.ENTITY)
            {
                Entity ent = ((EntityHitResult)p).getEntity();

                    var type = ent.getType();
                    entityData = type.builtInRegistryHolder().getData(DataTypes.ENTITY_CURSOR_SWAPS);

                    if (entityData != null) {
                        if (!entityData.texture().isEmpty()) {
                            if (!entityData.dataValues().isEmpty()) {
                                var allStates = entityData.dataValues().get();
                                for (String str : allStates) {
                                    String[] states = entityStateStrings(str);

                                    String dataName = states[0];
                                    Object target = typeOfString(states[1]);
                                    String operation = states[2];

                                    loc = null;

                                    CompoundTag tag = ent.serializeNBT(ent.registryAccess());
                                    tag.getAllKeys().forEach(T -> {
                                        if (Objects.equals(T.toLowerCase(), dataName.toLowerCase()))
                                        {
                                            debug = T + " .. " + dataName + " / " + tag.get(T).toString() + " .. " + target.toString();
                                            if (Objects.equals(operation, "="))
                                            {
                                                if (tag.get(T) instanceof NumericTag numericTag) {
                                                    if (Objects.equals(numericTag.getAsDouble(), typeOfString(states[1])))
                                                        loc = ResourceLocation.fromNamespaceAndPath(Tactile.MODID, entityData.texture().get());
                                                }
                                            }
                                            else
                                            {
                                                if (tag.get(T) instanceof NumericTag numericTag)
                                                {
                                                    int operationTarget = Objects.equals(operation, ">") ? 1 : -1;
                                                    if (compareValues(numericTag.getAsDouble(), target, target.getClass()) == operationTarget)
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
            }

            if (blockData != null)
            {
                if (!blockData.texture().isEmpty())
                {
                    if (blockData.blockStates().isEmpty())
                    {
                        return ResourceLocation.fromNamespaceAndPath(Tactile.MODID, blockData.texture().get());
                    }
                    else
                    {
                        var allStates = blockData.blockStates().get();
                        for (String str : allStates)
                        {
                            if (blockStateString(bS.toString()).contains(str))
                            {
                                return ResourceLocation.fromNamespaceAndPath(Tactile.MODID, blockData.texture().get());
                            }
                        }
                    }
                }

                if (!blockData.conditions().isEmpty())
                {
                    for (BlockCursorSwapData.Condition cond : blockData.conditions().get())
                    {
                        if (cond.blockStates().isEmpty())
                            return ResourceLocation.fromNamespaceAndPath(Tactile.MODID, cond.texture());
                        else
                        {
                            var allStates = cond.blockStates().get();
                            for (String str : allStates)
                            {
                                if (blockStateString(bS.toString()).contains(str))
                                {
                                    return ResourceLocation.fromNamespaceAndPath(Tactile.MODID, cond.texture());
                                }
                            }
                        }
                    }
                }
            }
        }

        return p_294549_;
    }
}
