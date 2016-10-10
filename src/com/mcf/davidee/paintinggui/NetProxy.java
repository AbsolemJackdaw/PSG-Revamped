package com.mcf.davidee.paintinggui;

import com.mcf.davidee.paintinggui.packet.PacketPaintingClient;

import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;

public class NetProxy {
	
	public EntityPlayer getClientPlayer() { 
		return null; 
	}
	
	public void displayPaintingSelectionScreen(PacketPaintingClient message){};
	public void displayPaintingSelectionScreen(EntityPainting painting){};

	public void processRayTracing(){};
}
