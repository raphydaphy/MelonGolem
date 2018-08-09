package tamaized.melongolem.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tamaized.melongolem.entity.EntityMelonGolem;

@Mixin(Item.class)
public abstract class MixinItem
{
	@Inject(method = "func_195939_a", at = @At("RETURN"))
	public void func_195939_a(ItemUseContext context, CallbackInfoReturnable info)
	{
		System.out.println("use");
		BlockPos pos = context.func_195995_a();
		World world = context.func_195991_k();
		if (world.getBlockState(pos).getBlock() == Blocks.MELON_BLOCK)
		{
			System.out.println("melon");
			EntityPlayer player = context.func_195999_j();
			if (player != null && player.getHeldItemMainhand().getItem() == Items.STICK && player.getHeldItemOffhand().getItem() == Items.STICK)
			{
				System.out.println("stick " + world.getBlockState(pos.up()).getBlock().equals(Blocks.MELON_BLOCK));
				if (world.getBlockState(pos.down()).getBlock().equals(Blocks.MELON_BLOCK))
				{
					System.out.println("more melon");
					if (world.getBlockState(pos.up()).getBlock().equals(Blocks.MELON_BLOCK))
					{
						System.out.println("so many melon");
						if (!world.isRemote)
						{
							System.out.println("do");
							if (!player.isCreative())
							{
								player.getHeldItem(EnumHand.MAIN_HAND).shrink(1);
								player.getHeldItem(EnumHand.OFF_HAND).shrink(1);
								player.openContainer.detectAndSendChanges();
							}

							world.setBlockToAir(pos.down());
							world.setBlockToAir(pos);
							world.setBlockToAir(pos.up());

							EntityMelonGolem golem = new EntityMelonGolem(world);
							golem.setPositionAndUpdate(pos.getX() + 0.5F, pos.getY() - 0.15F, pos.getZ() + 0.5f);
							world.spawnEntity(golem);

						}
					}
				}
			}
		}
	}
}
