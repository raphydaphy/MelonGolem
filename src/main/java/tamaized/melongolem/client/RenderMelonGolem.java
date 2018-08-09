package tamaized.melongolem.client;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.model.ModelSnowMan;
import net.minecraft.util.ResourceLocation;
import tamaized.melongolem.Constants;
import tamaized.melongolem.entity.EntityMelonGolem;

import javax.annotation.Nonnull;

public class RenderMelonGolem extends RenderLiving<EntityMelonGolem>
{
	private static final ResourceLocation TEXTURES = new ResourceLocation(Constants.MOD_ID, "textures/entity/golem.png");

	public RenderMelonGolem(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelSnowMan(), 0.5F);
		this.addLayer(new LayerMelonHead(this));
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityMelonGolem entity) {
		return TEXTURES;
	}
}