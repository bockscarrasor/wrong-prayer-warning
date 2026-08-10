# Wrong Prayer Warning

A RuneLite plugin that watches your equipped weapon and active prayer, and
warns you when they don't match — e.g. a bow equipped while a melee prayer
(Piety, Chivalry, etc.) is active, or a scimitar out while Rigour is on.

## How it works

- Every game tick, it checks your equipped weapon and classifies it as
  **melee**, **ranged**, or **magic** based on its name (bows/crossbows/
  darts → ranged; staves/wands/tridents → magic; everything else → melee).
- It checks which offensive prayer category is active (Piety/Chivalry/etc →
  melee; Rigour/Eagle Eye/etc → ranged; Augury/Mystic Might/etc → magic).
- If the two don't match, it fires a warning once (not spammed every tick),
  and resets once you fix it or the mismatch changes.

## Settings (in the RuneLite plugin panel)

- **Desktop notification** — OS/tray popup on mismatch (default on)
- **Chat message** — prints a warning line in your chat box (default on)
- **Warn when no offensive prayer active** — also nag you if you have a
  weapon out but no offensive prayer running at all (default off, since
  plenty of people fight prayer-off on purpose)

## Known limitations

Weapon classification is name-keyword based rather than pulling from the
game's real attack-style data, so a handful of oddball weapons (e.g. some
staves that are meleed, or special/unique items) might get misclassified.
If you hit one, it's an easy fix — just add the item name keyword to the
`MAGIC_KEYWORDS` / `RANGED_KEYWORDS` sets in `WrongPrayerPlugin.java`.

## Running it

This isn't on the Plugin Hub, so to use it you run it from source using
RuneLite's standard external-plugin development setup:

1. Install IntelliJ IDEA (Community edition is fine) and JDK 11.
2. Clone the RuneLite client: `git clone https://github.com/runelite/runelite.git`
3. Open that project in IntelliJ and let it import/build once so the
   `runelite-client` module resolves.
4. Copy this `wrong-prayer-warning` folder into the RuneLite checkout as a
   sibling Gradle module (or just add its `src/main/java/com/wrongprayer`
   package into an existing external-plugin module you already use for
   local plugins).
5. Run the `RuneLite` main class (`net.runelite.client.RuneLite`) from
   IntelliJ with `-Ddeveloper.mode=true` in the run configuration's VM
   options. The client will start with your plugin loaded automatically —
   look for "Wrong Prayer Warning" in the plugin list to enable it.

If you'd rather not deal with a full dev environment, the other route is to
package this as a proper Plugin Hub submission (RuneLite's docs walk through
that at https://github.com/runelite/plugin-hub) — that gets you an
install/update flow through the in-client plugin browser instead of running
from source each time.
