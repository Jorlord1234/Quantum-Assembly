# What changed in 0.18.0

## Nebula Blaze Burner
- The cage now really is see-through (the model is a cutout, and the block is registered as cutout).
- Head and rods are drawn by a renderer and animated like Create's burner: the head bobs and turns to look at the nearest
  player, and when lit the two sets of rods float up and down. Cold = a small dull head, lit = bright head and rods.

## Underground Quantum Lab (rebuilt, stronghold style)
- It is now made of many pieces that connect in a random layout every time: halls, corners, T-junctions, crossings, and
  six room types (library, lab, workshop, test chamber, storage, meteor vault). Dead ends are closed with a wall plug.
- Entrance: a crater with a meteor in it. In the bottom of the crater is a 1 x 1 ladder hole that goes 48+ blocks straight
  down into a hub deep underground (so it can no longer stick out of a hillside). The hub always has a door toward a workshop.
- Rooms were redesigned: the library has bookshelf walls, a balcony with a ladder, reading desks, chandeliers and beams;
  the lab has a glow squid tank; the workshop has the Quantum Engineer and a Kit; the test chamber has four cages with spawners.
- Loot: separate tables per room (library, lab, workshop, test chamber, meteor vault) and barrels hold supplies. The
  vault has the best loot, including a rare Netherite Upgrade Template. Rare finds are much rarer than before.

## Mob: Star Crawler (replaces the Star Mite)
- A big armored beetle with its own model, 30 health, 5 damage, glowing shell and eyes. Whatever it bites starts glowing.
- Drops glow ink sacs and sometimes Starglass. Spawn egg in the creative tab. Old Star Mite is gone.

## Villager trades (much stingier)
- He buys lab leftovers (amethyst, obsidian, crying obsidian, glow ink, Starglass) for single emeralds, and sells a few
  decor/utility things at high prices (end rods, ender pearl, XP bottles, tinted glass, ender chest, echo shard,
  amethyst blocks, and one Totem of Undying for 64 emeralds + 4 Starglass). No Cake, Pocket parts, Nova items or Netherite template.

## New items and recipes
- Heavy Duty Plate: Create sequenced assembly. Iron block -> press, press -> fill with lava (250 mB) -> press, press.
- Polished Starglass: Starglass polished with Create sandpaper.
- Quantum Processor: top row electron tube, transmitter, electron tube. Middle row 3 Polished Starglass. Bottom row 3 Heavy Duty Plates.
- Quantum Assembly Kit recipe now also has 4 Heavy Duty Plates in the corners (plates in the 4 corners, same plus shape inside).
- Nebula Batter now turns into a Cake of Nebulae ONLY in the Master Chef's Cauldron: right-click the Cauldron with the
  batter while it stands on a heat source (lit campfire, blaze burner, lava, fire). It cooks for 30 seconds and the cake
  pops out. The old cooking-pot recipe is gone. Nothing uses the Quantum Processor yet.

## New textures
- Nova Coin, Uncompleted Nova Coin, Nova Template, Starglass (a four-pointed star gem), Polished Starglass (a cut gem),
  Starglass Ore, Heavy Duty Plate, Quantum Processor, Quantum Gate icon.

## Quantum Gate
- Now a portal ring with an animated swirling opening. You link it like before (Link a Gate, then right-click it),
  after that you just walk through it. You come back out in front of the gate. 3 second cooldown so you cannot bounce.

## Not done yet
- Sophisticated Backpacks upgrades on the Kit (needs the Sophisticated Backpacks / Core versions your pack uses).
- Kinetic link (RPM / stress into the pocket): needs Create as a compile dependency.
