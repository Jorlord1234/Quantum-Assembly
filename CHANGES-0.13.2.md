# What changed in 0.13.2

- The build now leaves assets/create/** and data/create/** out of the jar, so files from OLD versions of this mod
  that are still lying in the repository can no longer change how Create's own Blaze Burner looks or works.
- Includes everything from 0.13.1 (Kit big stacks, see-through burner, no rods when cold).
- If a normal Create burner still looks wrong after this, open the built .jar (it is a zip file) and check that it has
  NO folder  assets/create  and NO folder  data/create.
