import java.util.Stack;
import java.util.LinkedList;

/** A player.  There can be up to four players.
 * Each player has 15 roads, 5 settlements and
 * 4 cities.
 *
 * If a player were to upgrade a settlement to a city,
 * then the player is able to take back the settlement
 * to use again.
 *
 * A player wins the game by having 10 victory points.
 *
 * Unlike the other classes, the player cannot place
 * buildings and roads onto a hex unless he or she expends resource
 * cards. In this sense, players cannot place onto the board under
 * certain conditions.
 *
 * @author John Angeles
 */
public class Player {

    /** Instantiates a new player with a unique color.
     *
     * @param color A unique color either black, white,
     *              orange or red.
     * @param name The name of the player.
     */
    Player(Color color, String name) {
        _color = color;
        _name = name;

        for (int i = 0; i < 15; i += 1) {
            _roads.push(new Road(_color, this));
        }

        for (int i = 0; i < 5; i += 1) {
            _settlements.push(new Settlement(_color, this));
        }

        for (int i = 0; i < 4; i += 1) {
            _cities.push(new City(_color, this));
        }

    }

    /** Gives me a resource card. */
    void giveResource(ResourceCard card) {
        if (card.resource() == Resource.wood()) {
            _wood.push(card);
        } else if (card.resource() == Resource.brick()) {
            _bricks.push(card);
        } else if (card.resource() == Resource.wheat()) {
            _wheat.push(card);
        } else if (card.resource() == Resource.ore()) {
            _ore.push(card);
        } else {
            _sheep.push(card);
        }
    }

    /** Returns a message saying how many of each resource I have. */
    String flag() {
        return "Resources owned by " + _name + ":\n"
                + "Wood: " + _wood.size() + "\n"
                + "Bricks: " + _bricks.size() + "\n"
                + "Wheat: " + _wheat.size() + "\n"
                + "Ore: " + _ore.size() + "\n"
                + "Sheep: " + _sheep.size() + "\n";
    }

    /** Checks whether or not a placement of one of my roads
     * on HEX in POSN is valid. This does NOT check whether
     * I have the correct amount of resources to do so; placeRoad
     * does that for me.
     * We can place a road if there is at least one of my roads next to
     * the place I intend to place a new road, if there isn't a settlement that
     * belongs to another player that cuts off my adjacent road, and if there
     * isn't already a road on hex HEX at posn POSN.
     *
     * @param hex The hex on the board where I intend to place
     *            my road.
     * @param side The position of the hex where I intend to place
     *             my road.
     * @return True if I am able to place one of my roads on hex HEX
     * in position POSN, false otherwise.
     */
    boolean isValidRoad(int hex, int side) {
        int adjSide1 = side - 1, adjSide2 = (side + 1) % 6;

        if (adjSide1 < 0) {
            adjSide1 += 6;
        }

        Hex currHex = Board.get(hex);
        Building leftB = currHex.building(side),
                rightB = currHex.building((side + 1) % 6);
        boolean hasAdjLRoad = (currHex.hasRoad(adjSide1) &&
                (leftB == null || leftB.player() == this) &&
                (currHex.getRoad(adjSide1).player() == this));
        boolean hasAdjRRoad = (currHex.hasRoad(adjSide2) &&
                (rightB == null || rightB.player() == this) &&
                (currHex.getRoad(adjSide2).player() == this));
        boolean notBlocked = !currHex.hasRoad(side);
        return (hasAdjLRoad || hasAdjRRoad) && notBlocked;
    }


    /** Places a road at the cost of expending one
     * wood and one brick. Before doing so, this method
     * checks whether or not my placement is valid, in addition to checking
     * whether or not I have the correct amount of resources.
     *
     * @param hex The hex on the board where I place my road.
     * @param side The side of the hex where I place my road.
     * @return A string message saying whether or not I have
     * successfully placed my road or not.
     */
    String placeRoad(int hex, int side) {
        if (!isValidRoad(hex, side)) {
            return "Nope, can't place it there.";
        } else if (_wood.isEmpty() || _bricks.isEmpty()) {
            return "Gonna need more trees and bricks.";
        } else if (_roads.isEmpty()) {
            return "Out of roads!";
        } else {
            _wood.pop();
            _bricks.pop();
            Road newRoad = _roads.pop();
            _placedRoads.push(newRoad);
            Board.placeRoad(newRoad, hex, side);
            return _name + " put down a road!";
        }
    }

    /** Returns true if we can place a settlement on the specified
     * HEX on the specified SIDE.
     *
     * @param hex Hex where we intend to place our settlement.
     * @param side Side where we intend to place our settlement.
     * @return True if we can place a settlement on HEX in SIDE or
     * false otherwise.
     */
    boolean isValidSettlement(int hex, int side) {
        //FIXME
        return false;
    }

    /** Places a settlement at the cost of expending
     * one wood, one brick, one wheat, and one sheep.
     * Before doing so, this method checks whether I have
     * at least one settlement, and whether or not my placement
     * is valid.
     * Returns a string message saying what happened.
     */
    String placeSettlement(int hex, int posn) {
        if (_settlements.isEmpty()) {
            return "No more settlements!";
        }
        else if (!_wood.isEmpty() && !_bricks.isEmpty()
                && !_wheat.isEmpty() && !_sheep.isEmpty()) {
            _wood.pop();
            _bricks.pop();
            _wheat.pop();
            _sheep.pop();
            Settlement toPlace = _settlements.pop();
            Board.placeSettlement(toPlace, hex, posn);
            return _name + " built a settlement!";
        } else {
            return "Shucks, not enough resources!";
        }
    }

    /** Current victory points that I have. */
    private int _victoryPoints;

    /** Roads that I haven't placed yet. */
    private Stack<Road> _roads = new Stack<>();

    /** Settlements that I haven't placed yet. */
    private Stack<Settlement> _settlements = new Stack<>();

    /** Cities that I haven't placed yet. */
    private Stack<City> _cities = new Stack<>();

    /** Roads that I have placed. */
    private LinkedList<Road> _placedRoads = new LinkedList<>();

    /** Settlements that I have placed. */
    private LinkedList<Settlement> _placedSettlements
            = new LinkedList<>();

    /** Cities that I have placed. */
    private LinkedList<City> _placedCities = new LinkedList<>();

    /** Wood that I own. */
    private Stack<Card> _wood = new Stack<>();

    /** Bricks that I own. */
    private Stack<Card> _bricks = new Stack<>();

    /** Wheat that I own. */
    private Stack<Card> _wheat = new Stack<>();

    /** Ore that I own. */
    private Stack<Card> _ore = new Stack<>();

    /** Sheep that I own. */
    private Stack<Card> _sheep = new Stack<>();

    /** My color. */
    private Color _color;

    /** My name. */
    private String _name;

}
