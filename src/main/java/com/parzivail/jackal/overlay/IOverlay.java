package com.parzivail.jackal.overlay;

import net.minecraft.entity.EntityLivingBase;

/**
 * Created by Colby on 3/9/2018.
 */
public interface IOverlay
{
	boolean shouldRender(RenderPhase phase);

	void render(EntityLivingBase entity, float partialTicks, float x, float y, float z);
}
