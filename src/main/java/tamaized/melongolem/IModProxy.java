package tamaized.melongolem;

import tamaized.melongolem.common.EntityMelonGolem;

public interface IModProxy {

	void preinit();

	void init();

	void postInit();

	void openMelonSignGui(EntityMelonGolem golem);

}
