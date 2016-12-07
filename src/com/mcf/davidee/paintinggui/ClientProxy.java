package com.mcf.davidee.paintinggui;

import com.mcf.davidee.paintinggui.gui.PaintingSelectionScreen;
import com.mcf.davidee.paintinggui.packet.PacketPaintingClient;
import com.mcf.davidee.paintinggui.packet.PacketPaintingServer;
import com.mcf.davidee.paintinggui.packet.PaintingPacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;

public class ClientProxy extends NetProxy {

	@Override
	public EntityPlayer getClientPlayer() { 
		return Minecraft.getMinecraft().player; 
	}

	@Override
	public void displayPaintingSelectionScreen(PacketPaintingClient message){
		Minecraft mc =  Minecraft.getMinecraft();
		if (mc.currentScreen == null)
			mc.displayGuiScreen(new PaintingSelectionScreen(message.art, message.id));
	}
	
	@Override
	public void displayPaintingSelectionScreen(EntityPainting painting){
//		Minecraft mc =  Minecraft.getMinecraft();
//		if (mc.currentScreen == null)
//			mc.displayGuiScreen(new PaintingSelectionScreen(painting.art, painting.getEntityId()));
	}
	
	@Override
	public void processRayTracing() {
		RayTraceResult pos =  Minecraft.getMinecraft().objectMouseOver;
		if (pos != null && pos.entityHit instanceof EntityPainting) 
			PaintingPacketHandler.NETWORK.sendToServer(new PacketPaintingServer(pos.entityHit.getEntityId(), new String[0]));
		else
			PaintingSelectionMod.proxy.getClientPlayer().sendMessage(new TextComponentString(PaintingSelectionMod.COLOR + "cError - No painting selected"));
	
	}
}
