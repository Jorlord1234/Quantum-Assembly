# What changed in 0.12.1

## Fixed (Nebula Blaze Burner)
- Cold burner texture used a different layout than the model, so the head was scrambled. It now uses the same layout as the lit texture (dark cosmic purple, dim eyes).
- The burner ITEM always used the lit (purple) model. It now shows the cold model, like the placed block.
- Cold head is now the same size as the lit head.
- The lit model no longer borrows Create's rod model files (blaze_rods_small.obj and blaze_rods_large.obj). The rods are now plain boxes in our own model, so nothing is shared with Create's burner.

## Not changed
- This mod has no code, tag or model that touches Create's own Blaze Burner.
