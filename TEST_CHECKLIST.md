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
