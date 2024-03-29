package mineopoly_three.strategy;

import mineopoly_three.game.Economy;
import mineopoly_three.action.TurnAction;
import mineopoly_three.item.InventoryItem;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class implements a strategy that selects a move completely at random and performs it.
 * It can never select TurnAction.PICK_UP as a move, so it will run around and mine randomly but is
 *  not capable of scoring points.
 */
public class RandomStrategy implements MinePlayerStrategy {
    private Random random;
    private List<TurnAction> allPossibleActions;
    private int pickUpActionIndex;
    private Map<Point, List<InventoryItem>> itemsOnBoard;

    @Override
    public void initialize(int boardSize, int maxInventorySize, int maxCharge, int winningScore,
                           PlayerBoardView startingBoard, Point startTileLocation, boolean isRedPlayer, Random random) {
        this.random = random;
        this.allPossibleActions = new ArrayList<>(EnumSet.allOf(TurnAction.class));
        allPossibleActions.add(null); // Doing nothing at all is a possible action
        this.pickUpActionIndex = allPossibleActions.indexOf(TurnAction.PICK_UP_RESOURCE);
        itemsOnBoard = startingBoard.getItemsOnGround();

    }

    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, Economy economy, int currentCharge, boolean isRedTurn) {
        // Neat trick to select a uniform random element (without selecting TurnAction.PICK_UP) in O(1) time
        int randomActionIndex = random.nextInt(allPossibleActions.size() - 1);
        if (randomActionIndex >= pickUpActionIndex) {
            randomActionIndex++;
        }
        return allPossibleActions.get(randomActionIndex);
    }

    @Override
    public void onReceiveItem(InventoryItem itemReceived) {

    }

    @Override
    public void onSoldInventory(int totalSellPrice) {

    }

    @Override
    public String getName() {
        return "RandomStrategy";
    }

    @Override
    public void endRound(int totalRedPoints, int totalBluePoints) {

    }
}
