import java.util.ArrayList;

public class Players {
	private ArrayList<AIPlayer> aiPlayerList;
	private HumanPlayer humanPlayer;
	private MotherPlayer motherPlayer;
	
	/**
	 * @param world
	 */
	public Players(World world) {
		super();
		aiPlayerList = new ArrayList<AIPlayer>(World.PlayerInfo.NUM_AI_PLAYERS);
//		for (int i = 0; i < World.PlayerInfo.NUM_AI_PLAYERS; i++) {
//			AIPlayer aiPlayer = new AIPlayer(world, World.PlayerInfo.AI1);
//			aiPlayerList.add(aiPlayer);
//		}
		for (World.PlayerInfo player : World.PlayerInfo.values()) {
			if (player.name() == "MOTHER") {
				motherPlayer = new MotherPlayer(world, player.color);
			}
			else if (player.name() == "HUMAN") {
				humanPlayer = new HumanPlayer(world, player.color);
			}
			else {
				aiPlayerList.add(new AIPlayer(world, player.color));
			}
		}
	}

	public void aiGiveOrder() {
		for (int i = 0; i < aiPlayerList.size(); i++) {
			aiPlayerList.get(i).giveOrder();
		}
	}

	public void excuteOrder(final double timeStep) {
		humanPlayer.excuteOrder(timeStep);
		motherPlayer.excuteOrder(timeStep);
		for (int i = 0; i < World.PlayerInfo.NUM_AI_PLAYERS; i++) {
			aiPlayerList.get(i).excuteOrder(timeStep);
		}
	}
	
	public HumanPlayer getHumanPlayer() {
		return humanPlayer;
	}
	
	public MotherPlayer getMotherPlayer() {
		return motherPlayer;
	}
	
//	public AIPlayer getAIPlayer(int i) {
//		return this.aiPlayerList.get(i);
//	}
}
