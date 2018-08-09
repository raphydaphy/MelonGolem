package tamaized.melongolem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.dimdev.rift.listener.EntityTypeAdder;
import org.dimdev.rift.listener.SoundAdder;
import org.dimdev.rift.listener.client.ClientTickable;
import org.dimdev.rift.listener.client.EntityRendererAdder;
import tamaized.melongolem.client.RenderMelonGolem;
import tamaized.melongolem.client.RenderMelonSlice;
import tamaized.melongolem.entity.EntityMelonGolem;
import tamaized.melongolem.entity.EntityMelonSlice;

import java.io.File;
import java.util.Map;

public class MelonGolem implements EntityTypeAdder, SoundAdder, ClientTickable, EntityRendererAdder
{
	public static EntityType MELON_GOLEM;
	public static EntityType MELON_SLICE;

	public static SoundEvent MELON_DADDY;

	public static MelonConfig config;
	private static boolean readConfig = false;
	private static boolean initialized = false;

	public static void init()
	{
		config = new MelonConfig(Minecraft.getMinecraft().gameDir + File.separator + "config" + File.separator + "MelonGolem.cfg");
		config.read();
	}

	@Override
	public void registerSounds()
	{
		MELON_DADDY = registerSound("melonmedaddy");
	}

	private SoundEvent registerSound(String name)
	{
		SoundEvent sound = new SoundEvent(new ResourceLocation(Constants.MOD_ID, name));
		SoundEvent.registerSound(sound.getSoundName().toString());
		return sound;
	}

	@Override
	public void clientTick()
	{
		if (!initialized)
		{
			init();
			initialized = true;
		}
	}

	@Override
	public void registerEntityTypes()
	{
		MELON_GOLEM = EntityType.registerEntityType(Constants.MOD_ID + ":melongolem", EntityType.Builder.create(EntityMelonGolem.class, EntityMelonGolem::new));
		MELON_SLICE = EntityType.registerEntityType(Constants.MOD_ID + ":melonslice", EntityType.Builder.create(EntityMelonSlice.class, EntityMelonSlice::new));
	}

	@Override
	public void addEntityRenderers(Map<Class<? extends Entity>, Render<? extends Entity>> entityRenderMap, RenderManager renderManager)
	{
		entityRenderMap.put(EntityMelonGolem.class, new RenderMelonGolem(renderManager));
		entityRenderMap.put(EntityMelonSlice.class, new RenderMelonSlice(renderManager));
	}
}
