package net.ennway.tactile.data;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber
public class DataEvents {

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(DataTypes.BLOCK_CURSOR_SWAPS);
        event.register(DataTypes.ENTITY_CURSOR_SWAPS);
    }
}
