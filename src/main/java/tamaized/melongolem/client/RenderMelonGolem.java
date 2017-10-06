package tamaized.melongolem.client;

import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityMelonGolem;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderMelonGolem extends RenderLiving<EntityMelonGolem> {
	private static final ResourceLocation TEXTURES = new ResourceLocation(MelonMod.modid, "textures/entity/golem.png");

	public RenderMelonGolem(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelSnowMan(), 0.5F);
		this.addLayer(new LayerMelonHead(this));
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityMelonGolem entity) {
		return TEXTURES;
	}
}