/**
 * Thread which plays an ai turn
 * Date created: 10 January 2016
 * Last modified: 11 January 2016
 * @author Dennis Krasnov
 */
package summativeChess;

import java.util.ArrayList;
import java.util.List;

public class AIMove implements Runnable {

	// The four available difficulty levels
	public static int PASSIVE = 0;
	public static int AGGESSIVE = 1;
	public static int EASY = 2;
	public static int MEDIUM = 3;

	// The cost of the figure weight
	private static int[] W_FIGURE_COST = { 1000, 1000, 1000, 1000 };
	// The risk of loosing the figure weight
	private static int[] W_FIGURE_RISK = { 1000, 500, 750, 750 };
	// The weight of scoring a check
	private static int[] W_CHECK = { 750, 5000, 1500, 1500 };
	// The weight of scoring a checkmate
	private static int[] W_CHECKMATE = { 1000000, 1000000, 1000000, 1000000 };
	// The weight of positioning in the middle of the board
	private static int[] W_POSITIONING = { 25, 75, 50, 50 };
	// The weight of reducing the enemies king's moves
	private static int[] W_ATTACK_AROUND_ENEMY_KING = { 0, 0, 0, 500 };
	// The weight of how many enemy figures a figure can attack if it moves onto
	// that square
	private static int[] W_ATTACK_ON_ENEMY_FIGURES = { 0, 0, 0, 5 };
	// The weight of how much a King doesn't want to move
	private static int[] W_KING_LAZINESS = { 0, 0, 0, 50 };
	// The weight of exactly repeating a turn
	private static int[] W_DISCOURAGE_REPEAT = { 0, 0, 0, 50 };

	// The type of difficulty
	private int type = -1;

	/**
	 * Finds the best move, with its own wait time Dependency: Board,
	 * ChessFrame, ArrayList, Figure Date created: 10 January 2016 Last
	 * modified: 11 January 2016
	 * 
	 * @author Dennis Krasnov @param none @return none @throws
	 */
	@Override
	public void run() {
		// Instance of board
		Board board = Board.getBoard();
		if (board.isWhiteMove()) {
			type = board.getWhitePlayer().getDifficulty();
		} else {
			type = board.getBlackPlayer().getDifficulty();
		}

		// Adds a "think" time for the computer
		try {
			int sleepTime = (int)(Math.random() * 10) + 10;
			for(int i = 0; i < sleepTime && !board.endOfGame; i++) {
				Thread.sleep(100);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Disables other player to move anything
		ChessFrame.getChessFrame().getBoardPanel().setEnabled(false);
		// Finds all of my figures
		ArrayList<Figure> myFigures = new ArrayList<Figure>();
		for (int h = 1; h <= 8; h++) {
			for (int v = 1; v <= 8; v++) {
				Figure figure = board.getSquare(h, v).getFigure();
				if (figure != null && figure.isWhiteFigure() == board.isWhiteMove()) {
					myFigures.add(figure);
				}
			}
		}

		// Finds all of his figure's moves
		List<Move> myMoves = new ArrayList<Move>();
		for (Figure f : myFigures) {
			if (f.getAllMoves(Move.VALID_MOVES) != null && f.getAllMoves(Move.VALID_MOVES).size() != 0) {
				myMoves.addAll(f.getAllMoves(Move.VALID_MOVES));
			}
		}
		// Sorts the moves from smallest to largest (largest = best move)
		myMoves = insertionSort(myMoves);

		// Enables other player's actions
		ChessFrame.getChessFrame().getBoardPanel().setEnabled(true);
		if (!Board.getBoard().endOfGame) {
			// Performs the last (best) move
			board.performMove(myMoves.get(myMoves.size() - 1));
		}
		ChessFrame.getChessFrame().updateBoard();
	}

	/**
	 * Insertion sorts a list of moves Dependency: ArrayList, Move Date created:
	 * 18 December 2015 Last modified: 19 December 2015
	 * 
	 * @author Dennis Krasnov
	 * @param list
	 *            list that will be sorted
	 * @return none
	 * @throws none
	 */
	public List<Move> insertionSort(List<Move> list) {
		List<Move> sortedMoves = new ArrayList<Move>();
		int i, j; // Indices in array
		for (j = 0; j < list.size(); j++) {
			int otherScore = evaluateMoveScore(list.get(j));
			// Cycles through moves, finds location of "wall"
			for (i = 0; i < sortedMoves.size() && evaluateMoveScore(sortedMoves.get(i)) < otherScore; i++) {
			}
			sortedMoves.add(i, list.get(j));
		}
		return sortedMoves;
	}

	/**
	 * Evaluates how good a move is Dependency: Board, ArrayList, Figure, Move,
	 * King Date created: 10 January 2016 Last modified: 11 January 2016
	 * 
	 * @author Dennis Krasnov @param move the move that will be
	 *         evaluated @return int the score of the move @throws
	 */
	public int evaluateMoveScore(Move move) {
		// Instance of board
		Board board = Board.getBoard();
		// Move's score
		int score = 0;
		// If move eats a figure
		if (move.getEatenFigure() != null) {
			// Adds on the eaten figure's cost
			score += move.getEatenFigure().getScore() * getWeight(W_FIGURE_COST);
			// If that square is also under attack, subtract the figure's risk
			if (board.isUnderAttack(move.getDestination(), move.getFigure().isWhiteFigure())) {
				score -= move.getFigure().getScore() * getWeight(W_FIGURE_RISK);
			}

		} else {
			// If that square only under attack, subtract the figure's cost
			if (board.isUnderAttack(move.getDestination(), move.getFigure().isWhiteFigure())) {
				score -= move.getFigure().getScore() * getWeight(W_FIGURE_COST);
			}
		}
		// Adds on the figure's risk if if successfully gets out of danger from
		// being eaten, excluding the King
		if (board.isUnderAttack(move.getStart(), move.getFigure().isWhiteFigure())) {
			if (!board.isUnderAttack(move.getDestination(), move.getFigure().isWhiteFigure())) {
				if (!(move.getFigure() instanceof King)) {
					score += move.getFigure().getScore() * getWeight(W_FIGURE_RISK);
				}
			}
		}

		// To discourage king moves
		if (move.getFigure() instanceof King) {
			score -= getWeight(W_KING_LAZINESS);
		}

		// To discourage repeat moves
		if (board.getMoveHistory().size() - 2 > 0) {
			Move previousMove = board.getMoveHistory().get(board.getMoveHistory().size() - 2);
			if (move.getDestination() == previousMove.getStart()) {
				score -= getWeight(W_DISCOURAGE_REPEAT);
			}
		}

		// To value positioning
		score += findSquareWorth(move.getDestination()) * getWeight(W_POSITIONING);
		// To value check
		if (move.isCheck()) {
			score += getWeight(W_CHECK);
		}
		// To value checkmate
		if (move.isCheckmate()) {
			score += getWeight(W_CHECKMATE);
		}

		// Adds on a dynamic positioning score
		score += dynamicPositionScore(move);

		move.setScore(score);

		return score;
	}

	/**
	 * Finds the value of a square position, center being worth more than the
	 * outer edges Dependency: Square Date created: 10 November 2015 Last
	 * modified: 20 December 2015
	 * 
	 * @author Dennis Krasnov
	 * @param square
	 *            that is going to get evaluated
	 * @return int the value of that square
	 * @throws none
	 */
	public int findSquareWorth(Square square) {
		// Score for the position
		int score = 0;
		// X axis location
		int h = square.getHPos();
		// Y axis location
		int v = square.getVPos();

		// Horizontal calculation
		if (h == 1 || h == 8) {
			score += 1;
		} else if (h == 2 || h == 7) {
			score += 2;
		} else if (h == 3 || h == 6) {
			score += 3;
		} else if (h == 4 || h == 5) {
			score += 4;
		}

		// Vertical calculation
		if (v == 1 || v == 8) {
			score += 1;
		} else if (v == 2 || v == 7) {
			score += 2;
		} else if (v == 3 || v == 6) {
			score += 3;
		} else if (v == 4 || v == 5) {
			score += 4;
		}

		return score;
	}

	/**
	 * Adds on the weight to the logic, adds a 10% randomness factor to
	 * discourage repeat moves even further Dependency: Math Date created: 10
	 * January 2016 Last modified: 11 January 2016
	 * 
	 * @author Dennis Krasnov @param move the move that will be
	 *         evaluated @return int the score of the move @throws none
	 */
	private int getWeight(int[] variant) {
		return (int) (variant[type] * (Math.random() * 0.2 + 0.9));
	}

	/**
	 * Evaluates how good a move is by playing the move, analysing it, then
	 * rolling it back Dependency: Board, ArrayList, Figure, Move Date created:
	 * 10 January 2016 Last modified: 11 January 2016
	 * 
	 * @author Dennis Krasnov @param move the move that will be
	 *         evaluated @return int the additional score to added to the total
	 *         score of the move @throws none
	 */
	private int dynamicPositionScore(Move move) {
		// Instance of Board
		Board board = Board.getBoard();
		// Move's score
		int score = 0;
		// List of my figures
		List<Figure> myFigures = new ArrayList<Figure>();
		// Enemy King
		Figure enemyKing = null;
		// Number of attacks around king before move
		int beforeAttacksAroundEnemyKing = 0;
		// Number of attacks around king after move
		int afterAttacksAroundEnemyKing = 0;
		// Total of potential eaten figures's score before move
		int beforeSumOfAttacks = 0;
		// Total of potential eaten figures's score after move
		int afterSumOfAttacks = 0;

		// Finds all of my figures and enemy king
		for (int h = 1; h <= 8; h++) {
			for (int v = 1; v <= 8; v++) {
				Figure figure = board.getSquare(h, v).getFigure();
				if (figure != null && figure.isWhiteFigure() == move.getFigure().isWhiteFigure()) {
					myFigures.add(figure);
				}
				if (figure != null && figure instanceof King
						&& figure.isWhiteFigure() != move.getFigure().isWhiteFigure()) {
					enemyKing = figure;
				}
			}
		}

		// Finds each figure's potential attack worth, adds all to sum
		for (Figure fig : myFigures) {
			List<Move> figureMoves = fig.getAllMoves(Move.ATTACKABLE_SQUARES);
			if (figureMoves != null && !(fig instanceof King)) {
				for (Move m : figureMoves) {
					if (m.getEatenFigure() != null) {
						beforeSumOfAttacks += m.getEatenFigure().getScore();
					}
				}
			}
		}

		// Finds the number of attacks around enemy king, including his allied
		// pieces
		for (Move m : enemyKing.getAllMoves(Move.ALL_POSSIBILITIES)) {
			if (board.isUnderAttack(m.getDestination(), m.getFigure().isWhiteFigure())) {
				beforeAttacksAroundEnemyKing++;
			}
		}

		// Move
		// Clear old figure location
		move.getStart().setFigure(null);
		// Set new figure location
		move.getDestination().setFigure(move.getFigure());

		// Finds each figure's potential attack worth, adds all to sum
		for (Figure fig : myFigures) {
			List<Move> figureMoves = fig.getAllMoves(Move.ATTACKABLE_SQUARES);
			if (figureMoves != null && !(fig instanceof King)) {
				for (Move m : figureMoves) {
					if (m.getEatenFigure() != null) {
						afterSumOfAttacks += m.getEatenFigure().getScore();
					}
				}
			}
		}

		// Finds the number of attacks around enemy king, including his allied
		// pieces
		for (Move m : enemyKing.getAllMoves(Move.ALL_POSSIBILITIES)) {
			if (board.isUnderAttack(m.getDestination(), m.getFigure().isWhiteFigure())) {
				afterAttacksAroundEnemyKing++;
			}
		}

		// Ads the weighted score of the difference in between before and after
		// the move
		score += (afterAttacksAroundEnemyKing - beforeAttacksAroundEnemyKing) * getWeight(W_ATTACK_AROUND_ENEMY_KING);
		score += (afterSumOfAttacks - beforeSumOfAttacks) * getWeight(W_ATTACK_ON_ENEMY_FIGURES);

		// Roll back
		move.getStart().setFigure(move.getFigure());
		move.getDestination().setFigure(move.getEatenFigure());
		return score;
	}

}