package tamaized.melongolem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tamaized.melongolem.common.EntityMelonGolem;

@Mod.EventBusSubscriber(modid = MelonMod.modid)
public class ModEventListener {

	@SubscribeEvent
	public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock e) {
		EntityPlayer player = e.getEntityPlayer();
		World world = e.getWorld();
		BlockPos pos = e.getPos();
		if (!world.isRemote && world.getBlockState(pos).getBlock() == Blocks.MELON_BLOCK && player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.STICK && player.getHeldItem(EnumHand.OFF_HAND).getItem() == Items.STICK) {
			if (world.getBlockState(pos.down()).getBlock() == Blocks.MELON_BLOCK && world.getBlockState(pos.up()).getBlock() == Blocks.MELON_BLOCK) {
				if (!player.isCreative()) {
					player.getHeldItem(EnumHand.MAIN_HAND).shrink(1);
					player.getHeldItem(EnumHand.OFF_HAND).shrink(1);
				}
				world.setBlockToAir(pos.down());
				world.setBlockToAir(pos);
				world.setBlockToAir(pos.up());
				EntityMelonGolem melon = new EntityMelonGolem(world);
				melon.setPositionAndUpdate(pos.getX() + 0.5F, pos.getY() - 0.5F, pos.getZ() + 0.5F);
				world.spawnEntity(melon);
			}
		}
	}

}
