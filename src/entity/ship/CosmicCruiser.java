package entity.ship;

import engine.Renderer;
import entity.Ship;
import entity.ShipMultipliers;

/**
 * Slow ship with very strong bullets.
 * It moves slower than the default ship, but its bullets are a lot stronger.
 */
public class CosmicCruiser extends Ship {
    public CosmicCruiser(final int positionX, final int positionY) {
        super(positionX, positionY,
                "Cosmic Cruiser", new ShipMultipliers(0.8f, 2f, 1.6f),
                Renderer.SpriteType.SHIP_4, ShipType.COSMIC_CRUISER);
    }
}
