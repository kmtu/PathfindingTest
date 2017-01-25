import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * @author TuTu
 * Apr 10, 2008
 */
public class World {
	public boolean alive = true;
	private long previousTime = 0;
	private Players players;
	private Board board;
//	public static EnumMap<World.Terrain, Double> movingSpeedFactorMap_BASIC_BATTLE_UNIT;
	public ArrayList<EnumMap<World.Terrain, Double>> movingSpeedFactorMapList = new ArrayList<EnumMap<World.Terrain, Double>>(Unit.Type.NUM_UNIT_TYPES);

	public static enum Terrain {
		FREE_GOUND(null),
		BLOCK_GROUND(Color.BLACK);
		
		public static final int NUM_TERRAINS = 2;
		public Color color;
		Terrain(Color color) {
			this.color = color;
		}
	}

	public static enum Layer {
		BACKGROUND_LAYER,
		GROUND_LOWER_UNIT_LAYER,
		GROUND_UPPER_UNIT_LAYER;
		
		public static final int NUM_LAYERS = 3;
	}
	
	public static enum PlayerInfo {
		MOTHER (Color.GRAY),
		HUMAN (Color.RED),
		AI1 (Color.BLUE),
		AI2 (Color.GREEN.darker()),
		AI3 (Color.YELLOW);
		
		public Color color;
		public static final int NUM_AI_PLAYERS = 3;
		PlayerInfo (Color color) {
			this.color = color;
		}
	}
	
	public static final int DEFAULT_BOARD_WIDTH = 50;
	public static final int DEFAULT_BOARD_HEIGHT = 50;
	public static final Dimension DEFAULT_BOARD_DIM
        = new Dimension(DEFAULT_BOARD_WIDTH, DEFAULT_BOARD_HEIGHT);
//	public static final int NUM_UNIT_LAYERS = 3;
//	public static final int GROUND_UPPER_UNIT_LAYER = 2;
//	public static final Color[] PLAYER_COLOR = {Color.GRAY, Color.RED, Color.BLUE, Color.GREEN.darker(), Color.YELLOW};
	private static final long MIN_DELTA_MS = 20;
	
	public World() {
		super();
		players = new Players(this);
//		board = new Board(new Dimension(DEFAULT_BOARD_WIDTH, DEFAULT_BOARD_HEIGHT));
		File boardInfoFile = new File("data"+File.separator+"board.info");
		try {
			board = new Board(boardInfoFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		placeInitialUnits();
	}

	public void render(Graphics2D g2) {
		board.draw(g2);
	}
	
	public void evolve() {
		double timeStep = getTimeStep();
		players.aiGiveOrder();
		players.excuteOrder(timeStep);
	}
	
	private double getTimeStep() {
		double delta;
		long delta_ms;
		long currentTime;
		
		if (previousTime == 0) {
			previousTime = System.currentTimeMillis();
		}
		currentTime = System.currentTimeMillis();    
		delta_ms = currentTime - previousTime;
        if (delta_ms < MIN_DELTA_MS) {
			// sleep more, make the cpu cooler
	        try {
	            Thread.sleep (MIN_DELTA_MS);
	        }
	        catch (InterruptedException exception) {
	            System.out.println("InterruptedException");
	            Thread.currentThread().interrupt();
	        }
        }
        currentTime = System.currentTimeMillis();    
        delta = (currentTime - previousTime)/1000.;
        previousTime = currentTime;
        return delta;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public Players getPlayers() {
		return players;
	}
	
	private void placeInitialUnits() {
		players.getHumanPlayer().createUnit(Unit.Type.BASIC_BATTLE_UNIT, new Point(3,3));
		players.getHumanPlayer().createUnit(Unit.Type.BASIC_BATTLE_UNIT, new Point(40,45));
		players.getHumanPlayer().createUnit(Unit.Type.BASIC_BATTLE_UNIT, new Point(3,45));
		players.getHumanPlayer().createUnit(Unit.Type.BASIC_BATTLE_UNIT, new Point(30,30));
		players.getHumanPlayer().createUnit(Unit.Type.BASIC_BATTLE_UNIT, new Point(10,30));
		players.getHumanPlayer().createUnit(Unit.Type.BASIC_BATTLE_UNIT, new Point(20,30));
		players.getHumanPlayer().createUnit(Unit.Type.BASIC_BATTLE_UNIT, new Point(40,30));
	}
}
