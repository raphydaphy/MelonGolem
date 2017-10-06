package tamaized.melongolem.common;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class EntityMelonSlice extends EntityThrowable {

	@SuppressWarnings("unused")
	public EntityMelonSlice(World worldIn) {
		super(worldIn);
	}

	public EntityMelonSlice(World worldIn, EntityLivingBase throwerIn) {
		super(worldIn, throwerIn);
	}

	@SuppressWarnings("unused")
	public EntityMelonSlice(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 3) {
			for (int i = 0; i < 8; ++i) {
				this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, Item.getIdFromItem(rand.nextInt() == 0 ? Items.MELON_SEEDS : Items.MELON), 0);
			}
		}
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult result) {
		if (result.entityHit != null) {
			if(result.entityHit == getThrower())
				return;
			int i = 4;

			result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), (float) i);
		}

		if (!this.world.isRemote) {
			this.world.setEntityState(this, (byte) 3);
			this.setDead();
		}
	}
}
