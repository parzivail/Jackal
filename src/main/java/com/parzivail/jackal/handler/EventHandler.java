package com.parzivail.jackal.handler;

import com.parzivail.jackal.proxy.Client;
import com.parzivail.jackal.util.Enumerable;
import com.parzivail.jackal.util.Fx;
import com.parzivail.jackal.util.gltk.EnableCap;
import com.parzivail.jackal.util.gltk.GL;
import com.parzivail.jackal.util.gltk.PrimitiveType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class EventHandler
{
	private void renderWallhack(EntityLivingBase e, double x, double y, double z)
	{
		Minecraft m = Minecraft.getMinecraft();

		/*
			Setup
		 */
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

		/*
			AABB render
		 */
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

		double distToEntity = e.getDistanceSq(m.player);

		if (distToEntity < 200)
		{
		/*
			2D info render
		 */
			GL.Translate(e.lastTickPosX, e.lastTickPosY, e.lastTickPosZ);
			GL11.glRotatef(-m.getRenderManager().playerViewY, 0, 1, 0);

			GL11.glLineWidth(2);

			GL11.glColor4f(1, 0, 0, 1);
			Fx.D2.DrawSolidRectangle(-e.width / 2 - 0.1f, 0, 0.1f, e.height * e.getHealth() / e.getMaxHealth());
			GL11.glColor4f(0, 0, 0, 1);
			Fx.D2.DrawWireRectangle(-e.width / 2 - 0.1f, 0, 0.1f, e.height);

		/*
			2D info text render
		 */
			FontRenderer fr = m.fontRenderer;
			GL.Enable(EnableCap.Texture2D);

			GL11.glColor4f(1, 1, 1, 1);
			GL.Translate(-e.width / 2 - 0.2f, e.height, 0);
			GL.Scale(-1 / 64f);
			fr.drawString((Math.round(e.getHealth() * 10) / 10) + "/" + (Math.round(e.getMaxHealth() * 10) / 10), 0, 0, 0xFFFFFF);
			fr.drawString(e.getTotalArmorValue() + " armor", 0, fr.FONT_HEIGHT, 0xFFFFFF);
		}

		GL.PopMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopAttrib();
		GL11.glPopAttrib();
		m.entityRenderer.enableLightmap();
	}

	private void renderBowAim(float p)
	{
		Minecraft m = Minecraft.getMinecraft();

		if (!(m.player.getHeldItemMainhand().getItem() instanceof ItemBow))
			return;

		float pull = m.player.getItemInUseMaxCount();
		float velocity = ItemBow.getArrowVelocity((int)pull);
		velocity *= 3;

		Vec3d look = m.player.getLook(p);

		Enumerable<Vector3f> positions = Enumerable.empty();
		Enumerable<RayTraceResult.Type> hits = Enumerable.empty();

		float f = -MathHelper.sin(m.player.rotationYaw * 0.017453292F) * MathHelper.cos(m.player.rotationPitch * 0.017453292F);
		float f1 = -MathHelper.sin(m.player.rotationPitch * 0.017453292F);
		float f2 = MathHelper.cos(m.player.rotationYaw * 0.017453292F) * MathHelper.cos(m.player.rotationPitch * 0.017453292F);

		for (int x = -1; x <= 1; x++)
			for (int y = -1; y <= 1; y++)
				for (int z = -1; z <= 1; z++)
				{
					Tuple<Vector3f, RayTraceResult> postuple = getArrowLandPosition(m, velocity, f, f1, f2, x, y, z);
					RayTraceResult hit = postuple.getSecond();
					if (hit != null)
					{
						hits.add(hit.typeOfHit);
						positions.add(postuple.getFirst());
					}
				}

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glPushAttrib(GL11.GL_LINE_BIT);
		GL11.glPushAttrib(GL11.GL_POINT_BIT);

		GL.PushMatrix();
		GL.Disable(EnableCap.Lighting);
		GL.Disable(EnableCap.Blend);
		GL.Disable(EnableCap.DepthTest);
		GL.Disable(EnableCap.Texture2D);

		GL11.glPointSize(5);
		GL11.glColor4f(0, 0, 0, 1);

		GL.Begin(PrimitiveType.Points);
		for (Vector3f hitPt : positions)
			GL.Vertex3(hitPt);
		GL.End();

		GL11.glPointSize(3);
		if (hits.all(h -> h == RayTraceResult.Type.ENTITY))
			GL11.glColor4f(1, 0, 0, 1);
		else if (hits.any(h -> h == RayTraceResult.Type.ENTITY))
			GL11.glColor4f(1, 1, 0, 1);
		else
			GL11.glColor4f(1, 1, 1, 1);

		GL.Begin(PrimitiveType.Points);
		for (Vector3f hitPt : positions)
			GL.Vertex3(hitPt);
		GL.End();

		GL.PopMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopAttrib();
		GL11.glPopAttrib();
		GL11.glPopAttrib();
	}

	private Tuple<Vector3f, RayTraceResult> getArrowLandPosition(Minecraft m, float velocity, double x, double y, double z, float dx, float dy, float dz)
	{
		x += dx * 0.007499999832361937D;
		y += dy * 0.007499999832361937D;
		z += dz * 0.007499999832361937D;

		x *= velocity;
		y *= velocity;
		z *= velocity;

		double motionX = x;
		double motionY = y;
		double motionZ = z;

		motionX += m.player.motionX;
		motionZ += m.player.motionZ;

		if (!m.player.onGround)
		{
			motionY += m.player.motionY;
		}

		boolean found = false;
		Vector3f pos = new Vector3f(0, (float)(m.player.getEyeHeight() - 0.10000000149011612D), 0);
		Vector3f.add(pos, new Vector3f((float)motionX, (float)motionY, (float)motionZ), pos);
		RayTraceResult fhit = null;

		for (int i = 0; i < 150; i++)
		{
			Vec3d vec3d1 = new Vec3d(m.player.posX + pos.x, m.player.posY + pos.y, m.player.posZ + pos.z);
			Vec3d vec3d = new Vec3d(m.player.posX + pos.x + motionX, m.player.posY + pos.y + motionY, m.player.posZ + pos.z + motionZ);

			Tuple<Entity, RayTraceResult> hit = findEntityOnPath(m.player, m.player.world, vec3d1, vec3d);
			if (hit != null)
			{
				fhit = hit.getSecond();
				fhit.typeOfHit = RayTraceResult.Type.ENTITY;
				pos = makeRelativeHitPos(m, fhit);
				found = true;
				break;
			}

			RayTraceResult raytraceresult = m.player.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
			if (raytraceresult != null && (raytraceresult.entityHit == null || raytraceresult.entityHit.getEntityId() != m.player.getEntityId()))
			{
				fhit = raytraceresult;
				fhit.typeOfHit = RayTraceResult.Type.BLOCK;
				pos = makeRelativeHitPos(m, fhit);
				found = true;
				break;
			}

			motionX *= 0.99;
			motionY *= 0.99;
			motionZ *= 0.99;
			motionY -= 0.05000000074505806D;
			Vector3f.add(pos, new Vector3f((float)motionX, (float)motionY, (float)motionZ), pos);
		}

		return new Tuple<>(pos, found ? fhit : null);
	}

	private Vector3f makeRelativeHitPos(Minecraft m, RayTraceResult fhit)
	{
		return new Vector3f((float)(fhit.hitVec.x - m.player.posX), (float)(fhit.hitVec.y - m.player.posY), (float)(fhit.hitVec.z - m.player.posZ));
	}

	protected Tuple<Entity, RayTraceResult> findEntityOnPath(Entity exclude, World world, Vec3d start, Vec3d end)
	{
		Entity entity = null;
		RayTraceResult hit = null;
		List<Entity> list = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(start.x, start.y, start.z, end.x, end.y, end.z));
		double d0 = 0D;

		for (Entity entity1 : list)
		{
			if (entity1.getEntityId() != exclude.getEntityId())
			{
				AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox();
				RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);

				if (raytraceresult != null)
				{
					double d1 = start.squareDistanceTo(raytraceresult.hitVec);

					if (d1 < d0 || d0 == 0.0D)
					{
						hit = raytraceresult;
						entity = entity1;
						d0 = d1;
					}
				}
			}
		}

		if (entity == null)
			return null;

		return new Tuple<>(entity, hit);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderWorldLastEvent event)
	{
		boolean isWallhack = Client.keyWallhack.isKeyDown();
		if (isWallhack)
			renderBowAim(event.getPartialTicks());
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderPlayerEvent.Post event)
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

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderLivingEvent.Post event)
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