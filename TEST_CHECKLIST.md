# Test checklist for version 0.9

Tell Claude which ones fail.

1. [ ] Catalyst on a Blaze Burner: burner gets SUPER Liquid Experience, Catalyst loses 1 bucket, item is NOT used up, no normal XP goes in
2. [ ] Kit recipe is shaped: Inception top, Netherite Backpack / Stack Tier 4 / Toolbox middle row, Everlasting bottom
3. [ ] Chef's Hat looks right in hand and on your head
4. [ ] Gems on the Quantum Assembly Kit look right
5. [ ] Nova Template and Cake of Nebulae are made in a Mixer (the Cauldron comes back out of the cake recipe)
6. [ ] Cauldron and Kit face you when placed (all 4 directions)
7. [ ] Cake of Nebulae on a Blaze Burner: superheated for a very long time
8. [ ] Unfinished Catalyst no longer shows in the creative tab (it still appears inside Sequenced Assembly)
9. [ ] Nova Coin menu, Master Chef reward, void coin, netherite-only ore still work (from 0.4)

## New in 0.9: the Nebula Blaze Burner ritual
10. [ ] Smithing table: Nova Template + Blaze Burner + Dragon Head = Nebula Blaze Burner (template used up)
11. [ ] Cold burner looks dark and cosmic
12. [ ] Cake of Nebulae on it: purple flame + purple particles, cake used up
13. [ ] After 5 minutes it goes cold again (config: nebulaBurnerLitSeconds, 0 = forever)
14. [ ] Right-click the Kit with a FULL Catalyst, a Nova Coin, a Totem: each goes in its slot (action bar message), wrong items do nothing, not-full Catalyst is refused
15. [ ] Shift + empty hand on the Kit gives the items back
16. [ ] Kit on a LIT burner with all 3 items: 10 second ritual with particles (config: ritualSeconds)
17. [ ] Result: the 3 items are used up, a "Quantum Gate (test item)" pops out, burner goes cold, Kit stays
18. [ ] Break the Kit: items drop. Break the burner during the ritual: items drop, ritual stops
19. [ ] Nothing happens if the burner is cold or the Kit is somewhere else
Note: the Nebula burner no longer heats Basins (only for the ritual), as the design document suggested.

# Extra tests for 0.10
15. [ ] Nebula Blaze Burner looks purple when lit and dark when cold
16. [ ] Hold a Catalyst with 1+ bucket and right-click a Blaze Enchanter. It says "Super experience added"
17. [ ] A normal Create Blaze Burner does NOTHING when you right-click it with a Catalyst or a Cake
18. [ ] Throw an Uncompleted Nova Coin into the Overworld void: nothing. In the End void: it becomes a Nova Coin
19. [ ] Mine Starglass Ore several times: drops 1 to 4
20. [ ] Wear the Chef's Hat. It is very tall
21. [ ] Place the Cauldron. Put it over a heat source. It opens the cooking pot screen
22. [ ] Make Cake Base (press), Nebula Batter (spout) and cook the Cake in the Cauldron
23. [ ] The advancements tab "Quantum Assembly" shows up

# Extra tests for 0.11
24. [ ] Right-click the Cauldron: the cooking pot screen stays open. Pick-block shows the golden Cauldron
25. [ ] The Nebula burner has the see-through cage and the purple blaze. Normal Create burners look normal
26. [ ] Kit: handle is small, gem on the lid is in the middle
27. [ ] Right-click the Kit (no item in hand): a screen with 3 ritual slots and storage opens
28. [ ] Put 100+ of one item in a Kit slot. It keeps the full number after you close and reopen it
29. [ ] Break the Kit: everything drops
30. [ ] Chef's Hat: long middle, small puff on top
31. [ ] Place a Quantum Gate and right-click it: you arrive on a floor in the pocket dimension
32. [ ] There is a Gate behind you: right-click it to come back
33. [ ] You cannot walk off the edge (invisible walls). Digging down is not possible (floor can be broken, void under it)
34. [ ] Right-click a Gate with 8 Starglass: your space gets bigger

# Extra tests for 0.12
35. [ ] Cauldron: right-click opens the cooking screen and it STAYS open
36. [ ] Kit: the screen looks like a backpack, with a straight grid
37. [ ] Craft Pocket Casing (4), Controls, Core
38. [ ] Build the 3x3x3 cube. Right-click the Controls: "The machine is assembled"
39. [ ] You cannot break any of the 27 blocks while it is assembled
40. [ ] Open the Controls: you see the level and 3 buttons
41. [ ] Press "Link a Gate", then right-click a Quantum Gate: "now linked"
42. [ ] Right-click the linked Gate: you arrive in the pocket. The gate there brings you back
43. [ ] Press "Upgrade" with enough Starglass: the space gets bigger
44. [ ] Press "Take the machine apart", then mine a block with a diamond pickaxe

# Extra tests for 0.13
43. [ ] Nebula burner looks like a normal Create Blaze Burner but purple (cold item and block, then lit)
44. [ ] A NORMAL Create Blaze Burner still looks normal (orange / blue), also after placing a Nebula burner next to it
45. [ ] Smithing table: Nova Template + Blaze Burner + Quantum Assembly Kit = Nebula Blaze Burner
46. [ ] Right-click the burner with a full Catalyst, a Nova Coin, a Totem: action bar shows [x] for each
47. [ ] Not-full Catalyst is refused. Empty hand shows the status. Shift + empty hand gives the items back
48. [ ] Cake of Nebulae lights the burner. With all 3 items it runs the ritual and drops a Quantum Gate item, then goes cold
49. [ ] Break the burner with items inside: they drop
50. [ ] Kit opens a plain 9 x 6 storage grid (no ritual tabs). Stacks up to 256 still work
51. [ ] Pocket Controls screen: two upgrade cards with pips, locked Kinetic card, Link and Take apart buttons
52. [ ] Upgrade Pocket space and Guests: Starglass is taken, pips fill, button greys out at the maximum
53. [ ] Guests: with level 0, a second guest cannot enter while one guest is inside (message). Level 1 lets two in

# Extra tests for 0.14
54. [ ] Build jar has NO assets/create folder and NO data/create folder (open the .jar as a zip)
55. [ ] Normal Create burner + normal Blaze Cake = BLUE again, with the mod installed
56. [ ] Controls screen shows 4 cards (Pocket space, Guests, Rest, Pocket Vault) and fits your GUI scale
57. [ ] Rest: upgrade, stand in your space, you get Regeneration; level 3 keeps hunger full
58. [ ] Pocket Vault: locked at first (Open greyed out). Upgrade once, Open shows 9 slots, items stay after reopening and after a restart

# Extra tests for 0.15
59. [ ] Controls screen: light panel, dark board, 5 colored groups, nodes with item icons (nothing overlaps at your GUI scale)
60. [ ] Hover a node: tooltip with name + level, what it does, and Unlocked / Click to upgrade / Needs X Starglass / Locked
61. [ ] Click the gold node: Starglass is taken, the node lights up, the next one gets the gold border
62. [ ] Vault group: Open is greyed out until Vault level 1
63. [ ] Multiblock: casing, core and controls look the same idle and active (glowing when assembled), blocks line up nicely

# Extra tests for 0.16
64. [ ] World / server still starts with the mod (worldgen files are valid)
65. [ ] /place feature quantum_assembly:starglass_ore_end puts a vein of Starglass ore in end stone
66. [ ] Starglass ore does not drop with a diamond pickaxe, drops with netherite
67. [ ] /place structure quantum_assembly:star_shrine builds the shrine with a chest and 4 ore blocks
68. [ ] The chest holds loot (Starglass, pearls, ...)

# Extra tests for 0.17
69. [ ] World still starts (worldgen files valid). In the jar there is NO star_shrine file
70. [ ] /place structure quantum_assembly:quantum_lab builds the lab under you, with a crater, a ladder shaft and 4 rooms
71. [ ] Library / lab / workshop / chamber look right; the villager stands in the workshop; glow squid swim in the tank
72. [ ] Chests hold loot (Starglass, glow ink, amethyst, pearls, XP bottles, books...)
73. [ ] Star Mite: spawn egg or spawner; purple, glowing eyes/body in the dark; biting makes you glow; drops glow ink / Starglass
74. [ ] Quantum Engineer: purple outfit; trade list per level (see CHANGES-0.17.0.md)
75. [ ] A normal villager next to a placed Quantum Assembly Kit can take the Quantum Engineer job

# Extra tests for 0.18
76. [ ] The burner cage is see-through; the head bobs and turns toward you; lit = rods float
77. [ ] /place structure quantum_assembly:quantum_lab : crater with a meteor, a 1x1 ladder hole going far down, a hub, random halls and rooms (try it a few times for different layouts)
78. [ ] Library has a balcony + ladder; lab has glow squid; workshop has the Engineer; chamber has 4 cages
79. [ ] Star Crawler (spawn egg): big beetle, glowing, bite makes you glow; drops glow ink
80. [ ] Villager trades are stingy (see CHANGES-0.18.0.md)
81. [ ] Iron block -> Heavy Duty Plate (Create: press, press, lava, press, press)
82. [ ] Starglass + sandpaper = Polished Starglass; Quantum Processor crafts in the ETE / SSS / PPP pattern
83. [ ] Kit recipe has plates in all 4 corners
84. [ ] Cake: batter right-click on the Master Chef's Cauldron over a campfire = cake after 30 s; a normal cooking pot does NOT make it
85. [ ] Quantum Gate: link it, walk through; you return in front of it; textures look right (coin, template, starglass, ore)
