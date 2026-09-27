package fr.loan.happywarden.client.renderer.happy_warden;

import fr.loan.happywarden.entity.HappyWardenEntity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.ResourceLocation;

public class HappyWardenSaddleLayer<T extends HappyWardenEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {

    // Emplacement de la texture de la selle (à adapter selon le chemin de ton mod)
    private static final ResourceLocation SADDLE_TEXTURE = new ResourceLocation("happywarden", "textures/entity/happy_warden/happy_warden_saddle.png");
    private final M model;

    public HappyWardenSaddleLayer(IEntityRenderer<T, M> renderer, M model) {
        super(renderer);
        this.model = model;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        // Affiche la selle UNIQUEMENT si l'entité a une selle
        if (entity.isSaddled()) {
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
            
            // Effectue le rendu avec la texture de la selle
            renderColoredCutoutModel(this.model, SADDLE_TEXTURE, matrixStack, buffer, packedLight, entity, 1.0F, 1.0F, 1.0F);
        }
    }
}