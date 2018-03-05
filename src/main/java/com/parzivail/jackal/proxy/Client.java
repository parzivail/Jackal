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
	public static KeyBinding keyWallhack;

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
		keyWallhack = registerKeybind("wallHack", Keyboard.KEY_X);
	}

	private static KeyBinding registerKeybind(String keyName, int keyCode)
	{
		KeyBinding b = new KeyBinding("key." + Resources.MODID + "." + keyName, keyCode, "key." + Resources.MODID);
		ClientRegistry.registerKeyBinding(b);
		return b;
	}
}