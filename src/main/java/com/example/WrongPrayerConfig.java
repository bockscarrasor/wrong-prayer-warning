package com.wrongprayer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("wrongprayer")
public interface WrongPrayerConfig extends Config
{
	@ConfigItem(
		keyName = "sendNotification",
		name = "Desktop notification",
		description = "Send a system/tray notification when your weapon and prayer don't match",
		position = 1
	)
	default boolean sendNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "sendChatMessage",
		name = "Chat message",
		description = "Print a message in the game chat when your weapon and prayer don't match",
		position = 2
	)
	default boolean sendChatMessage()
	{
		return true;
	}

	@ConfigItem(
		keyName = "warnOnNoPrayer",
		name = "Warn when no offensive prayer active",
		description = "Also warn if you have a weapon equipped but no offensive combat prayer is on at all",
		position = 3
	)
	default boolean warnOnNoPrayer()
	{
		return false;
	}
}
