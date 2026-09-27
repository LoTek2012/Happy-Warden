package fr.loan.happywarden;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import fr.loan.happywarden.init.ModEntityTypes;
import fr.loan.happywarden.init.ModItems;
import fr.loan.happywarden.network.HappyWardenNetwork;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HappyWarden.MOD_ID)
public class HappyWarden
{
    
    // Déclaration de l'ID du mod (doit être identique dans META-INF/mods.toml)
    public static final String MOD_ID = "happywarden";

    // Logger pour afficher des messages propres dans la console si besoin
    private static final Logger LOGGER = LogManager.getLogger();

    public HappyWarden() {
        
        // Récupération du bus d'événements du mod
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Enregistrement des registres (comme tes entités, items, blocs, etc.) auprès du bus d'événements
        ModEntityTypes.register(modEventBus);
        ModItems.register(modEventBus);
        HappyWardenNetwork.register();
    }
}
