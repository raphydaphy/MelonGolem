package tamaized.melongolem;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import tamaized.melongolem.client.RenderMelonGolem;
import tamaized.melongolem.client.RenderMelonSlice;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.common.EntityMelonSlice;

public class ClientProxy implements IModProxy {

	@Override
	public void preinit() {
		RenderingRegistry.registerEntityRenderingHandler(EntityMelonGolem.class, RenderMelonGolem::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMelonSlice.class, RenderMelonSlice::new);
	}

	@Override
	public void init() {

	}

	@Override
	public void postInit() {

	}
}
