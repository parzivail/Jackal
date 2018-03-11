package com.parzivail.jackal.overlay;

import com.parzivail.jackal.proxy.Client;
import com.parzivail.jackal.util.EntityUtil;
import com.parzivail.jackal.util.Enumerable;
import com.parzivail.jackal.util.gltk.EnableCap;
import com.parzivail.jackal.util.gltk.GL;
import com.parzivail.jackal.util.gltk.PrimitiveType;
import com.parzivail.jackal.util.overlay.IJackalModule;
import com.parzivail.jackal.util.overlay.RenderScope;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Colby on 3/9/2018.
 */
public class ArrowGuideModule implements IJackalModule
{
	@SideOnly(Side.CLIENT)
	private static KeyBinding key;

	private static boolean enabled;

	public ArrowGuideModule()
	{
		key = Client.registerKeybind("arrowGuide", Keyboard.KEY_G);
	}

	@Override
	public KeyBinding getKeyBinding()
	{
		return key;
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public String getName()
	{
		return "Arrow Guide";
	}

	@Override
	public Item getIcon()
	{
		return Items.ARROW;
	}

	@Override
	public void handleKeyInput()
	{
		enabled = !enabled;
		Client.showOverlayToggleToast(this);
	}

	@Override
	public boolean shouldRender(RenderScope phase)
	{
		return phase == RenderScope.Once && enabled;
	}

	@Override
	public void render(EntityLivingBase entity, float partialTicks, float rx, float ry, float rz)
	{
		Minecraft m = Minecraft.getMinecraft();

		if (!(m.player.getHeldItemMainhand().getItem() instanceof ItemBow))
			return;

		float pull = m.player.getItemInUseMaxCount();

		if (pull == 0)
			return;

		float velocity = ItemBow.getArrowVelocity((int)pull);
		velocity *= 3;

		Enumerable<Tuple<Vector3f, RayTraceResult>> hits = Enumerable.empty();

		float f = -MathHelper.sin(m.player.rotationYaw * 0.017453292F) * MathHelper.cos(m.player.rotationPitch * 0.017453292F);
		float f1 = -MathHelper.sin(m.player.rotationPitch * 0.017453292F);
		float f2 = MathHelper.cos(m.player.rotationYaw * 0.017453292F) * MathHelper.cos(m.player.rotationPitch * 0.017453292F);

		for (int x = -1; x <= 1; x++)
			for (int y = -1; y <= 1; y++)
				for (int z = -1; z <= 1; z++)
				{
					Tuple<Vector3f, RayTraceResult> postuple = getArrowLandPosition(velocity, f, f1, f2, x, y, z);
					if (postuple != null)
						hits.add(postuple);
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
		for (Tuple<Vector3f, RayTraceResult> hitPt : hits)
			GL.Vertex3(hitPt.getFirst());
		GL.End();

		GL11.glPointSize(3);

		boolean all = hits.all(h -> h.getSecond().typeOfHit == RayTraceResult.Type.ENTITY);
		if (all)
			GL11.glColor4f(1, 0, 0, 1);

		GL.Begin(PrimitiveType.Points);
		for (Tuple<Vector3f, RayTraceResult> hitPt : hits)
		{
			if (!all && hitPt.getSecond().typeOfHit == RayTraceResult.Type.ENTITY)
				GL11.glColor4f(1, 1, 0, 1);
			else if (!all)
				GL11.glColor4f(1, 1, 1, 1);
			GL.Vertex3(hitPt.getFirst());
		}
		GL.End();

		GL.PopMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopAttrib();
		GL11.glPopAttrib();
		GL11.glPopAttrib();
	}

	/**
	 * Simulates an arrow's trajectory for up to 150 (7.5s) ticks to determine it's landing position under the specified conditions
	 *
	 * @param velocity The velocity of the arrow
	 * @param x        The originating x position of the arrow
	 * @param y        The originating y position of the arrow
	 * @param z        The originating z position of the arrow
	 * @param dx       The x displacement of the arrow
	 * @param dy       The y displacement of the arrow
	 * @param dz       The z displacement of the arrow
	 * @return A <code>Tuple</code> of the quick position and the full raytrace result of the arrow
	 */
	private Tuple<Vector3f, RayTraceResult> getArrowLandPosition(float velocity, double x, double y, double z, float dx, float dy, float dz)
	{
		Minecraft m = Minecraft.getMinecraft();

		x += dx * 0.01;
		y += dy * 0.01;
		z += dz * 0.01;

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

			Tuple<Entity, RayTraceResult> hit = EntityUtil.findEntityOnPath(EntityLivingBase.class, m.player, m.player.world, vec3d1, vec3d);
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

		return found ? new Tuple<>(pos, fhit) : null;
	}

	private Vector3f makeRelativeHitPos(Minecraft m, RayTraceResult fhit)
	{
		return new Vector3f((float)(fhit.hitVec.x - m.player.posX), (float)(fhit.hitVec.y - m.player.posY), (float)(fhit.hitVec.z - m.player.posZ));
	}
}
