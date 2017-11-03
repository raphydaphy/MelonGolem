package tamaized.melongolem;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@Config(modid = MelonMod.modid)
public class MelonConfig {

	@Config.Name("Base Golem Health")
	public static double health = 8.0D;

	@Config.Name("Melon Slice Damage")
	public static float damage = 4.0F;

	@Config.Name("Enable Golem Block Heads")
	public static boolean hats = true;

	@Config.Name("Shears Spawn Block")
	public static boolean shear = true;

	@Config.Name("Golem Eats Melons")
	public static boolean eats = true;

	@Config.Name("Melon Heal Amount")
	public static float heal = 1.0F;

	@Config.Name("TehNut Mode")
	public static boolean tehnutMode = false;

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(MelonMod.modid)) {
			ConfigManager.sync(MelonMod.modid, Config.Type.INSTANCE);
		}
	}

}
