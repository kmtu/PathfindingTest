//import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Board {
	private Block[][] blocks;
	private Dimension dimension;
	private Pathfinder pathfinder;
	public static final NullBlock NULL_BLOCK = new NullBlock();
	private static final char COMMENT_CHAR = '#';
	private static final char DIRECTIVE_CHAR = '@';
	
	private enum Directive {
		DIMENSION,
		TERRAIN;
	}
	
	public Board(Dimension dimension) {
		super();
		this.dimension = dimension;
		initializeBlocks();
		pathfinder = new Pathfinder(this);
	}
	
	public Board(File file) throws FileNotFoundException {
		super();
		FileInputStream fs;
		BufferedReader in;
		fs = new FileInputStream(file);
		in = new BufferedReader(new InputStreamReader(fs));
		String string;
		try {
			while ((string = in.readLine()) != null && string.length() > 0) {
				string = string.trim();
				int commentIndex = string.indexOf(COMMENT_CHAR);
				if (commentIndex != -1) {
					string = string.substring(0, commentIndex);
				}
				if (string.length() > 0) {
					if (string.startsWith(Character.toString(DIRECTIVE_CHAR))) {
						switch (Directive.valueOf(string.substring(1).toUpperCase())) {
							case DIMENSION:
								String st = in.readLine();							
//								int spaceIndex = st.indexOf(' ');
								String[] sts = st.split("[ \t\n\f\r]");
								this.dimension = new Dimension(Integer.parseInt(sts[0]), Integer.parseInt(sts[1]));
								blocks = new Block[dimension.width][dimension.height];
								for (int y = 0; y < dimension.height; y++) {
						            for (int x = 0; x < dimension.width; x++) {
						                blocks[y][x] = new Block(new Point(x, y));
						            }
						        }
								break;
							case TERRAIN:
								if (this.dimension == null) {
									throw new IllegalStateException("Dimension of Board must be written first before setting Terrain!");
								}
								else {
									for (int y = 0; y < this.dimension.height; y++) {
										for (int x = 0; x < this.dimension.width; x++) {
											blocks[y][x].setTerrain(Integer.parseInt(String.valueOf((char)in.read())));
											if (x == this.dimension.width - 1) {
												in.readLine();
											}
										}
									}
								}
								break;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		pathfinder = new Pathfinder(this);
	}
	
	private void initializeBlocks() {
        final int w = dimension.width;
        final int h = dimension.height;
        blocks = new Block[w][h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                blocks[y][x] = new Block(new Point(x, y));
            }
        }
        
        int a = 15, b = 20, c = 9, d = 36;
		int i = a + 20, j = b + 12, k = c + 7, l = d + 7;
		for (int y = 0; y < dimension.height; y++) {
			for (int x = 0; x < dimension.width; x++) {
				if ((y == a && Math.abs(x-b) <= 10) 
						|| (x == c && Math.abs(y-a) <= 10)
						|| (x == d && Math.abs(y-a) <= 10))
					blocks[y][x].setTerrain(World.Terrain.BLOCK_GROUND);
			}
		}
		
		for (int y = 0; y < dimension.height; y++) {
			for (int x = 0; x < dimension.width; x++) {
				if ((y == i && Math.abs(x-j) <= 10) 
						|| (x == k && Math.abs(y-i) <= 10)
						|| (x == l && Math.abs(y-i) <= 10))
					blocks[y][x].setTerrain(World.Terrain.BLOCK_GROUND);
			}
		}
    }
	
	public void draw(Graphics2D g2) {
//		// draw grid
//		g2.setColor(Color.GRAY);
//		for (int y = 1; y < dimension.height; y++) {
//			g2.drawLine(0, y, dimension.width, y);
//		}
//		for (int x = 1; x < dimension.width; x++) {
//			g2.drawLine(x, 0, x, dimension.height);
//		}
		
		for (World.Layer layer : World.Layer.values())
        {
	        for (int i = 0; i < dimension.height; i++) {
	            for (int j = 0; j < dimension.width; j++) {
	                blocks[i][j].draw(g2, layer);
	            }
	        }
        }
    }

	
	/**
     * Get the specified block of this board
     * 
     * @return     block 
     */
    public Block getBlock(Point pos) {
        if (!isInside(pos))
            throw new ArrayIndexOutOfBoundsException("The position is out of board");
        return blocks[pos.y][pos.x];
//    	if (isInside(pos))
//    		return blocks[pos.y][pos.x];
//    	else
//    		return NULL_BLOCK;
    }
    
    /**
     * Check whether a point is inside this board
     * 
     * @param   pos
     * @return     true if inside, false if outside 
     */
    public final boolean isInside(final Point pos) {
    	if (pos.x < 0 || pos.x > dimension.width-1 || pos.y < 0 || pos.y > dimension.height-1)
            return false;
        else
            return true;
    }
    
    public Dimension getDimension() {
    	return dimension;
    }

	public ArrayList<Point> findPath(Unit unit, Point start, Point end) {
		ArrayList<Point> path;
		path = pathfinder.findPath(unit, start, end);
		return path;
	}

// Debug
public void resetBackgroundColor() {
	for (int y = 0; y < dimension.height; y++) {
		for (int x = 0; x < dimension.width; x++) {
			blocks[y][x].resetBackgroundColor();
		}
	}
}
	
//// Debug	
//public void clearAllUnits(final World.Layer layer) {
//	for (int y = 0; y < dimension.height; y++) {
//		for (int x = 0; x < dimension.width; x++) {
//			blocks[y][x].getUnit(layer).getOwner().removeAllUnit(blocks[y][x].getUnit(layer));
//		}
//	}
//}
	
}
