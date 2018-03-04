package com.parzivail.jackal.handler;

import com.parzivail.jackal.proxy.Client;
import com.parzivail.jackal.util.ShaderHelper;
import com.parzivail.jackal.util.gltk.EnableCap;
import com.parzivail.jackal.util.gltk.GL;
import com.parzivail.jackal.util.gltk.PrimitiveType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class EventHandler
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderLivingEvent.Pre event)
	{
		boolean isWallhack = Client.keyWallhack.isKeyDown();
		if (event.getEntity() != null && isWallhack)
		{
			EntityLivingBase e = event.getEntity();
			double x = event.getX();
			double y = event.getY();
			double z = event.getZ();

			renderWallhack(e, x, y, z);
		}
	}
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderPlayerEvent.Pre event)
	{
		boolean isWallhack = Client.keyWallhack.isKeyDown();
		if (event.getEntity() != null && isWallhack)
		{
			EntityPlayer e = (EntityPlayer)event.getEntity();
			double x = event.getX();
			double y = event.getY();
			double z = event.getZ();

			renderWallhack(e, x, y, z);
		}
	}

	private void renderWallhack(EntityLivingBase e, double x, double y, double z)
	{
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glPushAttrib(GL11.GL_LINE_BIT);

		GL.PushMatrix();
		GL11.glColor4f(1, 1, 1, 1);

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
		GL.Disable(EnableCap.Lighting);
		GL.Disable(EnableCap.Blend);
		GL.Disable(EnableCap.Texture2D);

		GL.Disable(EnableCap.DepthTest);
		GL.Translate(x - e.lastTickPosX, y - e.lastTickPosY, z - e.lastTickPosZ);

		GL11.glLineWidth(4);
		GL11.glColor4f(0, 0, 0, 1);
		renderAABB(e.getEntityBoundingBox());

		GL11.glLineWidth(2);
		if (e instanceof EntityPlayer)
			GL11.glColor4f(0, 1, 0, 1);
		else
			GL11.glColor4f(0, 0, 1, 1);
		renderAABB(e.getEntityBoundingBox());

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

		GL.PopMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopAttrib();
		GL11.glPopAttrib();

		ShaderHelper.useShader(ShaderHelper.entityGlow);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderPlayerEvent.Post event)
	{
		ShaderHelper.useShader(0);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderLivingEvent.Post event)
	{
		ShaderHelper.useShader(0);
	}

	private static void renderAABB(AxisAlignedBB aabb)
	{
		if (aabb == null)
			return;

		GL.Begin(PrimitiveType.Quads);
		GL.Vertex3(aabb.minX, aabb.maxY, aabb.minZ);
		GL.Vertex3(aabb.maxX, aabb.maxY, aabb.minZ);
		GL.Vertex3(aabb.maxX, aabb.minY, aabb.minZ);
		GL.Vertex3(aabb.minX, aabb.minY, aabb.minZ);

		GL.Vertex3(aabb.minX, aabb.minY, aabb.maxZ);
		GL.Vertex3(aabb.maxX, aabb.minY, aabb.maxZ);
		GL.Vertex3(aabb.maxX, aabb.maxY, aabb.maxZ);
		GL.Vertex3(aabb.minX, aabb.maxY, aabb.maxZ);

		GL.Vertex3(aabb.minX, aabb.minY, aabb.minZ);
		GL.Vertex3(aabb.maxX, aabb.minY, aabb.minZ);
		GL.Vertex3(aabb.maxX, aabb.minY, aabb.maxZ);
		GL.Vertex3(aabb.minX, aabb.minY, aabb.maxZ);

		GL.Vertex3(aabb.minX, aabb.maxY, aabb.maxZ);
		GL.Vertex3(aabb.maxX, aabb.maxY, aabb.maxZ);
		GL.Vertex3(aabb.maxX, aabb.maxY, aabb.minZ);
		GL.Vertex3(aabb.minX, aabb.maxY, aabb.minZ);

		GL.Vertex3(aabb.minX, aabb.minY, aabb.maxZ);
		GL.Vertex3(aabb.minX, aabb.maxY, aabb.maxZ);
		GL.Vertex3(aabb.minX, aabb.maxY, aabb.minZ);
		GL.Vertex3(aabb.minX, aabb.minY, aabb.minZ);

		GL.Vertex3(aabb.maxX, aabb.minY, aabb.minZ);
		GL.Vertex3(aabb.maxX, aabb.maxY, aabb.minZ);
		GL.Vertex3(aabb.maxX, aabb.maxY, aabb.maxZ);
		GL.Vertex3(aabb.maxX, aabb.minY, aabb.maxZ);
		GL.End();
	}
}