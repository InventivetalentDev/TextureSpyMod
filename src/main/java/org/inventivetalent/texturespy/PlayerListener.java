package org.inventivetalent.texturespy;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PlayerListener {

	private TextureSpyMod mod;

	Object lastTargetThing;

	public PlayerListener(TextureSpyMod mod) {
		this.mod = mod;
	}

	@SubscribeEvent
	public void on(TickEvent.ClientTickEvent event) {
		RayTraceResult rayTraceResult = Minecraft.getMinecraft().objectMouseOver;
		if (rayTraceResult == null) { return; }
		if (rayTraceResult.typeOfHit == RayTraceResult.Type.MISS) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.world == null) { return; }
		if (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos blockPos = rayTraceResult.getBlockPos();
			TileEntity tileEntity = mc.world.getTileEntity(blockPos);
			if (tileEntity instanceof TileEntitySkull) {
				GameProfile textureInfo = Util.getTextureInfo((TileEntitySkull) tileEntity);
				showTextureInfo(mc, TextureLocation.BLOCK, textureInfo, null, (TileEntitySkull) tileEntity);
			}
		} else if (rayTraceResult.typeOfHit == RayTraceResult.Type.ENTITY) {
			if (rayTraceResult.entityHit instanceof EntityPlayer) {
				GameProfile textureInfo = Util.getTextureInfo((EntityPlayer) rayTraceResult.entityHit);
				showTextureInfo(mc, TextureLocation.SKIN, textureInfo, (EntityPlayer) rayTraceResult.entityHit, null);
			}
			if (rayTraceResult.entityHit instanceof EntityLivingBase) {
				GameProfile textureInfo = Util.getTextureInfo((EntityLivingBase) rayTraceResult.entityHit);
				showTextureInfo(mc, TextureLocation.HEAD, textureInfo, (EntityLivingBase) rayTraceResult.entityHit, null);
			}
		}

	}

	public void showTextureInfo(Minecraft mc, TextureLocation location, GameProfile textureInfo, EntityLivingBase entity, TileEntitySkull tileEntity) {
		if (textureInfo == null) { return; }

		if (entity != null) {
			if (entity == lastTargetThing) {
				return;
			}
			lastTargetThing = entity;
		}
		if (tileEntity != null) {
			if (tileEntity == lastTargetThing) {
				return;
			}
			lastTargetThing = tileEntity;
		}

		System.out.println(" ");
		System.out.println(location);
		System.out.println("Name: " + textureInfo.getName());
		System.out.println("UUID: " + textureInfo.getId());

		String value = null;
		String signature = null;

		TextComponentString hoverText = new TextComponentString("");
		for (Map.Entry<String, Property> entry : textureInfo.getProperties().entries()) {
			System.out.println(entry.getKey() + ": {");
			System.out.println("  name:  " + entry.getValue().getName());
			System.out.println("  value: " + entry.getValue().getValue());
			System.out.println("  sig:   " + entry.getValue().getSignature());
			System.out.println("}");

			hoverText.appendText(entry.getKey() + ": {\n"
					+ TextFormatting.GRAY + "  name:  " + TextFormatting.WHITE + entry.getValue().getName() + "\n"
					+ TextFormatting.GRAY + "  value: " + TextFormatting.WHITE + Util.stripMiddle(entry.getValue().getValue(), 40) + "\n"
					+ TextFormatting.GRAY + "  sig:   " + TextFormatting.WHITE + Util.stripMiddle(entry.getValue().getSignature(), 40) + "\n"
					+ "}");

			value = entry.getValue().getValue();
			signature = entry.getValue().getSignature();
		}

		try {
			if (value != null) {
				value = URLEncoder.encode(value, StandardCharsets.UTF_8.toString()).replaceAll("/","%2F");
			}
			if (signature != null) {
				signature = URLEncoder.encode(signature, StandardCharsets.UTF_8.toString()).replaceAll("/","%2F");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		TextComponentString textComponent = new TextComponentString("[" + location.name() + "] " + TextFormatting.UNDERLINE + textureInfo.getName() + TextFormatting.RESET + " (" + textureInfo.getId() + ")");
		Style style = textComponent.getStyle();
		style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
		style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://api.mineskin.org/get/forTexture/" + value + (signature != null ? signature : "")));
		mc.ingameGUI.addChatMessage(ChatType.SYSTEM, textComponent);
	}

}
