package com.parzivail.jackal.util;

import com.parzivail.jackal.util.gltk.EnableCap;
import com.parzivail.jackal.util.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
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
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GL.Disable(EnableCap.Lighting);
		GL.Disable(EnableCap.DepthTest);

		FontRenderer fr = m.fontRenderer;
		GL.Enable(EnableCap.Texture2D);

		GL11.glColor4f(1, 1, 1, 1);
		GL.Translate(3, 3, 0);
		GL.Scale(1 / 2f);

		boolean hadItem = false;
		for (int i = 0; i < toasts.size(); i++)
		{
			Toast t = toasts.get(i);
			boolean hasItem = !t.itemStack.isEmpty();
			GL.Translate(0, i == 0 ? 0 : (!hasItem && !hadItem ? fr.FONT_HEIGHT * 1.2f : 17), 0);
			GL.PushMatrix();
			fr.drawString(t.content, !hasItem ? 0 : 18, 8 - fr.FONT_HEIGHT / 2, 0xFFFFFF, false);
			if (hasItem)
			{
				RenderHelper.enableGUIStandardItemLighting();
				m.getRenderItem().renderItemAndEffectIntoGUI(t.itemStack, 0, 0);
				RenderHelper.disableStandardItemLighting();
			}
			GL.PopMatrix();

			hadItem = hasItem;
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
