package net.ennway.tactile.mixin;

import net.ennway.tactile.Tactile;
import net.ennway.tactile.data.BlockCursorSwapData;
import net.ennway.tactile.data.DataTypes;
import net.ennway.tactile.data.EntityCursorSwapData;
import net.ennway.tactile.parsing.BasicParsing;
import net.ennway.tactile.parsing.CompoundParsing;
import net.ennway.tactile.parsing.MainParser;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.Console;
import java.util.Objects;
import java.util.logging.Logger;

@Mixin(Gui.class)
public abstract class CursorTextureMixin
{

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
        //guiGraphics.drawCenteredString(Minecraft.getInstance().font, MainParser.debug, 200, 150, 16777215);
        //guiGraphics.drawCenteredString(Minecraft.getInstance().font, CompoundParsing.debug, 200, 200, 16777215);
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

                CompoundTag tag = serializeNBT(ent);

                if (!Objects.equals(debug, tag.getAllKeys().toString()))
                    Tactile.LOGGER.info(tag.getAllKeys().toString());
                debug = tag.getAllKeys().toString();

                loc = MainParser.findFrom(tag, entityData);

                if (loc != null)
                {
                    return loc;
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
                            if (BasicParsing.blockStateString(bS.toString()).contains(str))
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
                                if (BasicParsing.blockStateString(bS.toString()).contains(str))
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
    private static CompoundTag serializeNBT(Entity entity) {
        CompoundTag ret = new CompoundTag();
        return entity.saveWithoutId(ret);
    }
}
