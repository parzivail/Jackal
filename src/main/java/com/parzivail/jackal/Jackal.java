package com.parzivail.jackal;

import com.parzivail.jackal.handler.EventHandler;
import com.parzivail.jackal.proxy.Common;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Resources.MODID, version = Resources.VERSION)
public class Jackal
{
	@SidedProxy(clientSide = "com.parzivail.jackal.proxy.Client", serverSide = "com.parzivail.jackal.proxy.Common")
	public static Common proxy;

	public static EventHandler eventHandler;

	@Mod.EventHandler
	public void init(FMLPreInitializationEvent event)
	{
		proxy.preInit();
	}

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
