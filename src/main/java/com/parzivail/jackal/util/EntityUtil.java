package com.parzivail.jackal.util;

import com.parzivail.jackal.util.gltk.GL;
import com.parzivail.jackal.util.gltk.PrimitiveType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class EntityUtil
{
	public static Tuple<Entity, RayTraceResult> findEntityOnPath(Class<? extends Entity> clazz, Entity exclude, World world, Vec3d start, Vec3d end)
	{
		Entity entity = null;
		RayTraceResult hit = null;
		List<Entity> list = world.getEntitiesWithinAABB(clazz, new AxisAlignedBB(start.x, start.y, start.z, end.x, end.y, end.z));
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

	public static UUID getPlayerUUID(String s)
	{
		EntityPlayer foundPlayer = Minecraft.getMinecraft().player.world.getPlayerEntityByName(s);
		return getPlayerUUID(foundPlayer);
	}

	public static UUID getPlayerUUID(EntityPlayer p)
	{
		if (p == null)
			return null;
		return EntityPlayer.getUUID(p.getGameProfile());
	}

	public static Enumerable<String> getOnlinePlayerNames()
	{
		return Enumerable.from(Minecraft.getMinecraft().player.world.getPlayers(EntityPlayer.class, EntitySelectors.NOT_SPECTATING)).select(EntityPlayer::getName);
	}

	public static void renderAABB(AxisAlignedBB aabb)
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
