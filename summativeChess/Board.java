/**
 * Frame which contains information about the program and project
 * Date created: 10 November 2015
 * Last modified: 11 January 2016
 * @author Dennis Krasnov
 */
package summativeChess;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Board {
	// Letter representatives of the columns of the board
	public static final char LETTERS[] = { ' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
	// Instance of board
	private static Board board = new Board();

	public static Board getBoard() {
		return board;
	}

	// Grid which chess is played on
	private Square[][] grid;
	// Square currently selected by user
	private Square selectedSquare;
	// Who's turn is it in boolean
	private boolean whiteMove = true;
	// The history in moves
	private ArrayList<Move> moveHistory = new ArrayList<Move>();
	// The history in StringBuilder
	private StringBuilder history = new StringBuilder();
	// Which turn it is
	private int turn = 1;

	// Player 1
	private Player whitePlayer;
	// Player 2
	private Player blackPlayer;

	// Is player 1 a computer
	private boolean whiteAI = false;
	// Is player 2 a computer
	private boolean blackAI = true;

	// Override string for the screamer?
	private String screamerText = null;
	// Is end of game
	public boolean endOfGame = false;

	public Board() {
		// Initializing player with default values
		whitePlayer = new Player(1, "White pl.", -1, 00);
		// Initializing player with default values
		blackPlayer = new Player(2, "Black CPU", -1, 00);
		// Creating grid and squares
		grid = new Square[8][8];
		for (int h = 1; h <= 8; h++) {
			for (int v = 1; v <= 8; v++) {
				Square square = new Square(h, v);
				square.setUpPieces();
				grid[h - 1][v - 1] = square;
			}
		}
	}

	public void addToTurn(int turn) {
		this.turn += turn;
	}

	/**
	 * Creates thread that plays turn by itself Dependency: ChessFrame, List,
	 * Move Date created: 10 November 2015 Last modified: 23 December 2015
	 * 
	 * @author Dennis Krasnov
	 * @param none
	 * @return none
	 * @throws none
	 */
	public void aiTurn() {
		new Thread(new AIMove()).start();
	}

	public void clearHistory() {
		history.delete(0, history.length());
	}

	public void clearHistoryArray() {
		moveHistory.clear();
	}

	public StringBuilder getHistory() {
		return history;
	}

	public ArrayList<Move> getMoveHistory() {
		return moveHistory;
	}

	/**
	 * Gets the next square from a starting point and a h and v (x, y) vector
	 * Dependency: Square Date created: 10 November 2015 Last modified: 15
	 * December 2015
	 * 
	 * @author Dennis Krasnov
	 * @param square
	 *            the starting position
	 * @param hd
	 *            the distance moved horizontally (x axis)
	 * @param vd
	 *            the distance moved vertically (y axis)
	 * @return Square the new square that has been moved on
	 * @throws If
	 *             the new square is outside of the board, will return null
	 */
	public Square getNextSquare(Square square, int hd, int vd) {
		int h = square.getHPos() + hd;
		int v = square.getVPos() + vd;

		if (h > 8 || h < 1 || v > 8 || v < 1) {
			return null;
		}
		return getSquare(h, v);
	}

	public Player getWhitePlayer() {
		return whitePlayer;
	}

	public Player getBlackPlayer() {
		return blackPlayer;
	}

	public Square getSelectedSquare() {
		return selectedSquare;
	}

	public Square getSquare(int h, int v) {
		return grid[h - 1][v - 1];
	}

	public int getTurn() {
		return turn;
	}

	/**
	 * Checks every enemy move to see if it could hit square Dependency: List,
	 * Square, Figure, Move Date created: 10 November 2015 Last modified: 15
	 * December 2015
	 * 
	 * @author Dennis Krasnov
	 * @param square
	 *            the field that is being checked if its under attack
	 * @param victimIsWhite
	 *            to update enemy moves
	 * @return boolean if the square is under attack or not
	 * @throws Will
	 *             ensure that there is an enemy moves to calculate from
	 */
	public boolean isUnderAttack(Square square, boolean victimIsWhite) {
		// List of current enemy moves
		List<Move> enemyMoves = new ArrayList<Move>();
		// Goes through each enemy figure and adds all of its valid moves
		for (int h = 0; h < 8; h++) {
			for (int v = 0; v < 8; v++) {
				Figure piece = grid[h][v].getFigure();
				if (piece != null && piece.isWhiteFigure() != victimIsWhite
						&& piece.getAllMoves(Move.ATTACKABLE_SQUARES) != null) {
					enemyMoves.addAll(piece.getAllMoves(Move.ATTACKABLE_SQUARES));
				}
			}
		}
		// Check to see if square is under attack by one of the enemy moves
		boolean isUnderFire = false;
		for (Move move : enemyMoves) {
			if (!isUnderFire && move.getDestination().equals(square)) {
				isUnderFire = true;
			}
		}
		return isUnderFire;
	}

	public boolean isWhiteMove() {
		return whiteMove;
	}

	/**
	 * Performs move, plays correct sound, will trigger promotion dialog,
	 * updates screamer text if necessary, will end game if turn is checkmate or
	 * stalemate; if AI, will make a queen automatically Dependency: ChessFrame,
	 * Move, Queen Date created: 10 November 2015 Last modified: 11 January 2016
	 * 
	 * @author Dennis Krasnov
	 * @param move
	 *            move that is going to get performed
	 * @return none
	 * @throws Will
	 *             not play sound if move does not specify the right type
	 */
	public void performMove(Move move) {

		// Clear old figure location
		move.getStart().setFigure(null);
		// Set new figure location
		move.getDestination().setFigure(move.getFigure());
		// Check for castle left
		if (move.getTypeOfTurn() == Move.CASTLE_LEFT) {
			getNextSquare(move.getStart(), -1, 0).setFigure(getNextSquare(move.getStart(), -4, 0).getFigure());
			getNextSquare(move.getStart(), -4, 0).setFigure(null);
		}
		// Check for castle right
		if (move.getTypeOfTurn() == Move.CASTLE_RIGHT) {
			getNextSquare(move.getStart(), 1, 0).setFigure(getNextSquare(move.getStart(), 3, 0).getFigure());
			getNextSquare(move.getStart(), 3, 0).setFigure(null);
		}
		// Sets selected to nothing
		setSelectedSquare(null);
		// Adds move to move history
		getMoveHistory().add(move);

		// Promotion dialog
		if (move.isPromotion()) {
			if ((whiteAI && whiteMove) || (blackAI && !whiteMove)) {
				Queen queen = new Queen(whiteMove);
				move.getDestination().setFigure(queen);
			} else {
				new PromotionDialog(move.getDestination());
			}
		}

		endOfGame = false;

		try {
			// Play sounds -- http://goo.gl/z3pPdz
			if (SettingsFrame.isSoundEffects()) {
				if (move.isCheckmate()) { // Checkmate sound
					// http://goo.gl/BS7xBi
					ChessFrame.getChessFrame().playCheckmateSound();
				} else if (move.isCheck()) { // Check sound
					// http://goo.gl/Orq1f
					ChessFrame.getChessFrame().playCheckSound();
				} else if (move.isStalemate()) { // Stalemate sound
					// http://goo.gl/62D10
					ChessFrame.getChessFrame().playStalemateSound();
				} else if (move.getTypeOfTurn() == Move.CASTLE_LEFT || move.getTypeOfTurn() == Move.CASTLE_RIGHT) { // Castle sound
					// http://goo.gl/UzTVYB
					ChessFrame.getChessFrame().playCastleSound();
				} else if (move.getTypeOfTurn() == Move.MOVE) { // Move sound
					// http://goo.gl/pCFq2A
					ChessFrame.getChessFrame().playMoveSound();
				} else if (move.getTypeOfTurn() == Move.ATTACK) { // Attack sound
					// http://goo.gl/iMwosE
					ChessFrame.getChessFrame().playBiteSound();
				} else {
					// System.out.println("sound error - no turn type");
				}
			}
		}catch(Exception e) {
			System.out.println("Sound problem");
		}
		
		// Sets screamer text if necessary
		screamerText = null;
		if (move.isCheckmate()) {
			screamerText = "Checkmate";
			ChessFrame.getChessFrame().winner(whiteMove);
			endOfGame = true;
		} else if (move.isCheck()) {
			screamerText = "Check";
		} else if (move.isStalemate()) {
			screamerText = "Stalemate";
			endOfGame = true;
		}

		move.getFigure().setHasMoven(true);

		if (!endOfGame) {
			switchTurn();
		} else {
			ChessFrame.getChessFrame().updateBoard();
		}
	}

	/**
	 * Resets every pieces location, removes selected square Dependency: Square
	 * Date created: 10 November 2015 Last modified: 15 December 2015
	 * 
	 * @author Dennis Krasnov @param none @return none @throws
	 */
	public void resetPieceLocation() {
		for (Square[] row : grid) {
			for (Square square : row) {
				// Ensures that each figure is in its correct position
				square.setFigure(null);
				square.setUpPieces();
			}
		}
		selectedSquare = null;
	}

	public void resetTurn() {
		turn = 1;
	}

	public void setSelectedSquare(Square selectedSquare) {
		this.selectedSquare = selectedSquare;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public void setWhiteMove(boolean whiteMove) {
		this.whiteMove = whiteMove;
	}

	/**
	 * Switches turn, resets valid move cache, plays ai turn if needed, rotates
	 * board if necessary Dependency: ChessFrame, Move, Queen Date created: 10
	 * November 2015 Last modified: 10 January 2015
	 * 
	 * @author Dennis Krasnov
	 * @param move
	 *            move that is going to get performed
	 * @return none
	 * @throws Will
	 *             not play sound if move does not specify the right type
	 */
	public void switchTurn() {

		whiteMove = !whiteMove; // Sets to opposite turn

		// enemyMoves = null; // Ensures that enemy moves will be updated
		for (Square[] row : grid) { // Ensures that valid moves will be updated
			for (Square square : row) {
				if (square.getFigure() != null) {
					square.getFigure().resetValidMoves();
				}
			}
		}

		if (whiteMove) { // If turn is now white, adds 1 turn (each player
							// moves
			// = 1 turn; starts with white)
			turn++;
		}

		if ((whiteAI && whiteMove) || (blackAI && !whiteMove)) {
			aiTurn();
		} else if (SettingsFrame.isRotateBoard() && (!whiteAI && !blackAI)) {
			ChessFrame.getChessFrame().rotateBoard();
		}

	}

	/**
	 * Finds the square that contains a figure Dependency: Square, Figure Date
	 * created: 10 November 2015 Last modified: 20 December 2015
	 * 
	 * @author Dennis Krasnov
	 * @param fig
	 *            that is going to be matched with its square
	 * @return Square the square that contains the figure
	 * @throws If
	 *             no square was found, will return null
	 */
	public Square whereAmI(Figure fig) {
		for (Square[] row : grid) {
			for (Square square : row) {
				if (fig == square.getFigure()) {
					// Check if the figures are the same
					return square;
				}
			}
		}
		return null;
	}

	/**
	 * Plays out move, checks if that move is acceptable, rolls back to the
	 * board's original state Dependency: Square Date created: 4 January 2015
	 * Last modified: 10 January 2016
	 * 
	 * @author Dennis Krasnov @param move what will be validated @return boolean
	 *         is that move acceptable @throws
	 */
	public boolean isMoveValid(Move move) {
		boolean isValid = true;

		// Left castle
		if (move.getTypeOfTurn() == Move.CASTLE_LEFT) {
			// Cannot castle out of check
			if (isUnderAttack(move.getStart(), move.getFigure().isWhiteFigure())) {
				return false;
			}
			// Cannot castle into check, or jump over attacked square
			if (isUnderAttack(getNextSquare(move.getStart(), -1, 0), move.getFigure().isWhiteFigure())
					|| isUnderAttack(getNextSquare(move.getStart(), -2, 0), move.getFigure().isWhiteFigure())) {
				return false;
			}
		}
		// Right castle
		if (move.getTypeOfTurn() == Move.CASTLE_RIGHT) {
			// Cannot castle out of check
			if (isUnderAttack(move.getStart(), move.getFigure().isWhiteFigure())) {
				return false;
			}
			// Cannot castle into check, or jump over attacked square
			if (isUnderAttack(getNextSquare(move.getStart(), 1, 0), move.getFigure().isWhiteFigure())
					|| isUnderAttack(getNextSquare(move.getStart(), 2, 0), move.getFigure().isWhiteFigure())) {
				return false;
			}
		}

		// Move
		move.getStart().setFigure(null); // Clear old figure location
		move.getDestination().setFigure(move.getFigure()); // Set new figure
															// location

		// Finds my king's square
		Square myKingSquare = null;
		for (Square[] row : grid) {
			for (Square square : row) {
				if (square.getFigure() != null && square.getFigure() instanceof King
						&& square.getFigure().isWhiteFigure() == move.getFigure().isWhiteFigure()) {
					myKingSquare = square;
				}
			}
		}

		// Check if my king is under attack
		if (myKingSquare != null && isUnderAttack(myKingSquare, myKingSquare.getFigure().isWhiteFigure())) {
			isValid = false;
		} else {
			// Finds enemy king's square
			Square enemyKingSquare = null;
			for (Square[] row : grid) {
				for (Square square : row) {
					if (square.getFigure() != null && square.getFigure() instanceof King
							&& square.getFigure().isWhiteFigure() != move.getFigure().isWhiteFigure()) {
						enemyKingSquare = square;
					}
				}
			}
			// Check if enemy king is under attack, if it is: set move as check
			if (enemyKingSquare != null
					&& isUnderAttack(enemyKingSquare, enemyKingSquare.getFigure().isWhiteFigure())) {
				move.setCheck(true);
			}

			// To prevent recursion, check for checkmate and stalemate only for
			// the current player
			if (move.getFigure().isWhiteFigure() == whiteMove) {
				// List of valid enemy moves
				List<Move> enemyValidMoves = new ArrayList<Move>();
				for (Square[] row : grid) {
					for (Square square : row) {
						if (square.getFigure() != null && square.getFigure().isWhiteFigure() != whiteMove) {
							square.getFigure().resetValidMoves();
							List<Move> enemyFigureMoves = square.getFigure().getAllMoves(Move.VALID_MOVES);
							if (enemyFigureMoves != null) {
								enemyValidMoves.addAll(enemyFigureMoves);
							}
						}
					}
				}
				if (enemyValidMoves.size() == 0) {
					if (move.isCheck()) {
						// Checkmate if is also check
						move.setCheck(false);
						move.setCheckmate(true);
					} else {
						// Stalemate if enemy king is not under check
						move.setStalemate(true);
					}
				}
			}
		}

		// Roll back
		move.getStart().setFigure(move.getFigure());
		move.getDestination().setFigure(move.getEatenFigure());
		return isValid;
	}

	public String getScreamerText() {
		return screamerText;
	}

	public boolean isWhiteAI() {
		return whiteAI;
	}

	public void setWhiteAI(boolean whiteAI) {
		this.whiteAI = whiteAI;
	}

	public boolean isBlackAI() {
		return blackAI;
	}

	public void setBlackAI(boolean blackAI) {
		this.blackAI = blackAI;
	}

}
