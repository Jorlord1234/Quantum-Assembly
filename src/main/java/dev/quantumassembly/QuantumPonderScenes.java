package dev.quantumassembly;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.Direction;

/**
 * The Ponder scenes. Every .text(...) becomes the lang key quantum_assembly.ponder.<scene id>.text_<number>, counted in
 * the order they appear here, and the title becomes ...<scene id>.header (the English texts are in the lang file).
 *
 * The structures: a 5 x 5 floor (layer 0) and the 3 x 3 x 3 cube on layers 1 to 3 with the Core at (2,2,2), the
 * Controls at (2,2,1) facing north and (in the power scene) the Kinetic Port at (1,2,2) facing west. Ponder shows the
 * north and west faces to the viewer by default.
 */
public final class QuantumPonderScenes {
    private QuantumPonderScenes() {
    }

    public static void multiblock(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("pocket_multiblock", "Building the Pocket multiblock");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(0.8F);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.idle(10);

        // 1. bottom layer
        scene.world().showSection(util.select().layer(1), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .text("The pocket multiblock is a cube of 3 x 3 x 3 blocks. Start with the bottom layer of 9 Pocket Casings.")
                .pointAt(util.vector().topOf(2, 1, 2))
                .placeNearTarget();
        scene.idle(90);

        // 2. the core
        scene.world().showSection(util.select().position(2, 2, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Place the Pocket Core in the middle of the cube.")
                .pointAt(util.vector().topOf(2, 2, 2))
                .placeNearTarget();
        scene.idle(80);

        // 3. the ring around the core (everything on layer 2 except the core and the Controls)
        Selection ring = util.select().layer(2)
                .substract(util.select().position(2, 2, 2))
                .substract(util.select().position(2, 2, 1));
        scene.world().showSection(ring, Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Surround the Core with Pocket Casings on the middle layer.")
                .pointAt(util.vector().topOf(1, 2, 2))
                .placeNearTarget();
        scene.idle(80);

        // 4. the controls
        scene.world().showSection(util.select().position(2, 2, 1), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(90)
                .text("Leave one spot in the middle of a side for the Pocket Controls. Its front faces outward.")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 1), Direction.NORTH))
                .colored(PonderPalette.BLUE)
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(100);

        // 5. the top layer
        scene.world().showSection(util.select().layer(3), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(90)
                .text("Finish with the top layer of Pocket Casings. In total: 25 Casings, the Core and the Controls.")
                .pointAt(util.vector().topOf(2, 3, 2))
                .placeNearTarget();
        scene.idle(100);

        // 6. assemble
        Selection cube = util.select().fromTo(1, 1, 1, 3, 3, 3);
        scene.overlay().showText(100)
                .text("Right-click the Pocket Controls to assemble the multiblock. Once it lights up, the parts are locked in place.")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 1), Direction.NORTH))
                .colored(PonderPalette.GREEN)
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(30);
        scene.world().modifyBlocks(cube, state -> state.hasProperty(Multiblock.ASSEMBLED) ? state.setValue(Multiblock.ASSEMBLED, true) : state, false);
        scene.overlay().showOutline(PonderPalette.GREEN, new Object(), cube, 60);
        scene.idle(80);

        // 7. the kinetic port
        scene.overlay().showText(80)
                .text("A Pocket Kinetic Port counts as one of the casings, so you can swap one casing for it.")
                .pointAt(util.vector().topOf(1, 2, 2))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void power(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("pocket_power", "Rotation in your pocket space");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(0.8F);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.idle(10);
        scene.world().showSection(util.select().layersFrom(1), Direction.DOWN);
        scene.idle(15);

        scene.overlay().showText(90)
                .text("The Pocket Kinetic Port is a casing of the multiblock with a shaft side. Connect your Create machines to that side.")
                .pointAt(util.vector().blockSurface(util.grid().at(1, 2, 2), Direction.WEST))
                .colored(PonderPalette.BLUE)
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(100);

        scene.overlay().showText(100)
                .text("The rotation is sent into your pocket space. You need the Power upgrade in the Pocket Controls for this.")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 1), Direction.NORTH))
                .placeNearTarget();
        scene.idle(110);

        scene.overlay().showText(110)
                .text("Inside your pocket space, place a Pocket Power Output. It turns at the speed of the port and offers the stress capacity of your Power upgrade.")
                .independent(40);
        scene.idle(120);

        scene.overlay().showText(100)
                .text("The port puts the same load on your network, so the power is never free. One port is enough.")
                .pointAt(util.vector().topOf(1, 2, 2))
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(110);
        scene.markAsFinished();
    }
}
