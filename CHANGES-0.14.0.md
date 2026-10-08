# What changed in 0.14.0

## The purple Create burner (found by opening the built jar)
- The jar still contained OLD files from earlier versions of this mod in Create's own folders:
  assets/create/textures/block/blaze_super.png, blaze_super_active.png, blaze_burner_flame_superheated_scroll.png
  These recolored Create's superheated (blue) burner purple. They were left over in the GitHub repository.
- The build now leaves out everything in assets/create, data/create and the assets folders of other mods, so old
  leftovers can never end up in the jar again.
- ALSO delete these folders in your GitHub repo if they exist: src/main/resources/assets/create and
  src/main/resources/data/create (and the old textures nebula_blaze.png and nebula_blaze_dark.png).

## New Pocket Controls upgrades (cards on the Controls screen)
- REST (3 levels: 8 / 16 / 32 Starglass): you heal while inside your own space.
  Level 1 = Regeneration I, level 2 = Regeneration II, level 3 = Regeneration II and you never get hungry.
- POCKET VAULT (4 levels: 8 / 16 / 32 / 64 Starglass): chest storage that belongs to your space.
  9, 27, 36 and 54 slots. Open it with the Open button on its card. Contents are saved with your space.
- Compact screen so all cards fit. Starglass you carry is shown in the title bar.
- Still not done: Kinetic link (needs Create as a build dependency).
