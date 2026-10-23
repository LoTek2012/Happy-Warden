package fr.loan.happywarden.client.renderer.happy_warden;

import fr.loan.happywarden.client.model.happy_warden.HappyWardenModel;
import fr.loan.happywarden.entity.happy_warden.HappyWardenEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class HappyWardenOverlayLayer<T extends HappyWardenEntity, M extends HappyWardenModel<T>> extends LayerRenderer<T, M> {

    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(HappyWarden.MOD_ID, "textures/entity/happy_warden_overlay.png");

}
