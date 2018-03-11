package com.parzivail.jackal.overlay;

import com.parzivail.jackal.proxy.Client;
import com.parzivail.jackal.util.EntityUtil;
import com.parzivail.jackal.util.Fx;
import com.parzivail.jackal.util.gltk.EnableCap;
import com.parzivail.jackal.util.gltk.GL;
import com.parzivail.jackal.util.overlay.IJackalModule;
import com.parzivail.jackal.util.overlay.RenderScope;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerRadarModule implements IJackalModule
{
	private static HashMap<UUID, RadarPersistMode> specialCases = new HashMap<>();

	@SideOnly(Side.CLIENT)
	private static KeyBinding key;

	private static boolean enabled;

	public PlayerRadarModule()
	{
		key = Client.registerKeybind("playerRadar", Keyboard.KEY_J);
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
		return "Player Radar";
	}

	@Override
	public Item getIcon()
	{
		return Items.COMPASS;
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
		return phase == RenderScope.GUI && enabled;
	}

	@Override
	public void render(EntityLivingBase entity, float partialTicks, float x, float y, float z)
	{
		Minecraft m = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(m);

		double w = sr.getScaledWidth_double() - 24;
		double h = sr.getScaledHeight_double() - 6;

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

		GL.PushMatrix();

		GL.Disable(EnableCap.Lighting);
		GL.Enable(EnableCap.DepthTest);
		GL.Enable(EnableCap.Texture2D);
		GL.Enable(EnableCap.Blend);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL.Scale(1 / 2f);

		List<EntityPlayer> players = entity.world.getPlayers(EntityPlayer.class, EntitySelectors.NOT_SPECTATING);
		for (EntityPlayer p : players)
		{
			RadarPersistMode playerMode = getRadarPersistMode(p);
			if (p.getEntityId() == m.player.getEntityId() || playerMode == RadarPersistMode.Never)
				continue;

			// Check distance
			double d = p.getDistance(m.player);
			if (d > 50 && playerMode != RadarPersistMode.Always)
				continue;

			// Check in FOV
			double fov = m.gameSettings.fovSetting * m.player.getFovModifier() * 1.5f;
			double halfFov = fov / 2;
			Vec3d pos = m.player.getPositionEyes(partialTicks);
			double angleToPlayer = Math.toDegrees(MathHelper.atan2(p.posZ - pos.z, p.posX - pos.x)) - (MathHelper.wrapDegrees(m.getRenderManager().playerViewY) + 90);

			if (angleToPlayer < -180)
				angleToPlayer += 360;
			if (angleToPlayer < -fov || angleToPlayer > fov)
				continue;

			FontRenderer fr = m.fontRenderer;
			double lerpAlongEdge;

			String s = String.format("%s (%sm %s°)", p.getDisplayNameString(), Math.round(d), Math.abs(Math.round(angleToPlayer)));
			int stringW = fr.getStringWidth(s);

			GL.PushMatrix();
			if (angleToPlayer < -halfFov)
			{
				lerpAlongEdge = -(fov + angleToPlayer) / halfFov + 1;
				GL.Translate(12, lerpAlongEdge * h + 6, 0);
			}
			else if (angleToPlayer >= -halfFov && angleToPlayer <= halfFov)
			{
				lerpAlongEdge = angleToPlayer / fov + 0.5f;
				lerpAlongEdge = lerpAlongEdge * w * 2 + 24 - stringW / 2f;
				double maxSize = w * 2 - stringW / 2f - 2.5f;
				if (lerpAlongEdge > maxSize)
					lerpAlongEdge = maxSize;
				if (lerpAlongEdge < 12)
					lerpAlongEdge = 12;
				GL.Translate(lerpAlongEdge, 6, 0);
			}
			else
			{
				lerpAlongEdge = (angleToPlayer - halfFov) / halfFov;
				GL.Translate(w * 2 + 36 - stringW, lerpAlongEdge * h + 6, 0);
			}

			GL.Enable(EnableCap.Texture2D);
			fr.drawString(s, 0, 0, 0xFFFFFF);
			GlStateManager.disableAlpha();
			GL.Disable(EnableCap.Texture2D);

			GL.PushMatrix();
			GL.Translate(stringW / 2f, fr.FONT_HEIGHT * 1.5f, 0);

			GL.Rotate(angleToPlayer, 0, 0, 1);

			GL11.glLineWidth(2);
			drawArrow();
			GL.PopMatrix();

			GL.PopMatrix();
		}

		GL.PopMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopAttrib();
	}

	private void drawArrow()
	{
		Fx.D2.DrawLine(0, -5f, 0, 5f);
		Fx.D2.DrawLine(0, -5f, -3f, -1f);
		Fx.D2.DrawLine(0, -5f, 3f, -1f);
	}

	private RadarPersistMode getRadarPersistMode(EntityPlayer p)
	{
		UUID uuid = EntityUtil.getPlayerUUID(p);
		if (uuid == null || !specialCases.containsKey(uuid))
			return RadarPersistMode.Normal;
		return specialCases.get(uuid);
	}

	public static boolean blacklist(String s)
	{
		UUID uuid = EntityUtil.getPlayerUUID(s);
		if (uuid == null)
			return false;
		if (specialCases.containsKey(uuid))
			specialCases.remove(uuid);
		specialCases.put(uuid, RadarPersistMode.Never);
		return true;
	}

	public static boolean persist(String s)
	{
		UUID uuid = EntityUtil.getPlayerUUID(s);
		if (uuid == null)
			return false;
		if (specialCases.containsKey(uuid))
			specialCases.remove(uuid);
		specialCases.put(uuid, RadarPersistMode.Always);
		return true;
	}

	public static boolean forget(String s)
	{
		UUID uuid = EntityUtil.getPlayerUUID(s);
		if (uuid == null)
			return false;
		if (specialCases.containsKey(uuid))
			specialCases.remove(uuid);
		return true;
	}
}
