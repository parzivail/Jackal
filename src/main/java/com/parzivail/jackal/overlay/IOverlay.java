package com.parzivail.jackal.overlay;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;

/**
 * Created by Colby on 3/9/2018.
 */
public interface IOverlay
{
	KeyBinding getKeyBinding();

	boolean isEnabled();

	String getName();

	Item getIcon();

	void handleKeyInput();

	boolean shouldRender(RenderPhase phase);

	void render(EntityLivingBase entity, float partialTicks, float x, float y, float z);
}
