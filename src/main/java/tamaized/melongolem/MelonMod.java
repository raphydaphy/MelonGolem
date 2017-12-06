package tamaized.melongolem;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.common.EntityMelonSlice;
import tamaized.melongolem.network.NetworkMessages;

@Mod(modid = MelonMod.modid, name = "EntityMelonGolem", version = MelonMod.version, acceptedMinecraftVersions = "[1.12,)")
public class MelonMod {

	public final static String version = "${version}";
	public static final String modid = "melongolem";
	@Instance(modid)
	public static MelonMod instance = new MelonMod();
	@SidedProxy(clientSide = "tamaized.melongolem.ClientProxy", serverSide = "tamaized.melongolem.ServerProxy")
	public static IModProxy proxy;
	private static int entityID;
	public Logger logger;
	public static SimpleNetworkWrapper network;

	public static String getVersion() {
		return version;
	}

	@SuppressWarnings("SameParameterValue")
	private static void registerEntity(String name, Class<? extends Entity> entityClass, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggPrimary, int eggSecondary) {
		ResourceLocation entityName = new ResourceLocation(modid, name);
		EntityRegistry.registerModEntity(entityName, entityClass, entityName.getResourceDomain() + "." + entityName.getResourcePath(), entityID++, instance, trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimary, eggSecondary);
	}

	@SuppressWarnings("SameParameterValue")
	private static void registerEntity(String name, Class<? extends Entity> entityClass, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
		ResourceLocation entityName = new ResourceLocation(modid, name);
		EntityRegistry.registerModEntity(entityName, entityClass, entityName.getResourceDomain() + "." + entityName.getResourcePath(), entityID++, instance, trackingRange, updateFrequency, sendsVelocityUpdates);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = LogManager.getLogger(modid);

		NetworkMessages.register(network = NetworkRegistry.INSTANCE.newSimpleChannel(modid));

		registerEntity("melon_golem", EntityMelonGolem.class, 128, 1, true, 0xFF00, 0x0);
		registerEntity("melon_slice", EntityMelonSlice.class, 128, 1, true);

		proxy.preinit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}

}
