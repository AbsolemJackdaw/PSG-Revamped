package com.mcf.davidee.paintinggui.packet;

import com.mcf.davidee.paintinggui.PaintingSelectionMod;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityPainting.EnumArt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketPaintingClient implements IMessage{

	// A default constructor is always required
	public PacketPaintingClient(){}

	public int id;
	public String[] art;

	public PacketPaintingClient(int toSend, String[] data) {
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

	public static class PaintingMessageHandlerClient implements IMessageHandler<PacketPaintingClient, IMessage> {
		@Override 
		public IMessage onMessage(PacketPaintingClient message, MessageContext ctx) {

			Minecraft.getMinecraft().addScheduledTask( () -> {
				if(message.id == -1) { //What painting is selected?
					PaintingSelectionMod.proxy.processRayTracing();
				}
				else if (message.art.length == 1) { //Set Painting
					EnumArt enumArt = getEnumArt(message.art[0]);
					Entity e = PaintingSelectionMod.proxy.getClientPlayer().worldObj.getEntityByID(message.id);
					if (e instanceof EntityPainting){
						setPaintingArt((EntityPainting)e, enumArt);
					}
				}
				else { //Show art GUI
					PaintingSelectionMod.proxy.displayPaintingSelectionScreen(message);
				}
			});

			return null;
		}

		protected EnumArt getEnumArt(String artName) {
			for (EnumArt art : EnumArt.values())
				if (art.title.equals(artName))
					return art;
			return EnumArt.KEBAB;
		}

		protected void setPaintingArt(EntityPainting p, EnumArt art) {
			//			p.art = art;
			//force a boundingbox update by reading the data of the entity
			NBTTagCompound tag = new NBTTagCompound();
			p.writeEntityToNBT(tag);
			tag.setString("Motive", art.title); //change art here, so it won't look like the painting moved
			p.readEntityFromNBT(tag);
		}
	}


}
