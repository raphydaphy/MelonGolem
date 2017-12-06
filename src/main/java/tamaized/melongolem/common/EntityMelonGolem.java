package tamaized.melongolem.common;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import tamaized.melongolem.MelonConfig;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.MelonSounds;
import tamaized.melongolem.network.client.ClientPacketHandlerMelonTTS;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EntityMelonGolem extends EntityGolem implements IRangedAttackMob, IShearable, IEntityAdditionalSpawnData {

	public static final TileEntitySign te = new TileEntitySign() {
		@Nonnull
		@Override
		public Block getBlockType() {
			return Blocks.WALL_SIGN;
		}

		@Override
		public int getBlockMetadata() {
			return 0;
		}
	};
	private static final DataParameter<ItemStack> HEAD = EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.ITEM_STACK);
	private static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation(MelonMod.modid, "melongolem"));
	private static final List<DataParameter<ITextComponent>> SIGN_TEXT = Lists.newArrayList(

			EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.TEXT_COMPONENT),

			EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.TEXT_COMPONENT),

			EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.TEXT_COMPONENT),

			EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.TEXT_COMPONENT)

	);
	private final float pitch;

	public EntityMelonGolem(World worldIn) {
		super(worldIn);
		this.setSize(0.7F, 1.9F);
		pitch = rand.nextFloat() * 3.0F;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(HEAD, ItemStack.EMPTY);
		for (DataParameter<ITextComponent> sign : SIGN_TEXT)
			dataManager.register(sign, new TextComponentString(""));
	}

	public void setSignText(int index, ITextComponent text) {
		dataManager.set(SIGN_TEXT.get(index), text);
	}

	public ITextComponent getSignText(int index) {
		return dataManager.get(SIGN_TEXT.get(index));
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAIAttackRanged(this, 1.25D, 20, 10.0F));
		this.tasks.addTask(2, new EntityAISearchAndEatMelons(this));
		this.tasks.addTask(3, new EntityAIWanderAvoidWater(this, 1.0D, 1.0000001E-5F));
		this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(5, new EntityAILookIdle(this));

		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 10, true, false, IMob.MOB_SELECTOR));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MelonConfig.health);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
	}

	@Override
	public void attackEntityWithRangedAttack(@Nonnull EntityLivingBase target, float distanceFactor) {
		EntityMelonSlice slice = new EntityMelonSlice(this.world, this);
		double d0 = target.posY + (double) target.getEyeHeight() - 1.100000023841858D;
		double d1 = target.posX - this.posX;
		double d2 = d0 - slice.posY;
		double d3 = target.posZ - this.posZ;
		float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
		slice.setThrowableHeading(d1, d2 + (double) f, d3, 1.6F, 12.0F);
		this.playSound(SoundEvents.ENTITY_SNOWMAN_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
		slice.setPositionAndUpdate(slice.posX, slice.posY, slice.posZ);
		this.world.spawnEntity(slice);
	}

	@Override
	public void setSwingingArms(boolean swingingArms) {

	}

	@Override
	public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos) {
		return !getHead().isEmpty();
	}

	@Override
	@Nullable
	protected ResourceLocation getLootTable() {
		return LOOT;
	}

	@Override
	protected float getSoundPitch() {
		return MelonConfig.tehnutMode ? pitch + rand.nextFloat() * 0.25F - 0.50F : super.getSoundPitch();
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return MelonConfig.tehnutMode ? MelonSounds.daddy : SoundEvents.ENTITY_SMALL_SLIME_SQUISH;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
		return SoundEvents.ENTITY_SLIME_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SLIME_DEATH;
	}

	@Override
	public int getTalkInterval() {
		return getHead().getItem() == Items.SIGN ? 200 : super.getTalkInterval();
	}

	@Override
	public void playLivingSound() {
		if (MelonConfig.tts && getHead().getItem() == Items.SIGN) {
			MelonMod.network.sendToAllAround(new ClientPacketHandlerMelonTTS.Packet(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 15));
		} else
			super.playLivingSound();
	}

	@Override
	@SuppressWarnings("deprecation")
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (!MelonConfig.hats || player.getHeldItemMainhand().getItem() instanceof ItemShears || player.getHeldItemOffhand().getItem() instanceof ItemShears)
			return false;
		ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty() && getHead().isEmpty()) {
			if (Block.getBlockFromItem(stack.getItem()) != Blocks.AIR || stack.getItem() == Items.SIGN) {
				setHead(stack);
				if (!player.isCreative())
					player.getHeldItem(hand).shrink(1);
				return true;
			}
		} else if (!getHead().isEmpty() && getHead().getItem() == Items.SIGN) {
			MelonMod.proxy.openMelonSignGui(this);
			return true;
		}
		return false;
	}

	@Override
	public float getEyeHeight() {
		return 1.7F;
	}

	public ItemStack getHead() {
		return dataManager.get(HEAD);
	}

	public void setHead(ItemStack stack) {
		for (int i = 0; i < 4; ++i)
			setSignText(i, new TextComponentString(""));
		ItemStack newstack = stack.copy();
		newstack.setCount(1);
		dataManager.set(HEAD, newstack);
	}

	@Override
	public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		List<ItemStack> list = Lists.newArrayList(MelonConfig.shear ? getHead() : ItemStack.EMPTY);
		setHead(ItemStack.EMPTY);
		return list;
	}

	@Override
	public void onDeath(@Nonnull DamageSource cause) {
		super.onDeath(cause);
		ItemStack stack = getHead();
		if (!world.isRemote && !stack.isEmpty()) {
			EntityItem e = new EntityItem(world, posX, posY, posZ, stack);
			e.motionY += rand.nextFloat() * 0.05F;
			e.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
			e.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
			world.spawnEntity(e);
		}
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		Block block = Block.getBlockFromItem(getHead().getItem());
		compound.setInteger("state", getHead().getItem() == Items.SIGN ? -100 : Block.getStateId(block.getStateFromMeta(getHead().getMetadata())));
		for (int i = 0; i < 4; ++i) {
			String s = ITextComponent.Serializer.componentToJson(getSignText(i));
			compound.setString("Text" + (i + 1), s);
		}
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		int stateid = compound.getInteger("state");
		if (stateid != -100) {
			IBlockState state = Block.getStateById(stateid);
			setHead(state.getBlock().getPickBlock(state, null, world, BlockPos.ORIGIN, null));
		} else
			setHead(new ItemStack(Items.SIGN));

		ICommandSender icommandsender = new ICommandSender() {
			@Override
			public String getName() {
				return "Sign";
			}

			@Override
			public boolean canUseCommand(int permLevel, @Nonnull String commandName) {
				return permLevel <= 2;
			}

			@Override
			public BlockPos getPosition() {
				return te.getPos();
			}

			@Override
			public Vec3d getPositionVector() {
				return new Vec3d((double) te.getPos().getX() + 0.5D, te.getPos().getY() + 0.5D, (double) te.getPos().getZ() + 0.5D);
			}

			@Nonnull
			@Override
			public World getEntityWorld() {
				return world;
			}

			@Override
			public MinecraftServer getServer() {
				return world.getMinecraftServer();
			}
		};
		for (int i = 0; i < 4; ++i) {
			String s = compound.getString("Text" + (i + 1));
			ITextComponent itextcomponent = ITextComponent.Serializer.jsonToComponent(s);

			try {
				//noinspection ConstantConditions
				setSignText(i, itextcomponent == null ? new TextComponentString("") : TextComponentUtils.processComponent(icommandsender, itextcomponent, null));
			} catch (CommandException var7) {
				setSignText(i, itextcomponent);
			}
		}
		super.readFromNBT(compound);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		ByteBufUtils.writeItemStack(buffer, getHead());
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		setHead(ByteBufUtils.readItemStack(additionalData));
	}

	static class EntityAISearchAndEatMelons extends EntityAIBase {

		private final EntityLiving parent;
		private int cooldown;
		private BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		private boolean foundMelon = false;

		EntityAISearchAndEatMelons(EntityLiving entity) {
			parent = entity;
			setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			return MelonConfig.eats && parent.getHealth() < parent.getMaxHealth();
		}

		@Override
		public void resetTask() {
			cooldown = 0;
		}

		@Override
		public void updateTask() {
			if (parent == null)
				return;
			if (cooldown > 0)
				cooldown--;
			final int radius = 25;
			AxisAlignedBB area = new AxisAlignedBB(parent.posX - radius, parent.posY - radius, parent.posZ - radius, parent.posX + radius, parent.posY + radius, parent.posZ + radius);
			List<EntityItem> items = parent.world.getEntitiesWithinAABB(EntityItem.class, area);
			for (EntityItem item : items) {
				if (parent.getNavigator().noPath() && item.getItem().getItem() == Items.MELON || item.getItem().getItem() == Item.getItemFromBlock(Blocks.MELON_BLOCK)) {
					parent.getNavigator().tryMoveToEntityLiving(item, 1.25F);
					parent.getLookHelper().setLookPositionWithEntity(item, 30.0F, 30.0F);
				}
				if (cooldown <= 0 && item.isEntityAlive() && item.getItem().getItem() == Items.MELON && item.getEntityBoundingBox().intersects(parent.getEntityBoundingBox().grow(1))) {
					item.getItem().shrink(1);
					parent.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1F, 1F);
					parent.heal(MelonConfig.heal);
					cooldown = 30 + parent.getRNG().nextInt(40);
				}
			}
			if (parent.getNavigator().noPath()) {
				foundMelon = false;
				search:
				for (int x = -radius; x < radius; x++)
					for (int y = -radius; y < radius; y++)
						for (int z = -radius; z < radius; z++) {
							pos.setPos(parent.getPosition().add(x, y, z));
							if (parent.world.isBlockLoaded(pos)) {
								TileEntity te = parent.world.getTileEntity(pos);
								if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
									IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
									if (cap != null)
										for (int i = 0; i < cap.getSlots(); i++) {
											if (cap.getStackInSlot(i).getItem() == Items.MELON) {
												foundMelon = parent.getDistance(pos.getX(), pos.getY(), pos.getZ()) < 2 || parent.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.25F);
												if (foundMelon)
													break search;
											}
										}
								}
							}
						}
			}
			if (foundMelon) {
				// Validate
				if (!parent.world.isBlockLoaded(pos)) {
					parent.getNavigator().clearPathEntity();
					foundMelon = false;
					return;
				}
				TileEntity te = parent.world.getTileEntity(pos);
				if (te == null || !te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
					parent.getNavigator().clearPathEntity();
					foundMelon = false;
					return;
				}
				IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
				if (cap == null) {
					parent.getNavigator().clearPathEntity();
					foundMelon = false;
					return;
				}
				boolean valid = false;
				int i;
				for (i = 0; i < cap.getSlots(); i++) {
					if (cap.getStackInSlot(i).getItem() == Items.MELON) {
						valid = true;
						break;
					}
				}
				if (!valid) {
					parent.getNavigator().clearPathEntity();
					foundMelon = false;
					return;
				}

				if (cooldown <= 0 && parent.getDistance(pos.getX(), pos.getY(), pos.getZ()) < 2) {
					cap.getStackInSlot(i).shrink(1);
					parent.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1F, 1F);
					parent.heal(MelonConfig.heal);
					cooldown = 10 + parent.getRNG().nextInt(40);
				}
			}
		}
	}
}
