package com.parzivail.jackal.util;

import com.parzivail.jackal.util.gltk.EnableCap;
import com.parzivail.jackal.util.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class Toast
{
	private static Enumerable<Toast> toasts = Enumerable.empty();

	public static void tick()
	{
		long time = System.currentTimeMillis();
		toasts.removeAll(toasts.where(t -> t.endTime + 100 <= time));
	}

	public static void render()
	{
		Minecraft m = Minecraft.getMinecraft();

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

		GL.PushMatrix();

		GL.Disable(EnableCap.Lighting);
		GL.Enable(EnableCap.DepthTest);
		GL.Enable(EnableCap.Texture2D);
		GL.Enable(EnableCap.Blend);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		FontRenderer fr = m.fontRenderer;

		GL.Translate(3, 3, 0);
		GL.Scale(1 / 2f);

		long time = System.currentTimeMillis();

		boolean hadItem = false;
		float prevRemainingFraction = 0;
		for (int i = 0; i < toasts.size(); i++)
		{
			Toast t = toasts.get(i);
			long timeLeft = t.endTime - time;

			float remainingFraction = 1;
			int fadeOutTime = 500;
			if (timeLeft < 0)
				remainingFraction = 0;
			else if (timeLeft < fadeOutTime)
				remainingFraction = (float)timeLeft / fadeOutTime;

			GL11.glColor4f(1, 1, 1, 1);
			boolean hasItem = !t.itemStack.isEmpty();
			GL.Translate(0, i == 0 ? 0 : (!hasItem && !hadItem ? fr.FONT_HEIGHT * 1.2f : 17) * prevRemainingFraction, 0);

			GL.PushMatrix();
			GL.Scale(remainingFraction);

			GL.PushMatrix();
			fr.drawString(t.content, !hasItem ? 0 : 18, 8 - fr.FONT_HEIGHT / 2, 0xFFFFFF, false);
			if (hasItem)
			{
				RenderHelper.enableGUIStandardItemLighting();
				m.getRenderItem().renderItemAndEffectIntoGUI(t.itemStack, 0, 0);
				RenderHelper.disableStandardItemLighting();
			}

			GL.PopMatrix();
			GL.PopMatrix();

			hadItem = hasItem;
			prevRemainingFraction = remainingFraction;
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
		this.itemStack = itemStack.copy();
		this.endTime = System.currentTimeMillis() + time;
	}

	public void show()
	{
		toasts.add(this);
	}
}
