package com.parzivail.jackal;

import com.parzivail.jackal.handler.EventHandler;
import com.parzivail.jackal.proxy.Common;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Resources.MODID, version = Resources.VERSION)
public class Jackal
{
	@SidedProxy(clientSide = "com.parzivail.jackal.proxy.Client", serverSide = "com.parzivail.jackal.proxy.Common")
	public static Common proxy;

	public static EventHandler eventHandler;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();

		eventHandler = new EventHandler();
		FMLCommonHandler.instance().bus().register(eventHandler);
		MinecraftForge.EVENT_BUS.register(eventHandler);
	}

	@Mod.EventHandler
	public void init(FMLPostInitializationEvent event)
	{
		proxy.postInit();
	}
}
