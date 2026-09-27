package fr.loan.happywarden.init;

import fr.loan.happywarden.HappyWarden;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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
                new Item.Properties().tab(ItemGroup.TAB_MISC) // Onglet créatif
            )
        );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}