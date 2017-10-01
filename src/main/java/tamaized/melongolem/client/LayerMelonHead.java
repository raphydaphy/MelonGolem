package tamaized.melongolem.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;
import tamaized.melongolem.common.EntityMelonGolem;

import javax.annotation.Nonnull;

public class LayerMelonHead implements LayerRenderer<EntityMelonGolem> {
	private final RenderMelonGolem renderer;

	public LayerMelonHead(RenderMelonGolem render) {
		renderer = render;
	}

	@Override
	public void doRenderLayer(@Nonnull EntityMelonGolem entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		ItemStack stack = entity.getHead();
		if (!entity.isInvisible() || !stack.isEmpty()) {
			GlStateManager.pushMatrix();
			((ModelSnowMan) renderer.getMainModel()).head.postRender(0.0625F);
			GlStateManager.translate(0.0F, -0.34375F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.scale(0.625F, -0.625F, -0.625F);
			Minecraft.getMinecraft().getItemRenderer().renderItem(entity, stack, ItemCameraTransforms.TransformType.HEAD);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return true;
	}
}
