package com.mcf.davidee.paintinggui.proxy;

import com.mcf.davidee.paintinggui.gui.PaintingSelectionScreen;
import com.mcf.davidee.paintinggui.mod.PaintingSelection;
import com.mcf.davidee.paintinggui.packet.CPacketPainting;
import com.mcf.davidee.paintinggui.packet.SPacketPainting;
import com.mcf.davidee.paintinggui.packet.NetworkHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;

public class ClientProxy extends ServerProxy {

	@Override
	public EntityPlayer getClientPlayer() { 
		return Minecraft.getMinecraft().player; 
	}

	@Override
	public void displayPaintingSelectionScreen(CPacketPainting message){
		Minecraft mc =  Minecraft.getMinecraft();
		if (mc.currentScreen == null)
			mc.displayGuiScreen(new PaintingSelectionScreen(message.art, message.id));
	}
	
	@Override
	public void processRayTracing() {
		RayTraceResult pos =  Minecraft.getMinecraft().objectMouseOver;
		if (pos != null && pos.entityHit instanceof EntityPainting) 
			NetworkHandler.NETWORK.sendToServer(new SPacketPainting(pos.entityHit.getEntityId(), new String[0]));
		else
			PaintingSelection.proxy.getClientPlayer().sendMessage(new TextComponentString(PaintingSelection.COLOR + "Error - No painting selected"));
	
	}
}
