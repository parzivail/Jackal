package com.parzivail.jackal.proxy;

import com.parzivail.jackal.Resources;
import com.parzivail.jackal.overlay.ArrowGuideOverlay;
import com.parzivail.jackal.overlay.IOverlay;
import com.parzivail.jackal.overlay.RenderPhase;
import com.parzivail.jackal.overlay.WallhackOverlay;
import com.parzivail.jackal.util.Enumerable;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public class Client extends Common
{
	@SideOnly(Side.CLIENT)
	public static KeyBinding keyDebug;
	@SideOnly(Side.CLIENT)
	public static KeyBinding keyWallhack;
	@SideOnly(Side.CLIENT)
	public static KeyBinding keyArrowGuide;
	@SideOnly(Side.CLIENT)
	public static KeyBinding keyZoom;

	public static boolean isArrowGuide;

	private static Enumerable<IOverlay> overlays = Enumerable.empty();

	public static void renderOverlays(EntityLivingBase entity, float partialTicks, RenderPhase phase, float x, float y, float z)
	{
		overlays.where(iOverlay -> iOverlay.shouldRender(phase)).forEach(iOverlay -> iOverlay.render(entity, partialTicks, x, y, z));
	}

	@Override
	public void preInit()
	{
	}

	@Override
	public void init()
	{
		overlays.add(new ArrowGuideOverlay());
		overlays.add(new WallhackOverlay());
	}

	@Override
	public void postInit()
	{
		keyDebug = registerKeybind("keyDebug", Keyboard.KEY_N);

		keyWallhack = registerKeybind("wallHack", Keyboard.KEY_X);
		keyArrowGuide = registerKeybind("arrowGuide", Keyboard.KEY_G);
		keyZoom = registerKeybind("zoom", Keyboard.KEY_Z);
	}

	private static KeyBinding registerKeybind(String keyName, int keyCode)
	{
		KeyBinding b = new KeyBinding("key." + Resources.MODID + "." + keyName, keyCode, "key." + Resources.MODID);
		ClientRegistry.registerKeyBinding(b);
		return b;
	}
}