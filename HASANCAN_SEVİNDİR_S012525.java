import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {
	public static int MAX_DEPTH = 6;
	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		MAX_DEPTH = readInt("Max Depth:");

		while (MAX_DEPTH > 6) {
			System.out.println("You should enter max. 6 depth.");

			MAX_DEPTH = readInt("Max Depth:");
		}

		Game game = new Game();

		int side = 0;

		while (side == 0) {
			System.out.println(game);

			int move = readInt("Enter the move:");

			if (!game.canAct(move - 1)) {
				System.out.println("Illegal move! You lose.");
				return;
			}

			game.act(move - 1, Game.USER);

			side = GameCheck.check(game.getBoard());

			if (side != 0)
				break;

			if (!game.canAct()) {
				System.out.println("No Winner!");

				return;
			}

			move = Player.act(game);

			game.act(move, Game.PROGRAM);

			side = GameCheck.check(game.getBoard());

			if (side == 0 && !game.canAct()) {
				System.out.println("No Winner!");

				return;
			}
		}

		System.out.println(game);

		if (side == 1) {
			System.out.println("You lose.");
		} else if (side == -1) {
			System.out.println("You win.");
		}
	}



	private static int readInt(String text) {
		System.out.print(text);

		try {
			String ans = scanner.nextLine();

			int value = Integer.parseInt(ans);

			if (value <= 0) {
				System.out.println("You should enter a positive number.");

				return readInt(text);
			}

			return value;
		} catch (Exception e) {
			System.out.println("Wrong number.");
			return readInt(text);
		}
	}

	public static class Game {
		private final int[][] board;

		public static final int USER = -1;
		public static final int PROGRAM = 1;

		public Game() {
			this.board = new int[6][7];
		}

		public Game(int[][] board) {
			this.board = new int[6][7];

			for (int i = 0; i < 6; i++) {
				System.arraycopy(board[i], 0, this.board[i], 0, 7);
			}
		}

		public boolean canAct() {
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 7; j++) {
					if (board[i][j] == 0)
						return true;
				}
			}

			return false;
		}

		public boolean canAct(int column) {
			if (column > 6 || column < 0)
				return false;

			for (int i = 0; i < 6; i++) {
				if (board[i][column] != 0)
					continue;

				return true;
			}

			return false;
		}

		public boolean act(int column, int player) {
			if (column > 6 || column < 0)
				return false;

			for (int i = 0; i < 6; i++) {
				if (board[i][column] != 0)
					continue;

				board[i][column] = player;

				return true;
			}

			return false;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();

			for (int i = 5; i >= 0; i--) {
				for (int j = 0; j < 7; j++) {
					if (board[i][j] == 1)
						builder.append(" O ");
					else if (board[i][j] == -1)
						builder.append(" X ");
					else
						builder.append(" # ");
				}
				builder.append("\n");
			}

			return builder.toString();
		}

		public int[][] getBoard() {
			return board;
		}
	}

	public static class GameCheck {
		public static int check(int[][] board) {
			int side = horizontalCheck(board);

			if (side != 0)
				return side;

			side = verticalCheck(board);

			if (side != 0)
				return side;

			side = diagonalCheck(board);

			if (side != 0)
				return side;

			return 0;
		}

		private static int diagonalCheck(int[][] board) {
			for (int i = 0; i <= 2; i++) {
				for (int j = 0; j <= 3; j++) {
					int counter = 0;

					for (int k = 0; k < 4; k++) {
						counter += board[i + k][j + k];
					}

					if (Math.abs(counter) == 4)
						return counter > 0 ? 1 : -1;
				}
			}

			for (int i = 0; i <= 2; i++) {
				for (int j = 3; j < 7; j++) {
					int counter = 0;

					for (int k = 0; k < 4; k++) {
						counter += board[i + k][j - k];
					}

					if (Math.abs(counter) == 4)
						return counter > 0 ? 1 : -1;
				}
			}

			return 0;
		}

		private static int verticalCheck(int[][] board) {
			for (int j = 0; j < 7; j++) {
				int counter = 0;
				int side = 0;

				for (int i = 0; i < 6; i++) {
					if (board[i][j] == side) {
						counter++;
					} else {
						side = board[i][j];
						counter = 0;

						if (side != 0)
							counter++;
					}

					if (counter == 4 && side != 0)
						return side;
				}
			}

			return 0;
		}

		private static int horizontalCheck(int[][] board) {
			for (int i = 0; i < 6; i++) {
				int counter = 0;
				int side = 0;

				for (int j = 0; j < 7; j++) {
					if (board[i][j] == side) {
						counter++;
					} else {
						side = board[i][j];
						counter = 0;

						if (side != 0)
							counter++;
					}

					if (counter == 4 && side != 0)
						return side;
				}
			}

			return 0;
		}
	}

	public static class Player {
		public static int act(Game game) {
			int maxScore = Integer.MIN_VALUE;
			int maxMove = -1;

			for (int i = 0; i < 7; i++) {
				if (!game.canAct(i))
					continue;

				Game nextGame = new Game(game.getBoard());
				nextGame.act(i, Game.PROGRAM);

				SearchNode node = new SearchNode(nextGame, SearchMode.MINIMIZE, 2, i);

				int score = node.getScore();

				if (score > maxScore) {
					maxScore = score;
					maxMove = i;
				}
			}

			return maxMove;
		}

		private static class SearchNode {
			private final Game game;

			private final SearchMode mode;

			private final int depth;

			protected final int score;

			protected final ArrayList<SearchNode> children;

			protected final int move;

			public SearchNode(Game game, SearchMode mode, int depth, int move) {
				this.mode = mode;
				this.game = game;
				this.depth = depth;
				this.move = move;

				score = GameCheck.check(game.getBoard());

				children = new ArrayList<>();
			}

			public void getChildren() {
				children.clear();

				if (depth >= Main.MAX_DEPTH) {
					return;
				}

				for (int i = 0; i < 7; i++) {
					if (!game.canAct(i))
						continue;

					Game child = new Game(game.getBoard());
					if (mode == SearchMode.MAXIMIZE)
						child.act(i, Game.PROGRAM);
					else
						child.act(i, Game.USER);

					children.add(new SearchNode(child, mode == SearchMode.MAXIMIZE ? SearchMode.MINIMIZE : SearchMode.MAXIMIZE, depth + 1, i));
				}
			}

			public int getScore() {
				if (score != 0)
					return score;

				if (!game.canAct())
					return 0;

				getChildren();

				if (children.isEmpty())
					return 0;

				int minScore = Integer.MAX_VALUE;
				int maxScore = Integer.MIN_VALUE;

				for (SearchNode node : children) {
					int score = node.getScore();

					if (score < minScore)
						minScore = score;

					if (score > maxScore)
						maxScore = score;
				}

				return mode == SearchMode.MAXIMIZE ? maxScore : minScore;
			}
		}
	}

	public enum SearchMode {
		MINIMIZE, MAXIMIZE
	}
}