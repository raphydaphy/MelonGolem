package tamaized.melongolem;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = MelonMod.modid, name = "MelonGolem", version = MelonMod.version, acceptedMinecraftVersions = "[1.12,)")
public class MelonMod {

	public final static String version = "${version}";
	public static final String modid = "melongolem";
	@Instance(modid)
	public static MelonMod instance = new MelonMod();
	public Logger logger;

	public static String getVersion() {
		return version;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = LogManager.getLogger(modid);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

}
