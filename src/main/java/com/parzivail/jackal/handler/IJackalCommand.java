package com.parzivail.jackal.handler;

import com.parzivail.jackal.util.Enumerable;

public interface IJackalCommand
{
	String getName();

	String getCommand();

	String getUsage();

	void handle(Enumerable<String> args);
}
