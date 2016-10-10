///*
//    Based on
//    http://www.minecraftforge.net/wiki/Netty_Packet_Handling
//*/
//package com.mcf.davidee.paintinggui.forge;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.entity.player.EntityPlayerMP;
//
//
//public interface IPacket {
//    /**
//     * Encode the packet data into the ByteBuf stream. Complex data sets may need specific data handlers (See @link{cpw.mods.fml.common.network.ByteBuffUtils})
//     *
//     * @param ctx    channel context
//     * @param buffer the buffer to encode into
//     */
//    public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer);
//
//    /**
//     * Decode the packet data from the ByteBuf stream. Complex data sets may need specific data handlers (See @link{cpw.mods.fml.common.network.ByteBuffUtils})
//     *
//     * @param ctx    channel context
//     * @param buffer the buffer to decode from
//     */
//    public abstract void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer);
//
//    /**
//     * Handle a packet on the client side. Note this occurs after decoding has completed.
//     *
//     * @param player the player reference
//     */
//    public abstract void handleClientSide(EntityPlayer player);
//
//    /**
//     * Handle a packet on the server side. Note this occurs after decoding has completed.
//     *
//     * @param player the player reference
//     */
//    public abstract void handleServerSide(EntityPlayerMP player);
//}