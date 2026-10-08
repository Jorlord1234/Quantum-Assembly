# What changed in 0.13.1

## Quantum Assembly Kit
- Slots now really hold big stacks (config kitStackLimit, default 256). Before, the slot class capped every slot at the
  item's normal stack size (64), so the setting did nothing. Left-click still picks up 64 at a time.

## Nebula Blaze Burner
- See-through again: the model now renders as "cutout", so the gaps in the cage show what is behind it.
- Cold burner = cage + a small dull purple head ONLY (no rods, so it no longer looks fueled).
- Lit burner = cage + bigger bright purple head + the flame rods.
- Both are built from Create's cage / rod models (copied into this mod) with purple textures.
