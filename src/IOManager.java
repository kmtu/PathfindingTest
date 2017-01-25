import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author floatu
 *
 */
public class IOManager implements MouseInputListener, KeyListener {
    private World world;
    private HumanPlayer humanPlayer;
    private GamePanel gamePanel;
    private AffineTransform deviceToUserCoordTransform;
    private AffineTransform userToDeviceCoordTransform;
    private boolean[] mouseButtonStatus; // {Empty, Button1, Button2, Button3}
    private Point2D.Double mousePos;
    private Point mouseBlockPos;
    private Point2D.Double selectionRectCorner1;
    private Point2D.Double selectionRectCorner2;
    private Point selectionRectCornerBlock1;
    private Point selectionRectCornerBlock2;
    private Rectangle2D.Double selectionRectangle;
    
    public static final Stroke SELECTION_STROKE = new BasicStroke(0.3f);
    public static final Color SELECTION_GLOW_COLOR = Color.GREEN;
    public static final BasicStroke BASIC_STROKE = new BasicStroke(0);
	
    /**
     * @param world
     */
    public IOManager(World world) {
    	super();
    	this.world = world;
    	humanPlayer = world.getPlayers().getHumanPlayer();
		
    	gamePanel = new GamePanel();
    	gamePanel.addMouseListener(this);
    	gamePanel.addMouseMotionListener(this);
    	gamePanel.addKeyListener(this);
    	JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(gamePanel);
//      frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        
        mouseButtonStatus = new boolean[]{false, false, false, false};
        mousePos = new Point2D.Double();
        mouseBlockPos = new Point();
        selectionRectangle = new Rectangle2D.Double();
        selectionRectCorner1 = new Point2D.Double();
        selectionRectCorner2 = new Point2D.Double();
        selectionRectCornerBlock1 = new Point();
        selectionRectCornerBlock2 = new Point();
    }
    
    public void repaint() {
    	gamePanel.repaint();
    }
	
	/**
     * Write a description of class GamePanel here.
     * 
     * @author TuTu
     * @version 2007.11.8
     */
    public class GamePanel extends JPanel
    {
        /**
	 * 
	 */
	private static final long serialVersionUID = 4364938303615486947L;

	/**
         * Constructor for objects of class GamePanel
         */
        public GamePanel()
        {
            setPreferredSize(new Dimension(640, 640));
            setBackground(Color.WHITE);
            setDoubleBuffered(true);
            setFocusable(true); // so that the panel can receive keyboard messages
        }
    
        /**
         * override
         * 
         * @param  y   a sample parameter for a method
         * @return     the sum of x and y 
         */
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            makeUserToDeviceCoordTransform();
            makeDeviceToUserCoordTransform();
            g2.transform(userToDeviceCoordTransform);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(BASIC_STROKE);
            world.render(g2);
            drawSelectionRectangle(g2);
        }
    }

    /**
     * Make the deviceToUserCoordTransform of this board
     */
    private void makeDeviceToUserCoordTransform() {
        try {
            deviceToUserCoordTransform = userToDeviceCoordTransform.createInverse();
        }
        catch (java.awt.geom.NoninvertibleTransformException e) {
            System.out.println("java.awt.geom.NoninvertibleTransformException");
            System.exit(1);
        }
    }    

    public void drawSelectionRectangle(Graphics2D g2) {
    	g2.setStroke(SELECTION_STROKE);
        g2.setColor(SELECTION_GLOW_COLOR);
        g2.draw(selectionRectangle);		
	}

	/**
     * Make the userToDeviceCoordTransform of this board
     */
    private void makeUserToDeviceCoordTransform() {
        AffineTransform coordT = new AffineTransform();     
        double xscale = (gamePanel.getSize().width) / (double)world.getBoard().getDimension().width;
        double yscale = (gamePanel.getSize().height) / (double)world.getBoard().getDimension().height;
        coordT.scale(xscale, yscale);
        userToDeviceCoordTransform = coordT;
    }
    
    
    
    
    public void mouseDragged(MouseEvent e) {
    	mousePos.setLocation(e.getX(), e.getY());
        deviceToUserCoordTransform.transform(mousePos, mousePos);
        mouseBlockPos.setLocation((int)mousePos.x, (int)mousePos.y);
        if (mouseButtonStatus[MouseEvent.BUTTON1])
        {
            selectionRectCorner2.setLocation(mousePos.x, mousePos.y);
            selectionRectCornerBlock2.setLocation(mouseBlockPos.x, mouseBlockPos.y);
            selectionRectangle.setFrameFromDiagonal(selectionRectCorner1, selectionRectCorner2);
        }
	}

	public void mouseMoved(MouseEvent e) {		
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {		
	}

	public void mousePressed(MouseEvent e) {
		mouseButtonStatus[e.getButton()] = true;
        mousePos.setLocation(e.getX(), e.getY());
        deviceToUserCoordTransform.transform(mousePos, mousePos);
        mouseBlockPos.setLocation((int)mousePos.x, (int)mousePos.y);
        
        if (e.getButton() == MouseEvent.BUTTON1) {
            selectionRectCorner1.setLocation(mousePos.x, mousePos.y);
            selectionRectCornerBlock1.setLocation(mouseBlockPos.x, mouseBlockPos.y);
            humanPlayer.deSelectAllUnit();
 // Debug
world.getBoard().resetBackgroundColor();
        }
        
        if (e.getButton() == MouseEvent.BUTTON3) {
            if (world.getBoard().getBlock(mouseBlockPos).isUnitOn() 
            		&& !(world.getBoard().getBlock(mouseBlockPos)).getTopUnit().getOwner().equals(humanPlayer)) {
//            	humanPlayer.order(new AttackOrder(world.getBoard().getBlock(mouseBlockPos).getTopUnit()));
            }
            else { // order move
// Debug
System.out.println("mouse pressed:" + mouseBlockPos.toString());
				humanPlayer.queueOrder(new MoveOrder((Point)mouseBlockPos.clone()));
				if (!e.isShiftDown()) {
					humanPlayer.giveOrder();
				}
            }
        }
	}

	public void mouseReleased(MouseEvent e) {
		mouseButtonStatus[e.getButton()] = false;
        mousePos.setLocation(e.getX(), e.getY());
        deviceToUserCoordTransform.transform(mousePos, mousePos);
        mouseBlockPos.setLocation((int)mousePos.x, (int)mousePos.y);
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            selectionRectCorner2.setLocation(mousePos.x, mousePos.y);
            selectionRectCornerBlock2.setLocation(mouseBlockPos.x, mouseBlockPos.y);

            // if mouse has not dragged, select any unit on that block
            // else if mouse has dragged, select only human units
            if (selectionRectCornerBlock1.x == selectionRectCornerBlock2.x
                && selectionRectCornerBlock1.y == selectionRectCornerBlock2.y
                && world.getBoard().getBlock(mouseBlockPos).isUnitOn()
                && world.getBoard().getBlock(mouseBlockPos).getTopUnit() instanceof Selectable) {
                humanPlayer.selectUnit((Selectable)world.getBoard().getBlock(mouseBlockPos).getTopUnit());
            }
            else
            {
                int xMin, xMax;
                int yMin, yMax;
                if (selectionRectCornerBlock1.x >= selectionRectCornerBlock2.x)
                {
                    xMin = selectionRectCornerBlock2.x;
                    xMax = selectionRectCornerBlock1.x;
                }
                else
                {
                    xMin = selectionRectCornerBlock1.x;
                    xMax = selectionRectCornerBlock2.x;
                }
                
                if (selectionRectCornerBlock1.y >= selectionRectCornerBlock2.y)
                {
                    yMin = selectionRectCornerBlock2.y;
                    yMax = selectionRectCornerBlock1.y;
                }
                else
                {
                    yMin = selectionRectCornerBlock1.y;
                    yMax = selectionRectCornerBlock2.y;
                }
                
                Point pos = new Point();
                for (int x = xMin; x <= xMax; x++)
                {
                    for (int y = yMin; y <= yMax; y++)
                    {
                        pos.setLocation(x, y);
                        if (world.getBoard().isInside(pos) && world.getBoard().getBlock(pos).isUnitOn()
                        		&& world.getBoard().getBlock(pos).getTopUnit() instanceof Selectable
                        		&& world.getBoard().getBlock(pos).getTopUnit().getOwner().equals(humanPlayer)) {
                            humanPlayer.selectUnit((Selectable)world.getBoard().getBlock(pos).getTopUnit());
                        }
                    }
                }                
            }
            selectionRectangle.setFrame(0,0,0,0);
    //             selectionRectCorner1.setLocation(-1,-1);
    //             selectionRectCorner2.setLocation(-1,-1);
    //             selectionRectCornerBlock1.setLocation(-1,-1);
    //             selectionRectCornerBlock2.setLocation(-1,-1);
        }
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_S:
				humanPlayer.queueOrder(Order.stopOrder);
				humanPlayer.giveOrder();
				break;
		}
	}

	public void keyReleased(KeyEvent e) {		
	}

	public void keyTyped(KeyEvent e) {
	}		
}