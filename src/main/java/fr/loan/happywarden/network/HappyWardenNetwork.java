package fr.loan.happywarden.network;

import java.util.function.Supplier;

import fr.loan.happywarden.HappyWarden;
import fr.loan.happywarden.entity.HappyWardenEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class HappyWardenNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(HappyWarden.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private HappyWardenNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(0, JumpMessage.class, JumpMessage::encode, JumpMessage::decode, JumpMessage::handle);
    }

    public static class JumpMessage {
        public JumpMessage() {
        }

        private static void encode(JumpMessage message, PacketBuffer buffer) {
        }

        private static JumpMessage decode(PacketBuffer buffer) {
            return new JumpMessage();
        }

        private static void handle(JumpMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                ServerPlayerEntity player = context.getSender();
                if (player != null && player.getRidingEntity() instanceof HappyWardenEntity) {
                    ((HappyWardenEntity) player.getRidingEntity()).jumpFromRider(player);
                }
            });
            context.setPacketHandled(true);
        }
    }
}