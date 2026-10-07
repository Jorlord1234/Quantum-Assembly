# What changed in 0.13

## Nebula Blaze Burner
- New look: Create's own Blaze Burner model, recolored purple (cold = dim purple head, lit = bright purple).
  The model and the recolored textures are copies inside this mod, so nothing is shared with Create's burner.
  (Create's assets are Create's work. Check Create's license before you publish this mod.)
- New recipe (smithing table): Nova Template + Blaze Burner + Quantum Assembly Kit (the Kit replaces the Dragon Head).
- The ritual moved from the Kit to the burner:
  1. Right-click the burner with a FULL Experience Catalyst, a Nova Coin and a Totem of Undying (one each).
  2. Light it with a Cake of Nebulae.
  3. After the ritual time (config: ritualSeconds) it drops a Quantum Gate and goes cold.
  - Empty hand: shows what is inside. Shift + empty hand: gives the items back. Breaking the burner drops them.

## Quantum Assembly Kit
- Now a pure storage block: 9 x 6 grid, stacks up to 256 (config: kitStackLimit). No ritual slots any more.

## Pocket Controls
- New screen: title bar, one card per upgrade with level pips and the Starglass cost, and the machine buttons below.
- New upgrade: GUESTS. How many other players can be inside your space at the same time:
  level 0 = 1, then 2, 4, 8, 16 (cost 4 / 8 / 16 / 32 Starglass). The owner is never counted.
  Someone who uses a linked Gate when the space is full gets a message instead.
- "Kinetic link" is shown as a locked card (coming soon).

## Not done yet
- Kinetic link (RPM / stress into the pocket): needs Create as a compile dependency.
- Sophisticated Backpacks upgrades on the Kit: needs Sophisticated Backpacks as a compile dependency.
