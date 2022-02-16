import mineopoly_three.strategy.MyPlayerStrategy;
import mineopoly_three.item.*;
import mineopoly_three.action.TurnAction;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.tiles.TileType;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyPlayerStrategyTests {
    private MyPlayerStrategy strategy;
    private List<InventoryItem> itemInventory = new ArrayList<>();
    private Map<String, List<Point>> boardResourceMap = new HashMap<>();
    private List<Point> testList = new ArrayList<>();
    private Point one;
    private Point two;
    private Point three;
    private Point four;
    private Point five;
    private TurnAction action;



    @Before
    public void setUp() {
        strategy = new MyPlayerStrategy();
        one = new Point(10,10);
        two = new Point(11,10);
        three = new Point(9,10);
        four = new Point(1,20);
        five = new Point(30,30);
        testList.add(one);
        testList.add(two);
        testList.add(three);
        testList.add(four);
        testList.add(five);
        strategy.setBoardResourceMap("testRes", testList);

    }

    @Test(expected = NullPointerException.class)
    public void testNullclosestItemPoint() {
        Point newPoint = strategy.closestItemPoint(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testNulldecideDirectionToMove() {
        strategy.decideDirectionToMove(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testNulltoMine() {
        strategy.toMine(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullgetItemLocations() {
        strategy.getItemLocations(null, null);
    }

    @Test
    public void testclosestItemPointY() {
        Point currPoint = new Point(1,19);
        Point toTest = strategy.closestItemPoint(currPoint, "testRes");
        assertEquals(toTest, four);

    }
    @Test
    public void testclosestItemPointX() {
        Point currPoint = new Point(12,10);
        Point toTest = strategy.closestItemPoint(currPoint, "testRes");
        assertEquals(toTest, two);

    }

    @Test
    public void decideDirectionToMoveLeft() {
        strategy.decideDirectionToMove(one,two);
        assertEquals(strategy.getDirectionToGo(),TurnAction.MOVE_LEFT);

    }

    @Test
    public void decideDirectionToMoveRight() {
        strategy.decideDirectionToMove(one,three);
        assertEquals(strategy.getDirectionToGo(),TurnAction.MOVE_RIGHT);

    }

    @Test
    public void decideDirectionToMoveDown() {
        three = new Point(10,11);
        strategy.decideDirectionToMove(one,three);
        assertEquals(strategy.getDirectionToGo(),TurnAction.MOVE_DOWN);

    }

    @Test
    public void decideDirectionToMoveUp() {
        three = new Point(10,9);
        strategy.decideDirectionToMove(one,three);
        assertEquals(strategy.getDirectionToGo(),TurnAction.MOVE_UP);

    }



}


