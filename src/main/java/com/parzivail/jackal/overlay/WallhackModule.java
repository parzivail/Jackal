package com.parzivail.jackal.overlay;

import com.parzivail.jackal.proxy.Client;
import com.parzivail.jackal.util.EntityUtil;
import com.parzivail.jackal.util.Fx;
import com.parzivail.jackal.util.Toast;
import com.parzivail.jackal.util.gltk.EnableCap;
import com.parzivail.jackal.util.gltk.GL;
import com.parzivail.jackal.util.overlay.IJackalModule;
import com.parzivail.jackal.util.overlay.RenderScope;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * Created by Colby on 3/9/2018.
 */
public class WallhackModule implements IJackalModule
{
	@SideOnly(Side.CLIENT)
	public static KeyBinding key;

	private static boolean enabled;

	private static WallhackMode[] modes = new WallhackMode[] {
			WallhackMode.Default,
			WallhackMode.Players,
			WallhackMode.Other
	};
	private static int modeIndex = 0;

	private static WallhackMotionMode[] motionModes = new WallhackMotionMode[] {
			WallhackMotionMode.Default,
			WallhackMotionMode.HighlightMoving,
			WallhackMotionMode.OnlyMoving
	};
	private static int motionModeIndex = 0;

	public WallhackModule()
	{
		key = Client.registerKeybind("wallHack", Keyboard.KEY_X);
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
		return "Wallhack";
	}

	@Override
	public Item getIcon()
	{
		return Items.ENDER_EYE;
	}

	@Override
	public void handleKeyInput()
	{
		if (Keyboard.isKeyDown(Keyboard.KEY_LMENU))
		{
			if (Keyboard.isKeyDown(Keyboard.KEY_RMENU))
			{
				motionModeIndex++;
				motionModeIndex %= motionModes.length;
				new Toast("Motion Mode: " + motionModes[motionModeIndex].modeName, getIcon().getDefaultInstance(), Toast.LENGTH_SHORT).show();
			}
			else
			{
				modeIndex++;
				modeIndex %= modes.length;
				new Toast("Mode: " + modes[modeIndex].modeName, getIcon().getDefaultInstance(), Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			enabled = !enabled;
			Client.showOverlayToggleToast(this);
		}
	}

	@Override
	public boolean shouldRender(RenderScope phase)
	{
		return phase == RenderScope.EachEntity && enabled;
	}

	@Override
	public void render(EntityLivingBase e, float partialTicks, float x, float y, float z)
	{
		if (modes[modeIndex] == WallhackMode.Players && !(e instanceof EntityPlayer))
			return;
		if (modes[modeIndex] == WallhackMode.Other && e instanceof EntityPlayer)
			return;

		Minecraft m = Minecraft.getMinecraft();

		boolean anyMovement = e.posX != e.prevPosX || e.posY != e.prevPosY || e.posZ != e.prevPosZ;

		if ((motionModes[motionModeIndex] == WallhackMotionMode.OnlyMoving && !anyMovement))
			return;

		/*
			Setup
		 */
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glPushAttrib(GL11.GL_LINE_BIT);

		GL.PushMatrix();
		GL11.glColor4f(1, 1, 1, 1);

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		m.entityRenderer.disableLightmap();

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
		EntityUtil.renderAABB(e.getEntityBoundingBox());

		boolean isMoving = motionModes[motionModeIndex] != WallhackMotionMode.OnlyMoving && ((motionModes[motionModeIndex] == WallhackMotionMode.HighlightMoving && anyMovement));

		GL11.glLineWidth(2);
		if (e instanceof EntityPlayer)
			GL11.glColor4f(0, 1, isMoving ? 1 : 0, 1);
		else
			GL11.glColor4f(isMoving ? 1 : 0, 0, 1, 1);
		EntityUtil.renderAABB(e.getEntityBoundingBox());

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

	public enum WallhackMode
	{
		Default("All"), Players("Players"), Other("Non-players");

		public final String modeName;

		WallhackMode(String name)
		{
			this.modeName = name;
		}
	}

	public enum WallhackMotionMode
	{
		Default("All"), HighlightMoving("Highlight moving"), OnlyMoving("Only moving");

		public final String modeName;

		WallhackMotionMode(String name)
		{
			this.modeName = name;
		}
	}
}
