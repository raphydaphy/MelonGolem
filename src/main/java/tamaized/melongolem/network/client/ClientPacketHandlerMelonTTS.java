package tamaized.melongolem.network.client;

import com.mojang.text2speech.Narrator;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tamaized.melongolem.common.EntityMelonGolem;

public class ClientPacketHandlerMelonTTS implements IMessageHandler<ClientPacketHandlerMelonTTS.Packet, IMessage> {

	private static Narrator narrator;

	@SideOnly(Side.CLIENT)
	private static void processPacket(Packet message, EntityPlayer player, World world) {
		Entity entity = world.getEntityByID(message.id);
		if (entity instanceof EntityMelonGolem && entity.getDistanceSqToEntity(player) <= 225) {
			if (narrator == null)
				narrator = Narrator.getNarrator();
			if (!narrator.active())
				return;
			narrator.clear();
			EntityMelonGolem golem = (EntityMelonGolem) entity;
			StringBuilder string = new StringBuilder();
			for (int i = 0; i < 4; ++i)
				string.append(TextFormatting.getTextWithoutFormattingCodes(golem.getSignText(i).getUnformattedText())).append(" ");
			narrator.say(string.toString());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(Packet message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> processPacket(message, Minecraft.getMinecraft().player, Minecraft.getMinecraft().world));
		return null;
	}

	public static class Packet implements IMessage {

		private int id;

		@SuppressWarnings("unused")
		public Packet() {

		}

		public Packet(EntityMelonGolem golem) {
			id = golem.getEntityId();
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			id = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(id);
		}
	}
}
