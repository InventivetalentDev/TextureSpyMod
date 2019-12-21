package org.inventivetalent.texturespy;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;

public class Util {

	public static GameProfile getTextureInfo(EntityPlayer entityPlayer) {
		if (entityPlayer != null) {
			return entityPlayer.getGameProfile();
		}
		return null;
	}

	public static GameProfile getTextureInfo(EntityLivingBase entityLiving) {
		if (entityLiving != null) {
			ItemStack headItem = entityLiving.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			return getTextureInfo(headItem);
		}
		return null;
	}

	public static GameProfile getTextureInfo(ItemStack itemStack) {
		if (itemStack != null) {
			Item item = itemStack.getItem();
			if (item == Items.SKULL && item.getMetadata(itemStack) == 3) {
				if (itemStack.getTagCompound() != null) {
					NBTTagCompound skullOwner = itemStack.getTagCompound().getCompoundTag("SkullOwner");
					return NBTUtil.readGameProfileFromNBT(skullOwner);
				}
			}
		}
		return null;
	}

	public static GameProfile getTextureInfo(TileEntitySkull entitySkull) {
		if (entitySkull.getSkullType() == 3) {
			return entitySkull.getPlayerProfile();
		}
		return null;
	}

	public static String stripMiddle(String string, int maxLength) {
		if (string == null) { return null; }
		if (string.length() < maxLength) { return string; }
		int half = Math.min(string.length(), maxLength) / 2;
		double ratio = 0.2;
		int fill = (int) Math.floor(half * ratio);
		int part = (int) Math.floor(half * (1 - ratio));
		String filler = "";
		for (int i = 0; i < fill; i++) {
			filler += ".";
		}
		return string.substring(0, part) + filler + string.substring(string.length() - part, string.length());
	}

}
