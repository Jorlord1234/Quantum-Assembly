# What changed in 0.17.0

## Structure: the Quantum Lab (replaces the small Star Shrine)
- An underground research complex with a crater at the surface. Climb down the ladder in the crater shaft into a hub that
  opens into four rooms:
  - LIBRARY (north): tall bookshelf walls, reading aisles, lectern, lanterns, cobwebs, 2 loot chests
  - LAB (west): brewing stands and cauldrons, a glass tank with glow squid, a caged spawner, 2 loot chests
  - WORKSHOP (east): smithing table, anvil, grindstone, blast furnace, a Quantum Assembly Kit and the Quantum Engineer
    villager, 2 loot chests and barrels
  - TEST CHAMBER (south): three tinted-glass cages with Star Mite spawners and Starglass ore, 2 loot chests
- The crater is lined with Starglass ore, amethyst and crying obsidian, with amethyst clusters on the floor.
- Generates in the Overworld on land (forest, taiga, hills, mountains, badlands, savanna, jungle, plains, desert,
  meadow, snowy plains). Rare: spacing about 28 chunks.
- The old Star Shrine is gone. The build leaves its files out of the jar even if they are still in your repo.

## Mob: Star Mite
- A silverfish-like bug bred in the lab on Starglass: purple, glowing in the dark. Whatever it bites starts glowing
  (so you cannot hide). Only spawns from the lab's spawners (and a spawn egg in the creative tab).
- Drops glow ink sacs (looting works) and sometimes (25%, killed by a player) Starglass.

## Villager: Quantum Engineer
- Job-site block: the Quantum Assembly Kit. Purple outfit. Lives in the lab's workshop, and any villager can take the
  job next to a Kit.
- Trades: Novice buys amethyst shards and obsidian, sells end rods. Apprentice buys Starglass, sells ender pearls.
  Journeyman sells echo shard / ender chest, buys crying obsidian. Expert sells a Pocket Casing and a Totem of
  Undying. Master sells a Cake of Nebulae (3 Starglass) and a Netherite Upgrade Smithing Template.
  The Nova Template, Experience Catalyst and the Kit are never sold.

## Test commands (creative, op)
- /place structure quantum_assembly:quantum_lab
- /locate structure quantum_assembly:quantum_lab
- /summon quantum_assembly:star_mite
- /summon minecraft:villager ~ ~ ~ {VillagerData:{profession:"quantum_assembly:quantum_engineer",level:5,type:"minecraft:plains"}}
