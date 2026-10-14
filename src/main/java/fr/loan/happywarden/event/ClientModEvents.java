package fr.loan.happywarden.event;

import fr.loan.happywarden.HappyWarden;
import fr.loan.happywarden.client.renderer.happy_warden.HappyWardenRenderer;
import fr.loan.happywarden.init.ModEntityTypes;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = HappyWarden.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)

public class ClientModEvents {
    public static final KeyBinding FIRE_BEAM_KEY = new KeyBinding(
        "key.happywarden.fire_beam", GLFW.GLFW_KEY_G, "key.categories.happywarden");

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ClientRegistry.registerKeyBinding(FIRE_BEAM_KEY));
        RenderingRegistry.registerEntityRenderingHandler(
            ModEntityTypes.HAPPY_WARDEN.get(), 
            HappyWardenRenderer::new
        );
    }
}
