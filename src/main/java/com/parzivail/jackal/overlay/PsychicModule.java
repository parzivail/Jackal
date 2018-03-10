package com.parzivail.jackal.overlay;

import com.parzivail.jackal.proxy.Client;
import com.parzivail.jackal.util.EntityUtil;
import com.parzivail.jackal.util.Toast;
import com.parzivail.jackal.util.overlay.IJackalModule;
import com.parzivail.jackal.util.overlay.RenderScope;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public class PsychicModule implements IJackalModule
{
	@SideOnly(Side.CLIENT)
	private static KeyBinding key;

	private static boolean shouldSearch = false;
	private static boolean searchDangerous = false;
	private static boolean shouldReset = false;
	private static boolean enabled;
	private static Entity link;

	public PsychicModule()
	{
		key = Client.registerKeybind("psychicLink", Keyboard.KEY_NONE);
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
		return "Psychic Link";
	}

	@Override
	public Item getIcon()
	{
		return Items.CARROT_ON_A_STICK;
	}

	@Override
	public void handleKeyInput()
	{
		enabled = !enabled;
		if (enabled)
		{
			searchDangerous = Keyboard.isKeyDown(Keyboard.KEY_LMENU);
			shouldSearch = true;
		}
		else
			shouldReset = true;
	}

	@Override
	public boolean shouldRender(RenderScope phase)
	{
		return phase == RenderScope.Once && (enabled || shouldReset);
	}

	@Override
	public void render(EntityLivingBase entity, float partialTicks, float x, float y, float z)
	{
		Minecraft m = Minecraft.getMinecraft();

		if (shouldSearch)
		{
			Vec3d pos = m.player.getPositionEyes(partialTicks);
			Vec3d look = m.player.getLook(partialTicks);

			Tuple<Entity, RayTraceResult> hit = EntityUtil.findEntityOnPath(searchDangerous ? Entity.class : EntityLivingBase.class, m.player, m.player.world, pos, pos.add(look.scale(100)));
			if (hit != null)
			{
				link = hit.getFirst();
				Client.showOverlayToggleToast(this);
			}
			else
			{
				new Toast("No entity found", getIcon().getDefaultInstance(), Toast.LENGTH_SHORT).show();
				enabled = false;
				shouldReset = true;
			}

			shouldSearch = false;
		}

		if (shouldReset)
		{
			m.setRenderViewEntity(m.player);
			shouldReset = false;
			enabled = false;
		}
		else if (link != null)
		{
			if (link.isDead)
				link = null;
			else
				m.setRenderViewEntity(link);
		}
	}
}
