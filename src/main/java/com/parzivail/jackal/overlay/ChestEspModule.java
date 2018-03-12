package com.parzivail.jackal.overlay;

import com.parzivail.jackal.proxy.Client;
import com.parzivail.jackal.util.overlay.IJackalModule;
import com.parzivail.jackal.util.overlay.RenderScope;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public class ChestEspModule implements IJackalModule
{
	@SideOnly(Side.CLIENT)
	private static KeyBinding key;

	private static boolean enabled;

	public ChestEspModule()
	{
		key = Client.registerKeybind("chestEsp", Keyboard.KEY_B);
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
		return "Chest ESP";
	}

	@Override
	public Item getIcon()
	{
		return Item.getItemFromBlock(Blocks.CHEST);
	}

	@Override
	public void handleKeyInput()
	{
		enabled = !enabled;
		Client.showOverlayToggleToast(this);
	}

	@Override
	public boolean shouldRender(RenderScope phase)
	{
		return false;
	}

	@Override
	public void render(EntityLivingBase entity, float partialTicks, float x, float y, float z)
	{

	}
}
