# What changed in 0.16.0

## Where to get Starglass ore (it never generated before, only in creative)
- Starglass ore now generates in THE END, in the end stone of the outer islands (and the main island's stone):
  veins of up to 6, about 4 veins per chunk, between height 20 and 80.
- It still needs a netherite pickaxe. Fortune works.
- Files: data/quantum_assembly/worldgen/configured_feature + placed_feature (starglass_ore_end) and
  neoforge/biome_modifier/add_starglass_ore.json

## First structure: the Star Shrine
- A small ruin of end stone bricks and purpur with four amethyst-topped pillars, four Starglass ore blocks around a
  loot chest, on the outer End islands (highlands, midlands, barrens). Roughly one every 10 x 10 chunks.
- Chest loot: Starglass, ender pearls, amethyst shards, crying obsidian, golden apples and (rare) a Netherite
  Upgrade Smithing Template (one of the things the Nova Template needs).
- Files: worldgen/structure/star_shrine, structure_set, template_pool, structure/star_shrine/shrine.nbt,
  loot_table/chests/star_shrine.json

## Test commands (in a creative world, op)
- /place feature quantum_assembly:starglass_ore_end
- /place structure quantum_assembly:star_shrine
- /locate structure quantum_assembly:star_shrine   (in the End, past the outer gateway)
