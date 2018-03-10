package com.parzivail.jackal.util.overlay;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;

/**
 * Created by Colby on 3/9/2018.
 */
public interface IJackalModule
{
	/**
	 * Gets the key binding associated with the module
	 *
	 * @return a <code>KeyBinding</code>
	 */
	KeyBinding getKeyBinding();

	/**
	 * Gets whether or not the module is disabled
	 * @return <code>true</code> if the module is enabled
	 */
	boolean isEnabled();

	/**
	 * Gets the translated name of the module
	 * @return a <code>String</code>
	 */
	String getName();

	/**
	 * Gets the icon associated with the module
	 * @return
	 */
	Item getIcon();

	/**
	 * Handle keyboard input
	 */
	void handleKeyInput();

	/**
	 * Gets whether or not the module should render under the specified conditions
	 *
	 * @param phase The phase to check against
	 * @return <code>true</code> if the module should render
	 */
	boolean shouldRender(RenderScope phase);

	/**
	 * Renders the module
	 * @param entity The entity associated with the render
	 * @param partialTicks Render partial ticks
	 * @param x The x position associated with the render
	 * @param y The y position associated with the render
	 * @param z The z position associated with the render
	 */
	void render(EntityLivingBase entity, float partialTicks, float x, float y, float z);
}
