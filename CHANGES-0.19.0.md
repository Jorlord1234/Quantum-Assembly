# 0.19.0

How to use this zip: copy `src/`, `build.gradle` and `gradle.properties` over your repository (same folders), commit, and let your
GitHub build (or `./gradlew build`) make the jar. In `src/main/templates/META-INF/neoforge.mods.toml` change Create from
`type="optional"` to `type="required"` (the Kinetic Port needs Create). Delete the files in DELETE_THESE.txt.
Everything below that says "needs the new build" does not exist in a jar made from the old code.

## Pocket power (Create SU / RPM) - needs the new build
- New block **Pocket Kinetic Port**: it is a casing of the multiblock (counts as one of the 25) with a shaft face. Create rotation goes in there.
- New block **Pocket Power Output**: place it inside your pocket space. It turns at the same speed as your Kinetic Port.
- Power upgrade (Pocket Controls, Power card, 3 levels for 16 / 32 / 64 Starglass): sets how much stress capacity the output offers
  (16 / 64 / 256 SU per RPM). The port loads the same amount onto the network it is connected to, so nothing is free.
- Only the fastest Kinetic Port of the multiblock counts (one is enough). The multiblock must be in a loaded chunk.
- Recipes: Kinetic Port = Pocket Casing + Shaft + Electron Tube. Pocket Power Output = Pocket Casing + Shaft + Polished Starglass.
- The two blocks have no spinning animation yet (they work, goggles show the speed). Say so if you want the shaft to spin.

## Quantum Assembly Kit - needs the new build
- The lid swings open like a Create toolbox while somebody has the Kit open (with the toolbox-style open/close sound).
- 4 upgrade slots (column on the left of the screen) that take Sophisticated Backpacks upgrades. Stack upgrades work
  (Tier 1 = x2, Tier 2 = x4, Tier 3 = x8, Tier 4 = x16, Omega = x32 per slot, several multiply together, max 65536 per slot).
  Other upgrades can be stored but do nothing yet.

## Quantum Gun - needs the new build
- Link it (Pocket Controls > "Link a Quantum Gun", then right-click with the gun). Right-click a block: opens a gate, right-click again: closes it.
  Walk through the gate to enter the pocket. The gate itself does nothing when right-clicked and drops nothing.

## Villager - needs the new build (the jar already has the fix patched in)
- The Quantum Engineer never sells a Totem of Undying.

## Resources (also in the patched jar)
- Recipes: Pocket Casing, Pocket Controls, Pocket Core (new recipes). No recipe for the Master Chef's Cauldron (it only comes from the advancement).
- Assembled casings use a seamless texture, so neighbouring casings connect into one surface.
- New Polished Starglass (same size as Create's polished rose quartz), Starglass Ore, Nova Template, Quantum Processor.
- The Quantum Gun has a 3D model that points forward. The Quantum Gate model is split into a solid frame and a translucent portal.
