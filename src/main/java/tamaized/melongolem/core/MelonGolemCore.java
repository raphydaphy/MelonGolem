package tamaized.melongolem.core;

import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.mixin.Mixins;

public class MelonGolemCore implements InitializationListener
{
	@Override
	public void onInitialization()
	{
		Mixins.addConfiguration("mixins.melongolem.json");
	}
}
