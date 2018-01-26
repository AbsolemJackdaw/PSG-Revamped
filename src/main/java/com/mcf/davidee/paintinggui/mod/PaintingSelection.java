package com.mcf.davidee.paintinggui.mod;

import com.mcf.davidee.paintinggui.command.CommandPainting;
import com.mcf.davidee.paintinggui.gui.ArtComparator;
import com.mcf.davidee.paintinggui.handler.PlacePaintingEventHandler;
import com.mcf.davidee.paintinggui.packet.NetworkHandler;
import com.mcf.davidee.paintinggui.proxy.ServerProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;


@Mod(modid = "paintingselgui", name = "PaintingSelectionGui", version = "$version", dependencies = "after:guilib")
public class PaintingSelection {

	public static final String CHANNEL = "PaintingSelGui";
	public static final char COLOR = '\u00A7';

	public static final ArtComparator ART_COMPARATOR = new ArtComparator();

	@SidedProxy(clientSide="com.mcf.davidee.paintinggui.ClientProxy", serverSide="com.mcf.davidee.paintinggui.NetProxy")
	public static ServerProxy proxy;

	@EventHandler 
	public void preInit(FMLPreInitializationEvent event) {
		new NetworkHandler();
		new PlacePaintingEventHandler();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandPainting());
	}
}