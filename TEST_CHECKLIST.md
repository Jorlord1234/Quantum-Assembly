# Test checklist for version 0.4

Tell Claude which ones fail.

1. [ ] Right-click the Nova Coin: the Waystones teleport menu opens
2. [ ] Teleporting from that menu costs no XP, and the coin stays in your inventory
3. [ ] Experience Catalyst on a Blaze Burner: burner goes PURPLE, the Catalyst is used up
4. [ ] Cake of Nebulae on a Blaze Burner: burner goes PURPLE
5. [ ] Chef's Hat looks right (texture) in hand and on your head
6. [ ] Earning the "Master Chef" advancement gives the Cauldron and the Hat
7. [ ] Cake of Nebulae recipe exists: Cauldron + Echo Shard + Dragon's Breath + Obsidian Dust (Cauldron comes back)
8. [ ] Quantum Assembly Kit recipe exists: Netherite Backpack + Toolbox + Inception + Stack Tier 4 + Everlasting
9. [ ] Unfinished Catalyst progress bar, void coin and netherite-only ore still work (from 0.3)

Config file (config/quantum_assembly-common.toml):
- catalystConsumedOnUse = true / false
- masterChefAdvancement = "modid:path" (only needed if the advancement is not found automatically)
