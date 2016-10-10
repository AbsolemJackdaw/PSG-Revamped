//package com.mcf.davidee.paintinggui.forge;
//
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.EnumMap;
//import java.util.LinkedList;
//import java.util.List;
//
//import com.mcf.davidee.paintinggui.PaintingSelectionMod;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.MessageToMessageCodec;
//import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.network.INetHandler;
//import net.minecraft.network.NetHandlerPlayServer;
//import net.minecraft.network.PacketBuffer;
//import net.minecraftforge.fml.common.FMLCommonHandler;
//import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
//import net.minecraftforge.fml.common.network.FMLOutboundHandler;
//import net.minecraftforge.fml.common.network.NetworkRegistry;
//import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
//import net.minecraftforge.fml.relauncher.Side;
//
///**
// * Packet pipeline class. Directs all registered packet data to be handled by the packets themselves.
// * @author sirgingalot
// * some code from: cpw
// */
//@ChannelHandler.Sharable
//public class PacketPipeline extends MessageToMessageCodec<FMLProxyPacket, IPacket> {
//
//    private EnumMap<Side, FMLEmbeddedChannel>	channels;
//    private LinkedList<Class<? extends IPacket>> packets = new LinkedList<Class<? extends IPacket>>();
//    private boolean	isPostInitialized = false;
//
//    // In line encoding of the packet, including discriminator setting
//    @Override
//    protected void encode(ChannelHandlerContext ctx, IPacket msg, List<Object> out) throws IllegalArgumentException {
//    	PacketBuffer buffer = (PacketBuffer) Unpooled.buffer();
//        Class<? extends IPacket> clazz = msg.getClass();
//        if (!packets.contains(msg.getClass())) 
//            throw new IllegalArgumentException("No packet registered for: " + msg.getClass().getCanonicalName());
//
//        byte discriminator = (byte) packets.indexOf(clazz);
//        buffer.writeByte(discriminator);
//        msg.encodeInto(ctx, buffer);
//        FMLProxyPacket proxyPacket = new FMLProxyPacket((PacketBuffer) buffer.copy(), ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
//        out.add(proxyPacket);
//    }
//
//    // In line decoding and handling of the packet
//    @Override
//    protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
//        ByteBuf payload = msg.payload();
//        byte discriminator = payload.readByte();
//        Class<? extends IPacket> clazz = packets.get(discriminator);
//        if (clazz == null)
//            throw new IllegalArgumentException("No packet registered for discriminator: " + discriminator);
//
//        IPacket pkt = clazz.newInstance();
//        pkt.decodeInto(ctx, payload.slice());
//
//        switch (FMLCommonHandler.instance().getEffectiveSide()) {
//            case CLIENT:
//                pkt.handleClientSide(PaintingSelectionMod.proxy.getClientPlayer());
//                break;
//            case SERVER:
//                INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
//                pkt.handleServerSide(((NetHandlerPlayServer) netHandler).playerEntity);
//                break;
//            default:
//            	break;
//        }
//        
//        out.add(pkt);
//    }
//
//    public void initalize() {
//        channels = NetworkRegistry.INSTANCE.newChannel(PaintingSelectionMod.CHANNEL, this);
//        packets.add(PaintingPacket.class);
//    }
//
//
//    public void postInitialize() {
//        if (isPostInitialized) 
//            return;
//        isPostInitialized = true;
//        
//        Collections.sort(packets, new Comparator<Class<? extends IPacket>>() {
//            public int compare(Class<? extends IPacket> clazz1, Class<? extends IPacket> clazz2) {
//                    return clazz1.getCanonicalName().compareTo(clazz2.getCanonicalName());
//            }
//        });
//    }
//
//    /**
//     * Adapted from CPW's code in cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
//     */
//    public void sendToAll(IPacket message) {
//        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
//        this.channels.get(Side.SERVER).writeAndFlush(message);
//    }
//
//    /**
//     * Adapted from CPW's code in cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
//     */
//    public void sendTo(IPacket message, EntityPlayerMP player) {
//        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
//        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
//        this.channels.get(Side.SERVER).writeAndFlush(message);
//    }
//
//    /**
//     * Adapted from CPW's code in cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
//     */
//    public void sendToAllAround(IPacket message, NetworkRegistry.TargetPoint point) {
//        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
//        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
//        this.channels.get(Side.SERVER).writeAndFlush(message);
//    }
//
//    /**
//     * Adapted from CPW's code in cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
//     */
//    public void sendToDimension(IPacket message, int dimensionId) {
//        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
//        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
//        this.channels.get(Side.SERVER).writeAndFlush(message);
//    }
//
//    /**
//     * Adapted from CPW's code in cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
//     */
//    public void sendToServer(IPacket message) {
//        this.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
//        this.channels.get(Side.CLIENT).writeAndFlush(message);
//    }
//}