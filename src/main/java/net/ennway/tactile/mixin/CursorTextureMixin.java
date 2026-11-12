package net.ennway.tactile.mixin;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Gui.class)
public abstract class CursorTextureMixin
{
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

        //guiGraphics.drawCenteredString(Minecraft.getInstance().font, blockStateString(bS.toString()), 200, 100, 16777215);

        EntityCursorSwapData entityData;

        if (p.getType() == HitResult.Type.ENTITY)
        {
            var eP = p.getLocation();
            var aabb = new AABB(eP.x - 1, eP.y - 1, eP.z - 1, eP.x + 1, eP.y + 1, eP.z + 1);

            List<Entity> entityList = level.getEntities(EntityTypeTest.forClass(Entity.class), aabb, e -> true);

            if (!entityList.isEmpty())
            {
                var type = entityList.getFirst().getType();
                entityData = type.builtInRegistryHolder().getData(DataTypes.ENTITY_CURSOR_SWAPS);

                if (entityData != null)
                {
                    //return ResourceLocation.fromNamespaceAndPath(Tactile.MODID, entityData.texture());
                }
            }
        }
    }

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

            BlockCursorSwapData data = bSH.getData(DataTypes.BLOCK_CURSOR_SWAPS);

            EntityCursorSwapData entityData;

            if (p.getType() == HitResult.Type.ENTITY)
            {
                var eP = p.getLocation();
                var aabb = new AABB(eP.x - 1, eP.y - 1, eP.z - 1, eP.x + 1, eP.y + 1, eP.z + 1);

                List<Entity> entityList = level.getEntities(EntityTypeTest.forClass(Entity.class), aabb, e -> true);

                if (entityList.isEmpty())
                {
                    var type = entityList.getFirst().getType();
                    entityData = type.builtInRegistryHolder().getData(DataTypes.ENTITY_CURSOR_SWAPS);

                    if (entityData != null)
                    {
                        return ResourceLocation.fromNamespaceAndPath(Tactile.MODID, entityData.texture());
                    }
                }
            }

            if (data != null)
            {
                if (data.blockStates().isEmpty())
                    return ResourceLocation.fromNamespaceAndPath(Tactile.MODID, data.texture());
                else
                {
                    var allStates = data.blockStates().get();
                    for (String str : allStates)
                    {
                        if (blockStateString(bS.toString()).contains(str))
                        {
                            return ResourceLocation.fromNamespaceAndPath(Tactile.MODID, data.texture());
                        }
                    }
                }
            }
        }

        return p_294549_;
    }
}
