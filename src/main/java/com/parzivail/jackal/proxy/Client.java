package com.parzivail.jackal.proxy;

import com.parzivail.jackal.Resources;
import net.minecraft.client.settings.KeyBinding;
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
	public static KeyBinding keyZoom;

	@Override
	public void preInit()
	{
	}

	@Override
	public void init()
	{

	}

	@Override
	public void postInit()
	{
		keyDebug = registerKeybind("keyDebug", Keyboard.KEY_N);

		keyWallhack = registerKeybind("wallHack", Keyboard.KEY_X);
		keyZoom = registerKeybind("zoom", Keyboard.KEY_Z);
	}

	private static KeyBinding registerKeybind(String keyName, int keyCode)
	{
		KeyBinding b = new KeyBinding("key." + Resources.MODID + "." + keyName, keyCode, "key." + Resources.MODID);
		ClientRegistry.registerKeyBinding(b);
		return b;
	}
}