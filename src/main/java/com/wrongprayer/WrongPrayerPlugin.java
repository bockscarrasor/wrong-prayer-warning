package com.wrongprayer;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Prayer;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Set;

/**
 * Warns the player when their equipped weapon's combat style (melee/ranged/magic)
 * does not match the offensive prayer they currently have active.
 */
@Slf4j
@PluginDescriptor(
	name = "Wrong Prayer Warning",
	description = "Warns you when your equipped weapon doesn't match your active offensive prayer",
	tags = {"prayer", "combat", "pvp", "notification", "bow", "melee", "mage"}
)
public class WrongPrayerPlugin extends Plugin
{
	// Keyword-based weapon classification. Checked in this order, so anything
	// with "staff" etc. is treated as magic even if it could technically melee.
	private static final Set<String> MAGIC_KEYWORDS = Set.of(
		"staff", "wand", "sceptre", "trident", "tome", "warped sceptre", "harmonised nightmare staff", "sanguinesti staff"
	);

	private static final Set<String> RANGED_KEYWORDS = Set.of(
		"bow", "crossbow", "ballista", "chinchompa", "blowpipe", "dart", "knife", "javelin", "throwing axe"
	);

	private static final Set<Prayer> MELEE_PRAYERS = Set.of(
		Prayer.CLARITY_OF_THOUGHT, Prayer.IMPROVED_REFLEXES, Prayer.INCREDIBLE_REFLEXES,
		Prayer.BURST_OF_STRENGTH, Prayer.SUPERHUMAN_STRENGTH, Prayer.ULTIMATE_STRENGTH,
		Prayer.CHIVALRY, Prayer.PIETY
	);

	private static final Set<Prayer> RANGED_PRAYERS = Set.of(
		Prayer.SHARP_EYE, Prayer.HAWK_EYE, Prayer.EAGLE_EYE, Prayer.RIGOUR
	);

	private static final Set<Prayer> MAGIC_PRAYERS = Set.of(
		Prayer.MYSTIC_WILL, Prayer.MYSTIC_LORE, Prayer.MYSTIC_MIGHT, Prayer.AUGURY
	);

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private Notifier notifier;

	@Inject
	private WrongPrayerConfig config;

	// Used so we only fire a warning once per mismatch, instead of every tick.
	private String lastWarningKey = null;

	@Provides
	WrongPrayerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WrongPrayerConfig.class);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		CombatStyle weaponStyle = getEquippedWeaponStyle();

		if (weaponStyle == CombatStyle.NONE)
		{
			// No weapon (or unarmed) - nothing to warn about, reset state.
			lastWarningKey = null;
			return;
		}

		CombatStyle prayerStyle = getActivePrayerStyle();

		boolean mismatch = (prayerStyle == CombatStyle.NONE && config.warnOnNoPrayer())
			|| (prayerStyle != CombatStyle.NONE && prayerStyle != weaponStyle);

		String key = weaponStyle + ":" + prayerStyle;

		if (mismatch)
		{
			if (!key.equals(lastWarningKey))
			{
				warn(weaponStyle, prayerStyle);
			}
			lastWarningKey = key;
		}
		else
		{
			lastWarningKey = null;
		}
	}

	private void warn(CombatStyle weaponStyle, CombatStyle prayerStyle)
	{
		String message = prayerStyle == CombatStyle.NONE
			? String.format("You have a %s weapon equipped but no offensive prayer active!", weaponStyle.label)
			: String.format("You have a %s weapon equipped but your %s prayer is active!", weaponStyle.label, prayerStyle.label);

		if (config.sendChatMessage())
		{
			client.addChatMessage(ChatMessageType.CONSOLE, "", message, null);
		}

		if (config.sendNotification())
		{
			notifier.notify(message);
		}
	}

	private CombatStyle getEquippedWeaponStyle()
	{
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null)
		{
			return CombatStyle.NONE;
		}

		Item weapon = equipment.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
		if (weapon == null || weapon.getId() <= 0)
		{
			return CombatStyle.NONE;
		}

		String name = itemManager.getItemComposition(weapon.getId()).getName().toLowerCase(Locale.ROOT);

		for (String keyword : MAGIC_KEYWORDS)
		{
			if (name.contains(keyword))
			{
				return CombatStyle.MAGIC;
			}
		}

		for (String keyword : RANGED_KEYWORDS)
		{
			if (name.contains(keyword))
			{
				return CombatStyle.RANGED;
			}
		}

		return CombatStyle.MELEE;
	}

	private CombatStyle getActivePrayerStyle()
	{
		for (Prayer p : MELEE_PRAYERS)
		{
			if (client.isPrayerActive(p))
			{
				return CombatStyle.MELEE;
			}
		}
		for (Prayer p : RANGED_PRAYERS)
		{
			if (client.isPrayerActive(p))
			{
				return CombatStyle.RANGED;
			}
		}
		for (Prayer p : MAGIC_PRAYERS)
		{
			if (client.isPrayerActive(p))
			{
				return CombatStyle.MAGIC;
			}
		}
		return CombatStyle.NONE;
	}

	private enum CombatStyle
	{
		NONE(""),
		MELEE("melee"),
		RANGED("ranged"),
		MAGIC("magic");

		final String label;

		CombatStyle(String label)
		{
			this.label = label;
		}
	}
}
