package com.parzivail.jackal.proxy;

import com.parzivail.jackal.Resources;
import com.parzivail.jackal.command.RadarEditCommand;
import com.parzivail.jackal.overlay.*;
import com.parzivail.jackal.render.RenderChestESP;
import com.parzivail.jackal.util.Enumerable;
import com.parzivail.jackal.util.Toast;
import com.parzivail.jackal.util.overlay.IJackalModule;
import com.parzivail.jackal.util.overlay.RenderScope;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public class Client extends Common
{
	@SideOnly(Side.CLIENT)
	public static KeyBinding keyDebug;
	@SideOnly(Side.CLIENT)
	public static KeyBinding keyZoom;

	private static Enumerable<IJackalModule> overlays = Enumerable.empty();

	public static void renderOverlays(EntityLivingBase entity, float partialTicks, RenderScope phase, float x, float y, float z)
	{
		overlays.where(iModule -> iModule.shouldRender(phase)).forEach(iModule -> iModule.render(entity, partialTicks, x, y, z));
	}

	public static void delegateKeyInputToOverlays()
	{
		for (IJackalModule overlay : overlays)
			if (overlay.getKeyBinding().isPressed())
				overlay.handleKeyInput();
	}

	public static void showOverlayToggleToast(IJackalModule overlay)
	{
		new Toast(String.format("%s %s", overlay.getName(), overlay.isEnabled() ? "enabled" : "disabled"), overlay.getIcon().getDefaultInstance(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void preInit()
	{
	}

	@Override
	public void init()
	{
		RenderChestESP rcesp = new RenderChestESP();
		rcesp.setRendererDispatcher(TileEntityRendererDispatcher.instance);
		TileEntityRendererDispatcher.instance.renderers.replace(TileEntityChest.class, rcesp);
	}

	public static IJackalModule getModule(Class<? extends IJackalModule> clazz)
	{
		return overlays.first(clazz::isInstance);
	}

	@Override
	public void postInit()
	{
		keyDebug = registerKeybind("keyDebug", Keyboard.KEY_N);
		keyZoom = registerKeybind("zoom", Keyboard.KEY_Z);

		overlays.add(new ArrowGuideModule());
		overlays.add(new WallhackModule());
		overlays.add(new PsychicModule());
		overlays.add(new PlayerRadarModule());
		overlays.add(new ChestEspModule());

		ClientCommandHandler.instance.registerCommand(new RadarEditCommand());
	}

	public static KeyBinding registerKeybind(String keyName, int keyCode)
	{
		KeyBinding b = new KeyBinding("key." + Resources.MODID + "." + keyName, keyCode, "key." + Resources.MODID);
		ClientRegistry.registerKeyBinding(b);
		return b;
	}
}