package com.parzivail.jackal.proxy;

import com.parzivail.jackal.Resources;
import com.parzivail.jackal.util.ShaderHelper;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class Client extends Common
{
	@SideOnly(Side.CLIENT)
	public static KeyBinding keyWallhack;

	@Override
	public void init()
	{
		ShaderHelper.initShaders();
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
