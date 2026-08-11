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

