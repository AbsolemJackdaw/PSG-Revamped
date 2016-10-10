package com.mcf.davidee.paintinggui.gui;

import org.lwjgl.opengl.GL11;

import com.mcf.davidee.guilib.core.Button;
import com.mcf.davidee.guilib.core.Scrollbar.Shiftable;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityPainting.EnumArt;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public class PaintingButton extends Button implements Shiftable {

	public static ResourceLocation TEXTURE = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");
	public static int KZ_WIDTH = 256, KZ_HEIGHT = 256;

	private static final int BORDER = 3;
	private static final int YELLOW = -256;

	public final EnumArt art;

	public PaintingButton(EnumArt art, ButtonHandler handler) {
		super(art.sizeX, art.sizeY, handler);

		this.art = art;
	}


	@Override
	public void draw(int mx, int my) {
		mc.renderEngine.bindTexture(TEXTURE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(x, y, art.offsetX, art.offsetY, width, height);

		if (inBounds(mx, my)) { //Hover over, draw the 4 outlining rectangles
			drawRect(x - BORDER, y - BORDER, x + width + BORDER, y, YELLOW); //upper left to upper right
			drawRect(x - BORDER, y + height, x + width + BORDER, y + height + BORDER, YELLOW); //lower left to lower right
			drawRect(x - BORDER, y, x, y + height, YELLOW); //middle rectangle to the left
			drawRect(x + width, y, x + width + BORDER, y + height, YELLOW); //middle rectangle to the right
		}
	}

	public void handleClick(int mx, int my) {
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		super.handleClick(mx, my);
	}

	public void drawTexturedModalRect(int x, int y, int xOffset, int yOffset, int width, int height) {
		double f = 1d /(double)KZ_WIDTH;
		double f1 =1d /(double)KZ_HEIGHT;

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(x, y + height, 0).tex((double)xOffset * f, (double)(yOffset+height) * f1).endVertex();
		vertexbuffer.pos(x + width, y + height, zLevel).tex((double)(xOffset + width) * f, (double)(yOffset + height) * f1).endVertex();
		vertexbuffer.pos(x + width, y, zLevel).tex((double)(xOffset + width) * f, (double)(yOffset + 0) * f1).endVertex();
		vertexbuffer.pos(x, y, zLevel).tex((double)(xOffset + 0) * f, (double)(yOffset + 0) * f1).endVertex();
		tessellator.draw();
	}

	@Override
	public void shiftY(int dy) {
		this.y += dy;
	}

	public void shiftX(int dx) {
		this.x += dx;
	}

}
