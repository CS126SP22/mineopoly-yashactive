package mineopoly_three.strategy;
import mineopoly_three.game.Economy;
import mineopoly_three.action.TurnAction;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.tiles.TileType;
import mineopoly_three.util.DistanceUtil;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This is my implementation of the player strategy
 */
public class MyPlayerStrategy implements MinePlayerStrategy {
    /** The random variable declared to generate radnom numbers if required */
    private Random random;
    /** The size of the game board */
    private int boardSize;
    /** The maximum number of resources that can be picked up by a player */
    private int maxInventorySize;
    /** The maximum charge the player starts the game with */
    private int maxCharge;
    /** The score to attain to win the game */
    private int winningScore;
    /** The current state of the game */
    private PlayerBoardView currentGameState;
    /** Represents if it is a red player or blue player */
    private boolean isRedPlayer;
    /** Represents if it the player has enough charge */
    private boolean isCharged;
    /** Maintains a List of resources picked up by the player */
    private List<InventoryItem> itemInventory =  new ArrayList<>();
    /** The threshold for the player to be in low charge */
    private static final double LOW_CHARGE = 0.15;
    /** The List of all the points which have gems */
    private List<Point> locationGems = new ArrayList<>();
    /** The List of all the points which are market tiles */
    private List<Point> locationMarket = new ArrayList<>();
    /** The List of all the points which are recharge tiles */
    private List<Point> locationRecharge = new ArrayList<>();
    /** A map which contains the details of where all the things are on the game board */
    private Map<String, List<Point>> boardResourceMap = new HashMap<>();
    /** Determines which direction should the player move in */
    private TurnAction directionToGo;
    /** Counts the number of turns spent on mining diamonds */
    private int diamondCounter = 0;
    /** Counts the number of turns spent on mining emerald */
    private int emeraldCounter = 0;
    /** Counts the number of turns spent on mining Rubys */
    private int rubyCounter = 0;


    @Override
    public void initialize(int boardSize, int maxInventorySize, int maxCharge, int winningScore,
                           PlayerBoardView startingBoard, Point startTileLocation, boolean isRedPlayer, Random random) {
        this.boardSize = boardSize;
        this.maxInventorySize = maxInventorySize;
        this.maxCharge = maxCharge;
        this.winningScore = winningScore;
        this.isRedPlayer = isRedPlayer;
        this.random = random;
        currentGameState = startingBoard;

        getItemLocations(TileType.RESOURCE_DIAMOND, locationGems);
        getItemLocations(TileType.RESOURCE_EMERALD,locationGems);
        getItemLocations(TileType.RESOURCE_RUBY,locationGems);
        boardResourceMap.put("Gems", locationGems);
        getItemLocations(TileType.RECHARGE, locationRecharge);
        boardResourceMap.put("Recharge", locationRecharge);

        if (isRedPlayer == true) {
            getItemLocations(TileType.RED_MARKET, locationMarket);
        } else {
            getItemLocations(TileType.BLUE_MARKET, locationMarket);
        }
        boardResourceMap.put("Market", locationMarket);
    }

    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, Economy economy, int currentCharge, boolean isRedTurn) {
        currentGameState  = boardView;
        if (currentCharge < maxCharge * LOW_CHARGE) {
            isCharged = false;
        } else if (currentCharge == maxCharge){
            isCharged = true;
        }

        if (isCharged == false) {
            if (currentGameState.getTileTypeAtLocation(currentGameState.getYourLocation()) == TileType.RECHARGE) {
                return null;
            }
            Point closestRechargeTile = closestItemPoint(currentGameState.getYourLocation(), "Recharge");
            decideDirectionToMove(closestRechargeTile, currentGameState.getYourLocation());
            return directionToGo;
        }
        else if (itemInventory.size() == maxInventorySize) {
            Point closestMarketTile = closestItemPoint(currentGameState.getYourLocation(), "Market");
            decideDirectionToMove(closestMarketTile, currentGameState.getYourLocation());
            return directionToGo;
        }
        else if (itemInventory.size() < maxInventorySize) {
            Point closestGemTile = closestItemPoint(currentGameState.getYourLocation(), "Gems");
            decideDirectionToMove(closestGemTile, currentGameState.getYourLocation());
            if (directionToGo == null) {
                if (currentGameState.getTileTypeAtLocation(currentGameState.getYourLocation()) == TileType.EMPTY) {
                    directionToGo = TurnAction.PICK_UP_RESOURCE;
                    boardResourceMap.get("Gems").remove(closestGemTile);
                    diamondCounter = 0;
                    emeraldCounter = 0;
                    rubyCounter = 0;
                    return directionToGo;
                } else {
                    toMine(currentGameState);
                    return directionToGo;
                }
            } else {
                return directionToGo;
            }
        }
        return directionToGo;
    }

    @Override
    public void onReceiveItem(InventoryItem itemReceived) {
        itemInventory.add(itemReceived);
    }

    @Override
    public void onSoldInventory(int totalSellPrice) {
        itemInventory.clear();
    }

    @Override
    public String getName() {
        return "YashPlayerStrategy";
    }

    @Override
    public void endRound(int totalRedPoints, int totalBluePoints) {
        boardResourceMap = new HashMap<>();
        itemInventory = new ArrayList<>();
        locationGems = new ArrayList<>();
        locationMarket = new ArrayList<>();
        locationRecharge = new ArrayList<>();
    }

    /**
     * This function adds points whihc contain a particular tile type to a list
     * @param typeOfTile represents the type of the Tile being added to the Map
     * @param resourceList is the list of points which should contain the resource
     */
    public void getItemLocations(TileType typeOfTile, List resourceList) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (currentGameState.getTileTypeAtLocation(i,j) == typeOfTile) {
                    Point pointToAdd = new Point(i,j);
                    resourceList.add(pointToAdd);
                }
            }
        }
    }

    /**
     * This function gives the closest tile type passed to the player's current location
     * @param currentLocation is the Point at which the player is currently on the board
     * @param typeOfTile represents the type of the Tile
     * @return It returns the point of the closest tile type passed to the player's current location
     */
    public Point closestItemPoint(Point currentLocation, String typeOfTile) {
       Point currentClosestPoint = boardResourceMap.get(typeOfTile).get(0);
       int closestTileDist = DistanceUtil.getManhattanDistance(currentLocation, currentClosestPoint);
       List<Point> tileTypeLocations = boardResourceMap.get(typeOfTile);
       int distancePoints;
       for (Point currentPoint : tileTypeLocations) {
           distancePoints = DistanceUtil.getManhattanDistance(currentLocation, currentPoint);;
           if (distancePoints < closestTileDist){
               currentClosestPoint = currentPoint;
               closestTileDist = DistanceUtil.getManhattanDistance(currentLocation, currentPoint);
           }
       }
       return currentClosestPoint;
    }

    /**
     * This function decides the turn action dependent on where the player is supposed to go
     * @param pointToMove is the target location which the player must move to
     * @param currentLocation is the current location of the player.
     */
    public void decideDirectionToMove(Point pointToMove, Point currentLocation) {
        if (pointToMove.getX() < currentLocation.getX()) {
            directionToGo = TurnAction.MOVE_LEFT;
        }
        else if (pointToMove.getX() > currentLocation.getX()) {
            directionToGo = TurnAction.MOVE_RIGHT;
        }
        else if (pointToMove.getY() > currentLocation.getY()) {
            directionToGo = TurnAction.MOVE_UP;
        }
        else if (pointToMove.getY() < currentLocation.getY()) {
            directionToGo = TurnAction.MOVE_DOWN;
        } else {
            directionToGo = null;
        }
    }

    /**
     * The function determines the number of mine actions to send depending upon type of gem
     * @param currentGameState represents the current state of the player in the game.
     */
    public void toMine(PlayerBoardView currentGameState) {
        int diamondMineNumber = 3;
        int MineNumber = 1;
        int rubyMineNumber = 1;
        if (currentGameState.getTileTypeAtLocation(currentGameState.getYourLocation()) == TileType.RESOURCE_DIAMOND) {
            if (diamondCounter < 3) {
                diamondCounter++;
                directionToGo = TurnAction.MINE;
                return;
            }
        } else if (currentGameState.getTileTypeAtLocation(currentGameState.getYourLocation()) == TileType.RESOURCE_EMERALD) {
            if (emeraldCounter < 2) {
                emeraldCounter++;
                directionToGo = TurnAction.MINE;
                return;
            }
        } else if (currentGameState.getTileTypeAtLocation(currentGameState.getYourLocation()) == TileType.RESOURCE_RUBY) {
            if (rubyCounter < 1) {
                rubyCounter++;
                directionToGo = TurnAction.MINE;
                return;
            }
        }
    }

    /** getter to assist with testing */
    public TurnAction getDirectionToGo() {
        return directionToGo;
    }

    public void setBoardResourceMap(String l, List<Point> k) {
        boardResourceMap.put(l,k);
    }

}



