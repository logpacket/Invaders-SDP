package entity.ship;

import engine.Renderer;
import entity.Ship;
import entity.ShipMultipliers;

/**
 * Stronger ship with slower speed.
 * It moves slower than the default ship, but its bullets are stronger.
 */
public class GalacticGuardian extends Ship {
    public GalacticGuardian(final int positionX, final int positionY) {
        super(positionX, positionY,
                "Galactic Guardian", new ShipMultipliers(0.8f, 1.5f, 1.2f),
                Renderer.SpriteType.SHIP_3, ShipType.GALACTIC_GUARDIAN);
    }
}
