package com.mcf.davidee.paintinggui.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mcf.davidee.paintinggui.mod.PaintingSelection;
import com.mcf.davidee.paintinggui.packet.CPacketPainting;
import com.mcf.davidee.paintinggui.packet.NetworkHandler;

import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityPainting.EnumArt;
import net.minecraft.entity.player.EntityPlayer;
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
		if(!stack.isEmpty())
			if(stack.getItem().equals(Items.PAINTING)){

				EntityPlayer player = event.getEntityPlayer();
				EnumFacing face = event.getFace();
				BlockPos blockpos = event.getPos().offset(face);
				boolean flag = false;

				for(EnumFacing facing : EnumFacing.HORIZONTALS)
					if(face.equals(facing))
						flag = true;

				if (flag && player.canPlayerEdit(blockpos, face, stack)){

					EntityPainting painting =  new EntityPainting(event.getWorld(), blockpos, face);

					if(painting.onValidSurface()){
						event.getEntityPlayer().swingArm(EnumHand.MAIN_HAND); //recreate the animation of placing down an item

						if(!event.getEntityPlayer().isCreative())
							stack.shrink(1);

						if (!event.getWorld().isRemote){
							painting.playPlaceSound();
							event.getWorld().spawnEntity(painting);

							EnumArt originalArt = painting.art;
							List<EnumArt> validArts = new ArrayList<EnumArt>(); //list of paintings placeable at current location
							for(EnumArt art : EnumArt.values()){
								painting.art = art;

								//update the bounding box of the painting to make sure the simulation of placing down a painting
								//happens correctly. Omiting this will result in overlapping paintings
								updatePaintingBoundingBox(painting);

								//simulate placing down a painting. if possible, add it to a list of paintings
								//that are possible to place at this location
								if (painting.onValidSurface())
									validArts.add(art);
							}
							//reset the art of the painting to the one registered before
							painting.art = originalArt;
							updatePaintingBoundingBox(painting); // reset bounding box

							EnumArt[] validArtsArray = validArts.toArray(new EnumArt[0]);
							//sort paintings from high to low, and from big to small
							Arrays.sort(validArtsArray, PaintingSelection.ART_COMPARATOR);
							
							String[] names = new String[validArtsArray.length];
							for (int i =0; i < validArtsArray.length; ++i)
								names[i] = validArtsArray[i].title;

							EntityPlayerMP playerMP = (EntityPlayerMP)event.getEntityPlayer();
							NetworkHandler.NETWORK.sendTo(new CPacketPainting(painting.getEntityId(), names), playerMP);
						}
					}
					event.setCanceled(true);
				}
			}
	}

	//probably copied this from vanilla at some point ...
	private void updatePaintingBoundingBox(EntityPainting painting) {
		if (painting.facingDirection != null) {
			double hangX = (double) painting.getHangingPosition().getX() + 0.5D;
			double hangY = (double) painting.getHangingPosition().getY() + 0.5D;
			double hangZ = (double) painting.getHangingPosition().getZ() + 0.5D;
			double offsetWidth = painting.getWidthPixels() % 32 == 0 ? 0.5D : 0.0D;
			double offsetHeight = painting.getHeightPixels() % 32 == 0 ? 0.5D : 0.0D;
			hangX = hangX - (double) painting.facingDirection.getFrontOffsetX() * 0.46875D;
			hangZ = hangZ - (double) painting.facingDirection.getFrontOffsetZ() * 0.46875D;
			hangY = hangY + offsetHeight;
			EnumFacing enumfacing = painting.facingDirection.rotateYCCW();
			hangX = hangX + offsetWidth * (double) enumfacing.getFrontOffsetX();
			hangZ = hangZ + offsetWidth * (double) enumfacing.getFrontOffsetZ();
			painting.posX = hangX;
			painting.posY = hangY;
			painting.posZ = hangZ;
			double widthX = (double) painting.getWidthPixels();
			double height = (double) painting.getHeightPixels();
			double widthZ = (double) painting.getWidthPixels();

			if (painting.facingDirection.getAxis() == EnumFacing.Axis.Z) {
				widthZ = 1.0D;
			} else {
				widthX = 1.0D;
			}

			widthX = widthX / 32.0D;
			height = height / 32.0D;
			widthZ = widthZ / 32.0D;
			painting.setEntityBoundingBox(new AxisAlignedBB(hangX - widthX, hangY - height, hangZ - widthZ, hangX + widthX, hangY + height, hangZ + widthZ));
		}
	}
}
