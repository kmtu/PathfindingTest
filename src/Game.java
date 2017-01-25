/**
 * 
 */

/**
 * @author TuTu
 *
 */
public class Game extends Thread{
	private World world;
	private IOManager ioManager;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}

	/**
	 * 
	 */
	public Game() {
		super();
		world = new World();
		ioManager = new IOManager(world);
	}


	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		super.run();
        while(world.alive)
        {
            world.evolve();
            ioManager.repaint();
        }
	}
	
}
