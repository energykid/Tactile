package net.ennway.tactile.mixin;

import net.ennway.tactile.Tactile;
import net.ennway.tactile.data.CursorSwapData;
import net.ennway.tactile.data.DataTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(Gui.class)
public abstract class CursorTextureMixin {
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

            CursorSwapData data = bSH.getData(DataTypes.BLOCK_CURSOR_SWAPS);

            if (p.getType() == HitResult.Type.ENTITY)
            {
                var eP = p.getLocation();
                var aabb = new AABB(eP.x - 1, eP.y - 1, eP.z - 1, eP.x + 1, eP.y + 1, eP.z + 1);

                List<Entity> entityList = level.getEntities(EntityTypeTest.forClass(Entity.class), aabb, e -> true);

                if (!entityList.isEmpty())
                {
                    var type = entityList.getFirst().getType();
                    data = type.builtInRegistryHolder().getData(DataTypes.ENTITY_CURSOR_SWAPS);
                }
            }

            if (data != null)
            {
                return ResourceLocation.fromNamespaceAndPath(Tactile.MODID, data.texture());
            }
        }

        return p_294549_;
    }
}
