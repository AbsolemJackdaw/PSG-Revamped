package com.mcf.davidee.paintinggui.packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mcf.davidee.paintinggui.mod.PaintingSelection;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityPainting.EnumArt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketPainting implements IMessage{

	// A default constructor is always required
	public SPacketPainting(){}

	public int id;
	public String[] art;

	public SPacketPainting(int toSend, String[] data) {
		this.id = toSend;
		art = data;
	}

	@Override 
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeInt(art.length);
		for(int i = 0; i < art.length; i++)
			ByteBufUtils.writeUTF8String(buf, art[i]);
	}

	@Override 
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
		String[] s = new String[buf.readInt()];
		for(int i = 0; i < s.length; i++){
			s[i] = ByteBufUtils.readUTF8String(buf);
		}
		art = s;
	}

	public static class SPaintingMessageHandler implements IMessageHandler<SPacketPainting, IMessage> {

		@Override 
		public IMessage onMessage(SPacketPainting message, MessageContext ctx) {
			((WorldServer)ctx.getServerHandler().player.world).addScheduledTask(() -> {
				handleServerSide(ctx.getServerHandler().player, message);
			});
			return null;
		}

		private void handleServerSide(EntityPlayerMP player, SPacketPainting packet){
			if (packet.art.length == 1) { //Set Painting
				EnumArt enumArt = getEnumArt(packet.art[0]);
				Entity e = player.world.getEntityByID(packet.id);
				if (e instanceof EntityPainting) {
					setPaintingArt((EntityPainting)e, enumArt);
					NetworkHandler.NETWORK.sendToDimension(new CPacketPainting(packet.id, new String[]{enumArt.title}), e.dimension);
				}
				else
					player.sendMessage(new TextComponentString(PaintingSelection.COLOR + "Error - Could not locate painting"));
			}
			else { //Send possible paintings
				Entity e = player.world.getEntityByID(packet.id);
				if (e instanceof EntityPainting) {
					EntityPainting painting = (EntityPainting)e;
					EnumArt origArt = painting.art;

					List<EnumArt> validArts = new ArrayList<EnumArt>();
					for(EnumArt art : EnumArt.values()){
						setPaintingArt(painting, art);
						if (painting.onValidSurface())
							validArts.add(art);
					}
					EnumArt[] validArtsArray = validArts.toArray(new EnumArt[0]);
					Arrays.sort(validArtsArray, PaintingSelection.ART_COMPARATOR);
					String[] names = new String[validArtsArray.length];
					for (int i =0; i < validArtsArray.length; ++i)
						names[i] = validArtsArray[i].title;

					NetworkHandler.NETWORK.sendTo(new CPacketPainting(packet.id, names), player);

					//Reset the art
					setPaintingArt(painting, origArt);
				}
				else
					player.sendMessage(new TextComponentString(PaintingSelection.COLOR + "cError - Could not locate painting"));
			}
		}

		protected EnumArt getEnumArt(String artName) {
			for (EnumArt art : EnumArt.values())
				if (art.title.equals(artName))
					return art;
			return EnumArt.KEBAB;
		}

		protected void setPaintingArt(EntityPainting p, EnumArt art) {

			//force a boundingbox update by reading the data of the entity
			NBTTagCompound tag = new NBTTagCompound();
			p.writeEntityToNBT(tag);
			tag.setString("Motive", art.title); //change art here, so it won't look like the painting moved
			p.readEntityFromNBT(tag);
		}
	}
}