package com.mcf.davidee.paintinggui;

import java.util.Arrays;

import com.mcf.davidee.paintinggui.gui.ArtComparator;
import com.mcf.davidee.paintinggui.handler.PlacePaintingEventHandler;
import com.mcf.davidee.paintinggui.packet.PaintingPacketHandler;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;


@Mod(modid = "paintingselgui", name = "PaintingSelectionGui", version = "1.11 v1: updated by Subaraki", dependencies = "after:guilib")
public class PaintingSelectionMod {

	public static final String CHANNEL = "PaintingSelGui";
	public static final char COLOR = '\u00A7';

	public static final ArtComparator ART_COMPARATOR = new ArtComparator();

	@SidedProxy(clientSide="com.mcf.davidee.paintinggui.ClientProxy", serverSide="com.mcf.davidee.paintinggui.NetProxy")
	public static NetProxy proxy;

	@EventHandler 
	public void preInit(FMLPreInitializationEvent event) {
		new PlacePaintingEventHandler();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		new PaintingPacketHandler();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandPainting());
	}


}
