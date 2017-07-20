package com.mcf.davidee.paintinggui;

import com.mcf.davidee.paintinggui.packet.PacketPaintingClient;
import com.mcf.davidee.paintinggui.packet.PaintingPacketHandler;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;


public class CommandPainting extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		EntityPlayerMP player = (EntityPlayerMP)sender;
		PaintingPacketHandler.NETWORK.sendTo(new PacketPaintingClient(-1, new String[0]), player);
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender instanceof EntityPlayer;
	}

	@Override
	public String getName() {
		return "painting";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/painting";
	}
}
