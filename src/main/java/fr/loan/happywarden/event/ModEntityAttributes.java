package fr.loan.happywarden.event;

import fr.loan.happywarden.HappyWarden;
import fr.loan.happywarden.entity.HappyWardenEntity;
import fr.loan.happywarden.init.ModEntityTypes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HappyWarden.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityAttributes {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.HAPPY_WARDEN.get(), HappyWardenEntity.registerAttributes().build());
    }
}