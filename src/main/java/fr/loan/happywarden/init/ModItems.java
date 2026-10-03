package fr.loan.happywarden.init;

import fr.loan.happywarden.HappyWarden;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModItems {
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HappyWarden.MOD_ID);

    public static final RegistryObject<Item> HAPPY_WARDEN_SPAWN_EGG = ITEMS.register("happy_warden_spawn_egg",
            () -> new ForgeSpawnEggItem(
                ModEntityTypes.HAPPY_WARDEN, // Supplier<EntityType<?>>
                0x034150, // Couleur de fond (Hex)
                0x96d9c0, // Couleur des points (Hex)
                new Item.Properties().group(ItemGroup.MISC) // Onglet créatif
            )
        );

    public static final RegistryObject<Item> WARDEN_BREAD = ITEMS.register("warden_bread",
            () -> new Item(new Item.Properties().group(ItemGroup.FOOD)
            .food(new Food.Builder()
            .hunger(14) // Nombre de demi-gigots restaurés (4 = 2 gigots)
            .saturation(0.6f) // Niveau de saturation
            .setAlwaysEdible() // Permet de manger l'item même quand la barre de faim est pleine
            .effect(() -> new EffectInstance(Effects.STRENGTH, 600, 0), 1.0f)
            .build()
    )));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}