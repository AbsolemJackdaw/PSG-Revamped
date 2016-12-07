package com.mcf.davidee.paintinggui.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.mcf.davidee.paintinggui.PaintingSelectionMod;
import com.mcf.davidee.paintinggui.packet.PacketPaintingClient;
import com.mcf.davidee.paintinggui.packet.PaintingPacketHandler;

import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityPainting.EnumArt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlacePaintingEventHandler {

	public PlacePaintingEventHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onPaintingPlaced(PlayerInteractEvent.RightClickBlock event){

		ItemStack stack = event.getItemStack();
		if(stack.getItem().equals(Items.PAINTING)){

			EnumFacing face = event.getFace();
			BlockPos blockpos = event.getPos().offset(face);

			if (face != EnumFacing.DOWN && face != EnumFacing.UP && 
					event.getEntityPlayer().canPlayerEdit(blockpos, face, stack)){

				EntityPainting painting =  new EntityPainting(event.getWorld(), blockpos, face);

				if(painting.onValidSurface()){
					event.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);

					if(!event.getEntityPlayer().isCreative())
						stack.shrink(1);//grow

					if (!event.getWorld().isRemote){
						painting.playPlaceSound();
						event.getWorld().spawnEntity(painting);

						EnumArt origArt = painting.art;
						List<EnumArt> validArts = new ArrayList<EnumArt>();
						for(EnumArt art : EnumArt.values()){
							painting.art = art;

							updatePaintingBoundingBox(painting);

							if (painting.onValidSurface())
								validArts.add(art);
						}
						painting.art = origArt;
						updatePaintingBoundingBox(painting); // reset bounding box

						EnumArt[] validArtsArray = validArts.toArray(new EnumArt[0]);
						Arrays.sort(validArtsArray, PaintingSelectionMod.ART_COMPARATOR);
						String[] names = new String[validArtsArray.length];
						for (int i =0; i < validArtsArray.length; ++i)
							names[i] = validArtsArray[i].title;

						EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
						PaintingPacketHandler.NETWORK.sendTo(new PacketPaintingClient(painting.getEntityId(), names), player);
					}else{
						//PaintingPacketHandler.NETWORK.sendToServer(new PacketPaintingServer(painting.getEntityId(), new String[0]));
					}
				}
				event.setCanceled(true);
			}
		}
	}

	private void updatePaintingBoundingBox(EntityPainting painting) {
		if (painting.facingDirection != null) {
			double d0 = (double) painting.getHangingPosition().getX() + 0.5D;
			double d1 = (double) painting.getHangingPosition().getY() + 0.5D;
			double d2 = (double) painting.getHangingPosition().getZ() + 0.5D;
			double d3 = 0.46875D;
			double d4 = painting.getWidthPixels() % 32 == 0 ? 0.5D : 0.0D;
			double d5 = painting.getHeightPixels() % 32 == 0 ? 0.5D : 0.0D;
			d0 = d0 - (double) painting.facingDirection.getFrontOffsetX() * 0.46875D;
			d2 = d2 - (double) painting.facingDirection.getFrontOffsetZ() * 0.46875D;
			d1 = d1 + d5;
			EnumFacing enumfacing = painting.facingDirection.rotateYCCW();
			d0 = d0 + d4 * (double) enumfacing.getFrontOffsetX();
			d2 = d2 + d4 * (double) enumfacing.getFrontOffsetZ();
			painting.posX = d0;
			painting.posY = d1;
			painting.posZ = d2;
			double d6 = (double) painting.getWidthPixels();
			double d7 = (double) painting.getHeightPixels();
			double d8 = (double) painting.getWidthPixels();

			if (painting.facingDirection.getAxis() == EnumFacing.Axis.Z) {
				d8 = 1.0D;
			} else {
				d6 = 1.0D;
			}

			d6 = d6 / 32.0D;
			d7 = d7 / 32.0D;
			d8 = d8 / 32.0D;
			painting.setEntityBoundingBox(new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
		}
	}
}
