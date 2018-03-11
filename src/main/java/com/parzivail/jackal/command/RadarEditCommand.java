package com.parzivail.jackal.command;

import com.parzivail.jackal.overlay.PlayerRadarModule;
import com.parzivail.jackal.proxy.Client;
import com.parzivail.jackal.util.EntityUtil;
import com.parzivail.jackal.util.Toast;
import com.parzivail.jackal.util.overlay.IJackalModule;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RadarEditCommand extends CommandBase implements IClientCommand
{
	static final ArrayList<String> verbs = new ArrayList<>();

	static
	{
		verbs.add("hide");
		verbs.add("clear");
		verbs.add("show");
	}

	@Override
	public String getName()
	{
		return "j.radar";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "Usage: <hide|clear|show> <player>";
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		switch (args.length)
		{
			case 1:
				return CommandBase.getListOfStringsMatchingLastWord(args, verbs);
			case 2:
				return CommandBase.getListOfStringsMatchingLastWord(args, EntityUtil.getOnlinePlayerNames());
			default:
				return super.getTabCompletions(server, sender, args, targetPos);
		}
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
	{
		Minecraft m = Minecraft.getMinecraft();
		if (args.length < 2)
		{
			sender.sendMessage(new TextComponentString(getUsage(sender)));
			return;
		}

		switch (args[0])
		{
			case "hide":
				if (!PlayerRadarModule.blacklist(args[1]))
					sender.sendMessage(new TextComponentString(getUsage(sender)));
				else
				{
					IJackalModule module = Client.getModule(PlayerRadarModule.class);
					new Toast(String.format("%s hidden", args[1]), module.getIcon().getDefaultInstance(), Toast.LENGTH_SHORT).show();
				}
				break;
			case "clear":
				if (!PlayerRadarModule.forget(args[1]))
					sender.sendMessage(new TextComponentString(getUsage(sender)));
				else
				{
					IJackalModule module = Client.getModule(PlayerRadarModule.class);
					new Toast(String.format("%s cleared", args[1]), module.getIcon().getDefaultInstance(), Toast.LENGTH_SHORT).show();
				}
				break;
			case "show":
				if (!PlayerRadarModule.persist(args[1]))
					sender.sendMessage(new TextComponentString(getUsage(sender)));
				else
				{
					IJackalModule module = Client.getModule(PlayerRadarModule.class);
					new Toast(String.format("%s shown", args[1]), module.getIcon().getDefaultInstance(), Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				sender.sendMessage(new TextComponentString(getUsage(sender)));
		}
	}

	@Override
	public boolean allowUsageWithoutPrefix(ICommandSender sender, String message)
	{
		return false;
	}
}
