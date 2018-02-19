package com.parzivail.jackal.handler;

import com.parzivail.jackal.util.ShaderHelper;
import com.parzivail.jackal.util.gltk.EnableCap;
import com.parzivail.jackal.util.gltk.GL;
import com.parzivail.jackal.util.gltk.PrimitiveType;
import com.parzivail.jackal.proxy.Client;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderLivingEvent;
import org.lwjgl.opengl.GL11;

public class EventHandler
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderLivingEvent.Pre event)
	{
		boolean isWallhack = Client.keyWallhack.getIsKeyPressed();
		if (event.entity instanceof EntityLiving && isWallhack)
		{
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			GL11.glPushAttrib(GL11.GL_LINE_BIT);

			GL.PushMatrix();
			GL11.glColor4f(1, 1, 1, 1);

			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			GL11.glDisable(GL11.GL_LIGHTING);
			Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL.Disable(EnableCap.DepthTest);
			GL.Translate(event.x - event.entity.lastTickPosX, event.y - event.entity.lastTickPosY, event.z - event.entity.lastTickPosZ);

			GL11.glLineWidth(4);
			GL11.glColor3f(0, 0, 0);
			renderAABB(event.entity.boundingBox);

			GL11.glLineWidth(2);
			if (event.entity instanceof EntityPlayer)
				GL11.glColor3f(0, 0, 1);
			else
				GL11.glColor3f(1, 0, 0);
			renderAABB(event.entity.boundingBox);

			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

			GL.PopMatrix();
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glPopAttrib();
			GL11.glPopAttrib();

			ShaderHelper.useShader(ShaderHelper.entityGlow);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderLivingEvent.Post event)
	{
		boolean isWallhack = Client.keyWallhack.getIsKeyPressed();
		if (isWallhack)
		{
			ShaderHelper.releaseShader();
		}
	}

	private static void renderAABB(AxisAlignedBB aabb)
	{
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
