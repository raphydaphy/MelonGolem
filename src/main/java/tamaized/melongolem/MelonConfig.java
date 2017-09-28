package tamaized.melongolem;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@Config(modid = MelonMod.modid)
public class MelonConfig {

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(MelonMod.modid)) {
			ConfigManager.sync(MelonMod.modid, Config.Type.INSTANCE);
		}
	}

}
