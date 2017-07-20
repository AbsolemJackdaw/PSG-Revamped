package com.mcf.davidee.paintinggui.packet;


import com.mcf.davidee.paintinggui.packet.PacketPaintingClient.PaintingMessageHandlerClient;
import com.mcf.davidee.paintinggui.packet.PacketPaintingServer.PaintingMessageHandlerServer;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PaintingPacketHandler {
	
	private static final String CHANNEL = "Paint_Select_Gui";
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
	
	public PaintingPacketHandler() {
		NETWORK.registerMessage(PaintingMessageHandlerServer.class, PacketPaintingServer.class, 0, Side.SERVER);
		NETWORK.registerMessage(PaintingMessageHandlerClient.class, PacketPaintingClient.class, 1, Side.CLIENT);
	}
}
