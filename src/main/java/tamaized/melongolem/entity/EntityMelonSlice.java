package tamaized.melongolem.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import tamaized.melongolem.MelonGolem;

import javax.annotation.Nonnull;

public class EntityMelonSlice extends EntityThrowable
{
	public EntityMelonSlice(World worldIn)
	{
		super(MelonGolem.MELON_SLICE, worldIn);
	}

	public EntityMelonSlice(World worldIn, EntityLivingBase throwerIn)
	{
		super(MelonGolem.MELON_SLICE, throwerIn, worldIn);
	}

	public EntityMelonSlice(World worldIn, double x, double y, double z)
	{
		super(MelonGolem.MELON_SLICE, x, y, z, worldIn);
	}

	@Override
	public void handleStatusUpdate(byte id)
	{
		if (id == 3)
		{
			for (int i = 0; i < 8; ++i)
			{
				ItemParticleData data = new ItemParticleData(Particles.ITEM, new ItemStack(rand.nextInt() == 0 ? Items.MELON_SEEDS : Items.MELON));
				this.world.addParticle(data, this.posX, this.posY, this.posZ, 0, 0, 0);
			}
		}
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult result)
	{
		if (result.entityHit != null)
		{
			if (result.entityHit == getThrower()) return;
			result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), MelonGolem.config.damage);
		}

		if (!this.world.isRemote)
		{
			this.world.setEntityState(this, (byte) 3);
			this.setDead();
		}
	}
}