package fr.loan.happywarden.event;

import org.lwjgl.glfw.GLFW;

import fr.loan.happywarden.HappyWarden;
import fr.loan.happywarden.entity.HappyWardenEntity;
import fr.loan.happywarden.network.HappyWardenNetwork;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HappyWarden.MOD_ID, value = Dist.CLIENT)
public class HappyWardenInputEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (event.getAction() != GLFW.GLFW_PRESS || minecraft.player == null
                || !minecraft.gameSettings.keyBindJump.matchesKey(event.getKey(), event.getScanCode())
                || !(minecraft.player.getRidingEntity() instanceof HappyWardenEntity)) {
            return;
        }

        HappyWardenEntity mount = (HappyWardenEntity) minecraft.player.getRidingEntity();
        mount.jumpFromRider(minecraft.player);
        HappyWardenNetwork.CHANNEL.sendToServer(new HappyWardenNetwork.JumpMessage());
    }
}