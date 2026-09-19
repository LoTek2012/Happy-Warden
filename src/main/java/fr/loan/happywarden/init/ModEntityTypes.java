package fr.loan.happywarden.init;

import fr.loan.happywarden.entity.HappyWardenEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntityTypes {
    // 1.Création du registre pour les entités
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, "happywarden");

    // 2. Déclaration des entités

    // happywarden
    public static final RegistryObject<EntityType<HappyWardenEntity>> HAPPY_WARDEN =
            ENTITY_TYPES.register("happy_warden",
                () -> EntityType.Builder.of(HappyWardenEntity::new, EntityClassification.CREATURE)
                        .sized(2.0F, 3.7F) // Largeur (X,Z) et la hauteur (Y) de la hitbox
                        .build(new ResourceLocation("happywarden", "happy_warden").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
