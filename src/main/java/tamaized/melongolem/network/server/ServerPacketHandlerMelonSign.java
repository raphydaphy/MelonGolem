package tamaized.melongolem.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tamaized.melongolem.common.EntityMelonGolem;

public class ServerPacketHandlerMelonSign implements IMessageHandler<ServerPacketHandlerMelonSign.Packet, IMessage> {

	private static void processPacket(Packet message, EntityPlayerMP player, World world) {
		Entity entity = world.getEntityByID(message.id);
		if (entity instanceof EntityMelonGolem && entity.getDistanceToEntity(player) <= 6)
			for (int i = 0; i < message.lines.length; ++i) {
				String text = TextFormatting.getTextWithoutFormattingCodes(message.lines[i]);
				((EntityMelonGolem) entity).setSignText(i, new TextComponentString(text == null ? "" : text));
			}

	}

	@Override
	public IMessage onMessage(Packet message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().player;
		MinecraftServer server = player.getServer();
		if (server != null)
			server.addScheduledTask(() -> processPacket(message, player, player.world));
		return null;
	}

	public static class Packet implements IMessage {

		private int id;
		private String[] lines = new String[4];

		@SuppressWarnings("unused")
		public Packet() {

		}

		public Packet(EntityMelonGolem golem) {
			id = golem.getEntityId();
			lines = new String[]{golem.getSignText(0).getUnformattedText(), golem.getSignText(1).getUnformattedText(), golem.getSignText(2).getUnformattedText(), golem.getSignText(3).getUnformattedText()};
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			id = buf.readInt();
			for (int i = 0; i < 4; ++i) {
				this.lines[i] = ByteBufUtils.readUTF8String(buf);
			}
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(id);
			for (int i = 0; i < 4; ++i) {
				ByteBufUtils.writeUTF8String(buf, lines[i]);
			}
		}
	}
}
