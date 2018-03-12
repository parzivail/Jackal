package com.parzivail.jackal.render;

import com.parzivail.jackal.overlay.ChestEspModule;
import com.parzivail.jackal.proxy.Client;
import com.parzivail.jackal.util.EntityUtil;
import com.parzivail.jackal.util.gltk.EnableCap;
import com.parzivail.jackal.util.gltk.GL;
import com.parzivail.jackal.util.overlay.IJackalModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

public class RenderChestESP extends TileEntityChestRenderer
{
	public void render(TileEntityChest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		IJackalModule chestEspModule = Client.getModule(ChestEspModule.class);
		if (!chestEspModule.isEnabled())
			return;

		Minecraft m = Minecraft.getMinecraft();

		AxisAlignedBB aabb = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);

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

		GL11.glLineWidth(4);
		GL11.glColor4f(0, 0, 0, 1);
		EntityUtil.renderAABB(aabb);

		GL11.glLineWidth(2);
		GL11.glColor4f(1, 1, 0, 1);
		EntityUtil.renderAABB(aabb);

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glColor4f(1, 1, 1, 1);

		ITextComponent itc = te.getDisplayName();
		if (itc != null)
		{
			FontRenderer fr = this.getFontRenderer();

			GL.Enable(EnableCap.Texture2D);

			GL11.glColor4f(1, 1, 1, 1);
			GL.Translate(x + 0.5, y + 0.5, z + 0.5);
			GL11.glRotatef(-m.getRenderManager().playerViewY, 0, 1, 0);
			GL11.glRotatef(m.getRenderManager().playerViewX, 1, 0, 0);
			GL.Scale(-1 / 32f, -1 / 32f, 1);

			String s = itc.getFormattedText();
			int w = fr.getStringWidth(s);
			fr.drawString(s, -w / 2, -fr.FONT_HEIGHT / 2, 0xFFFFFF);
		}

		GL.PopMatrix();
		GL11.glPopAttrib();
		GL11.glPopAttrib();
		m.entityRenderer.enableLightmap();
	}
}
