package com.mcf.davidee.paintinggui.gui;

import java.util.Comparator;

import net.minecraft.entity.item.EntityPainting.EnumArt;

public class ArtComparator implements Comparator<EnumArt> {
	
	@Override
	public int compare(EnumArt a, EnumArt b) {
		if (a.sizeY > b.sizeY)
			return -1;
		if (a.sizeY < b.sizeY)
			return 1;
		return b.sizeX - a.sizeX;
	}
}

