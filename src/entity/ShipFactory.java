package entity;

import entity.ship.CosmicCruiser;
import entity.ship.GalacticGuardian;
import entity.ship.StarDefender;
import entity.ship.VoidReaper;

/**
 * Factory for creating ships.
 * It is used to create ships of different types.
 */
public class ShipFactory {
    private ShipFactory() { }

    /**
     * Creates a ship of the specified type.
     * @param type The type of ship to create.
     * @param positionX The initial position of the ship in the X axis.
     * @param positionY The initial position of the ship in the Y axis.
     * @return A new ship of the specified type.
     */
    public static Ship create(Ship.ShipType type, final int positionX, final int positionY) {
        return switch (type) {
            case STAR_DEFENDER -> new StarDefender(positionX, positionY);
            case VOID_REAPER -> new VoidReaper(positionX, positionY);
            case GALACTIC_GUARDIAN -> new GalacticGuardian(positionX, positionY);
            case COSMIC_CRUISER -> new CosmicCruiser(positionX, positionY);
        };
    }
}
