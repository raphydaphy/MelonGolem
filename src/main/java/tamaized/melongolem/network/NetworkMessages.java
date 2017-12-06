package tamaized.melongolem.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import tamaized.melongolem.network.client.ClientPacketHandlerMelonTTS;
import tamaized.melongolem.network.server.ServerPacketHandlerMelonSign;

public class NetworkMessages {

	private static int index = 0;

	public static void register(SimpleNetworkWrapper network) {
		registerMessage(network, ServerPacketHandlerMelonSign.class, ServerPacketHandlerMelonSign.Packet.class, Side.SERVER);

		registerMessage(network, ClientPacketHandlerMelonTTS.class, ClientPacketHandlerMelonTTS.Packet.class, Side.CLIENT);
	}

	private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(SimpleNetworkWrapper network, Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		network.registerMessage(messageHandler, requestMessageType, index, side);
		index++;
	}
}
