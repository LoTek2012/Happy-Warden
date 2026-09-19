package fr.loan.happywarden.client.renderer.happy_warden;

import fr.loan.happywarden.HappyWarden;
import fr.loan.happywarden.client.model.HappyWardenModel;
import fr.loan.happywarden.entity.HappyWardenEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class HappyWardenRenderer extends MobRenderer<HappyWardenEntity, HappyWardenModel<HappyWardenEntity>> {

    // Emplacement exact de l'image PNG de la texture (à partire de textures/)
    private static final ResourceLocation TEXTURE = 
        new ResourceLocation(HappyWarden.MOD_ID, "textures/entity/happy_warden/happy_warden.png");

    public HappyWardenRenderer(EntityRendererManager renderManager) {
        super(renderManager, new HappyWardenModel<>(), 0.7F); // 0.7F = Taille de l'ombre au sol
    }

    @Override
    public ResourceLocation getTextureLocation(HappyWardenEntity entity) {
        return TEXTURE;
    }
}