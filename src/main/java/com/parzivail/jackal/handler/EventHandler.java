package com.parzivail.jackal.handler;

import com.parzivail.jackal.proxy.Client;
import com.parzivail.jackal.util.Toast;
import com.parzivail.jackal.util.overlay.RenderScope;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandler
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderWorldLastEvent event)
	{
		Client.renderOverlays(Minecraft.getMinecraft().player, event.getPartialTicks(), RenderScope.Once, 0, 0, 0);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(FOVUpdateEvent event)
	{
		if (Client.keyZoom.isKeyDown())
			event.setNewfov(0.1f);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(InputEvent.KeyInputEvent event)
	{
		Client.delegateKeyInputToOverlays();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(TickEvent.ClientTickEvent event)
	{
		Toast.tick();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderGameOverlayEvent event)
	{
		if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT)
			return;

		Client.renderOverlays(Minecraft.getMinecraft().player, event.getPartialTicks(), RenderScope.GUI, 0, 0, 0);
		Toast.render();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderPlayerEvent.Post event)
	{
		if (event.getEntity() != null)
			Client.renderOverlays(event.getEntityLiving(), event.getPartialRenderTick(), RenderScope.EachEntity, (float)event.getX(), (float)event.getY(), (float)event.getZ());
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderLivingEvent.Post event)
	{
		if (event.getEntity() != null)
			Client.renderOverlays(event.getEntity(), event.getPartialRenderTick(), RenderScope.EachEntity, (float)event.getX(), (float)event.getY(), (float)event.getZ());
	}
}