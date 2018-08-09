package tamaized.melongolem.entity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import tamaized.melongolem.Constants;
import tamaized.melongolem.MelonGolem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EntityMelonGolem extends EntityGolem implements IRangedAttackMob
{
	private static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation(Constants.MOD_ID, "melongolem"));
	private static final DataParameter<ItemStack> HEAD = EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.ITEM_STACK);

	private final float pitch;

	public EntityMelonGolem(World world)
	{
		super(MelonGolem.MELON_GOLEM, world);
		this.setSize(0.7F, 1.9F);
		pitch = rand.nextFloat() * 3.0F;
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		dataManager.register(HEAD, ItemStack.EMPTY);
	}

	@Override
	protected void initEntityAI()
	{
		this.tasks.addTask(1, new EntityAIAttackRanged(this, 1.25D, 20, 10.0F));
		this.tasks.addTask(2, new EntityAISearchAndEatMelons(this));
		this.tasks.addTask(3, new EntityAIWanderAvoidWater(this, 1.0D, 1.0000001E-5F));
		this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(5, new EntityAILookIdle(this));

		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 10, true, false, IMob.MOB_SELECTOR));
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
	{
		EntityMelonSlice slice = new EntityMelonSlice(this.world, this);
		double d0 = target.posY + (double) target.getEyeHeight() - 1.100000023841858D;
		double d1 = target.posX - this.posX;
		double d2 = d0 - slice.posY;
		double d3 = target.posZ - this.posZ;
		float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
		slice.shoot(d1, d2 + (double) f, d3, 1.6F, 12.0F);
		this.playSound(SoundEvents.ENTITY_SNOWMAN_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
		slice.setPositionAndUpdate(slice.posX, slice.posY, slice.posZ);
		this.world.spawnEntity(slice);
	}

	@Override
	public void setSwingingArms(boolean b)
	{

	}

	public ItemStack getHead()
	{
		return dataManager.get(HEAD);
	}

	public void setHead(ItemStack stack)
	{
		ItemStack newstack = stack.copy();
		newstack.setCount(1);
		dataManager.set(HEAD, newstack);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		Block block = Block.getBlockFromItem(getHead().getItem());
		compound.setInteger("state", Block.getStateId(block.getBlockState().getBaseState()));
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		int stateid = compound.getInteger("state");
		IBlockState state = Block.getStateById(stateid);
		setHead(state.getBlock().getItem(world, null, state));
		super.readFromNBT(compound);
	}

	@Override
	@Nullable
	protected ResourceLocation getLootTable()
	{
		return LOOT;
	}

	@Override
	protected float getSoundPitch()
	{
		return MelonGolem.config.tehnutMode ? pitch + rand.nextFloat() * 0.25F - 0.50F : super.getSoundPitch();
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound()
	{
		return MelonGolem.config.tehnutMode ? MelonGolem.MELON_DADDY : SoundEvents.ENTITY_SMALL_SLIME_SQUISH;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource p_184601_1_)
	{
		return SoundEvents.ENTITY_SLIME_HURT;
	}

	@Override
	public float getEyeHeight()
	{
		return 1.7F;
	}

	/*
	public void writeSpawnData(ByteBuf buffer)
	{
		PacketBuffer pbuf = new PacketBuffer(buffer);
		pbuf.writeItemStack(getHead());
	}

	public void readSpawnData(ByteBuf additionalData)
	{
		PacketBuffer pbuf = new PacketBuffer(additionalData);
		setHead(pbuf.readItemStack());
	}*/

	@Nullable
	@Override
	protected SoundEvent getDeathSound()
	{
		return SoundEvents.ENTITY_SLIME_DEATH;
	}


	static class EntityAISearchAndEatMelons extends EntityAIBase
	{

		private final EntityLiving parent;
		private int cooldown;
		private BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		private boolean foundMelon = false;

		EntityAISearchAndEatMelons(EntityLiving entity)
		{
			parent = entity;
			setMutexBits(3);
		}

		@Override
		public boolean shouldExecute()
		{
			return MelonGolem.config.eats && parent.getHealth() < parent.getMaxHealth();
		}

		@Override
		public void resetTask()
		{
			cooldown = 0;
		}

		@Override
		public void updateTask()
		{
			if (parent == null) return;
			if (cooldown > 0) cooldown--;
			final int radius = 25;
			AxisAlignedBB area = new AxisAlignedBB(parent.posX - radius, parent.posY - radius, parent.posZ - radius, parent.posX + radius, parent.posY + radius, parent.posZ + radius);
			List<EntityItem> items = parent.world.getEntitiesWithinAABB(EntityItem.class, area);
			for (EntityItem item : items)
			{
				if (parent.getNavigator().noPath() && item.getItem().getItem() == Items.MELON || item.getItem().getItem() == Item.getItemFromBlock(Blocks.MELON_BLOCK))
				{
					parent.getNavigator().tryMoveToEntityLiving(item, 1.25F);
					parent.getLookHelper().setLookPositionWithEntity(item, 30.0F, 30.0F);
				}
				if (cooldown <= 0 && item.isEntityAlive() && item.getItem().getItem() == Items.MELON && item.getEntityBoundingBox().intersects(parent.getEntityBoundingBox().grow(1)))
				{
					item.getItem().shrink(1);
					parent.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1F, 1F);
					parent.heal(MelonGolem.config.heal);
					cooldown = 30 + parent.getRNG().nextInt(40);
				}
			}
			if (parent.getNavigator().noPath())
			{
				foundMelon = false;
				search:
				for (int x = -radius; x < radius; x++)
					for (int y = -radius; y < radius; y++)
						for (int z = -radius; z < radius; z++)
						{
							pos.setPos(parent.getPosition().add(x, y, z));
							if (parent.world.isBlockLoaded(pos))
							{
								TileEntity te = parent.world.getTileEntity(pos);
								if (te != null && te instanceof IInventory)
								{
									IInventory inv = (IInventory) te;
									if (inv != null) for (int i = 0; i < inv.getSizeInventory(); i++)
									{
										if (inv.getStackInSlot(i).getItem() == Items.MELON)
										{
											foundMelon = parent.getDistance(pos.getX(), pos.getY(), pos.getZ()) < 2 || parent.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.25F);
											if (foundMelon) break search;
										}
									}
								}
							}
						}
			}
			if (foundMelon)
			{
				// Validate
				if (!parent.world.isBlockLoaded(pos))
				{
					parent.getNavigator().clearPath();
					foundMelon = false;
					return;
				}
				TileEntity te = parent.world.getTileEntity(pos);
				if (te == null || !(te instanceof IInventory))
				{
					parent.getNavigator().clearPath();
					foundMelon = false;
					return;
				}
				IInventory inv = (IInventory) te;
				if (inv == null)
				{
					parent.getNavigator().clearPath();
					foundMelon = false;
					return;
				}
				boolean valid = false;
				int i;
				for (i = 0; i < inv.getSizeInventory(); i++)
				{
					if (inv.getStackInSlot(i).getItem() == Items.MELON)
					{
						valid = true;
						break;
					}
				}
				if (!valid)
				{
					parent.getNavigator().clearPath();
					foundMelon = false;
					return;
				}

				if (cooldown <= 0 && parent.getDistance(pos.getX(), pos.getY(), pos.getZ()) < 2)
				{
					inv.getStackInSlot(i).shrink(1);
					parent.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1F, 1F);
					parent.heal(MelonGolem.config.heal);
					cooldown = 10 + parent.getRNG().nextInt(40);
				}
			}
		}
	}
}
