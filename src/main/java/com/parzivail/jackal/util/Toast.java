package com.parzivail.jackal.util;

import com.parzivail.jackal.util.gltk.EnableCap;
import com.parzivail.jackal.util.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class Toast
{
	private static Enumerable<Toast> toasts = Enumerable.empty();

	public static void tick()
	{
		long time = System.currentTimeMillis();
		toasts.removeAll(toasts.where(t -> t.endTime <= time));
	}

	public static void render()
	{
		Minecraft m = Minecraft.getMinecraft();

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

		GL.PushMatrix();

		GL.Enable(EnableCap.Blend);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL.Disable(EnableCap.Lighting);
		GL.Disable(EnableCap.DepthTest);

		FontRenderer fr = m.fontRenderer;
		GL.Enable(EnableCap.Texture2D);

		GL11.glColor4f(1, 1, 1, 1);
		GL.Translate(3, 3, 0);
		GL.Scale(1 / 2f);

		for (Toast t : toasts)
		{
			GL.PushMatrix();
			if (!t.itemStack.isEmpty())
			{
				m.getRenderItem().renderItemAndEffectIntoGUI(t.itemStack, 0, 0);
				GlStateManager.enableAlpha();
			}
			fr.drawString(t.content, t.itemStack.isEmpty() ? 0 : 18, 8 - fr.FONT_HEIGHT / 2, 0xFFFFFF, false);
			GL.PopMatrix();
			GL.Translate(0, fr.FONT_HEIGHT * 1.5f, 0);
		}

		GL.PopMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopAttrib();
	}

	private final String content;
	private final ItemStack itemStack;
	private final long endTime;

	public Toast(String content, int time)
	{
		this(content, null, time);
	}

	public Toast(String content, ItemStack itemStack, int time)
	{
		this.content = content;
		this.itemStack = itemStack;
		this.endTime = System.currentTimeMillis() + time;
	}

	public void show()
	{
		toasts.add(this);
	}
}
