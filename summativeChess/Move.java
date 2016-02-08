package summativeChess;

public class Move {

	// Type in score format for a move that can't be executed
	public static final int INVALID_MOVE_SCORE = Integer.MIN_VALUE;

	// Allied attack type of move
	public static final int ALLIED_ATTACK = 0;
	// Move type of move
	public static final int MOVE = 1;
	// Attack type of move
	public static final int ATTACK = 2;
	// Castling type of move
	public static final int CASTLE_LEFT = 3;
	// Castling type of move
	public static final int CASTLE_RIGHT = 4;

	// Every type of moves
	public static final int ALL_POSSIBILITIES = 100;
	// Only valid type of moves
	public static final int VALID_MOVES = 101;
	// Only attackable type of moves
	public static final int ATTACKABLE_SQUARES = 102;

	// Figure that is moved
	private Figure figure;
	// Figure that is eaten (optional)
	private Figure eatenFigure;
	// Start position
	private Square start;
	// End position
	private Square destination;
	// Turn move happened
	private int turn;
	// Colour of figure
	private boolean white;
	// The type of turn, use constants, 1: Move, 2: Attack, 3: Castling, 4: En
	// passant
	private int typeOfTurn = 0;
	// Score for the turn, used for ai choice of turn
	private int score;

	// Is turn a promotion
	private boolean promotion = false;
	// Does turn check
	private boolean check = false;
	// Does turn checkmate
	private boolean checkmate = false;
	// Does turn stalemate
	private boolean stalemate = false;

	// Data constructor
	public Move(Figure figure, Square start, Square destination) {
		Board board = Board.getBoard();

		this.figure = figure;
		this.start = start;
		this.destination = destination;
		turn = board.getTurn();
		white = figure.isWhiteFigure();

		// Setting type of turn
		if (destination.getFigure() != null) {
			eatenFigure = destination.getFigure();
			if (eatenFigure.isWhiteFigure() != figure.isWhiteFigure()) {
				typeOfTurn = ATTACK;
			} else {
				typeOfTurn = ALLIED_ATTACK;
			}
		} else {
			typeOfTurn = MOVE;
		}

		// Setting type of attack for pawn
		if (figure instanceof Pawn && start.getHPos() != destination.getHPos()) {
			if (destination.getFigure() != null && destination.getFigure().isWhiteFigure() != figure.isWhiteFigure()) {
				typeOfTurn = ATTACK;
			} else {
				typeOfTurn = ALLIED_ATTACK;
			}
		}

		// Setting promotion
		if (figure instanceof Pawn && (destination.getVPos() == 8 && figure.isWhiteFigure()
				|| destination.getVPos() == 1 && !figure.isWhiteFigure())) {
			promotion = true;
		}

	}

	// String constructor (from history)
	public Move(String line) {

		// Is figure white or not
		if (line.charAt(1) == ' ') {
			white = false;
		} else {
			white = true;
		}

		// Index of dash (between 2 locations)
		int dash = line.indexOf('-');

		// Castling
		if (line.length() >= dash + 4 && line.substring(dash - 1, dash + 4).equals("0-0-0")) {
			typeOfTurn = CASTLE_LEFT;
			if(white) {
				start = Board.getBoard().getSquare(5, 1);
				destination = Board.getBoard().getSquare(3, 1);
			}
			else {
				start = Board.getBoard().getSquare(5, 8);
				destination = Board.getBoard().getSquare(3, 8);
			}
		} else if (line.substring(dash - 1, dash + 2).equals("0-0")) {
			typeOfTurn = CASTLE_RIGHT;
			if(white) {
				start = Board.getBoard().getSquare(5, 1);
				destination = Board.getBoard().getSquare(7, 1);
			}
			else {
				start = Board.getBoard().getSquare(5, 8);
				destination = Board.getBoard().getSquare(7, 8);
			}
		} else {
			// Start location
			int h = returnColumbFromLetter(line.charAt(dash - 2));
			int v = Integer.parseInt("" + line.charAt(dash - 1));

			start = Board.getBoard().getSquare(h, v);

			// End location
			h = returnColumbFromLetter(line.charAt(dash + 1));
			v = Integer.parseInt("" + line.charAt(dash + 2));

			destination = Board.getBoard().getSquare(h, v);
		}

		// Setting type of turns (only 1 at a time)
		if (line.charAt(line.length() - 1) == '#') {
			checkmate = true;
		} else if (line.charAt(line.length() - 1) == '+') {
			check = true;
		} else if (line.charAt(line.length() - 1) == '=') {
			stalemate = true;
		} else if (destination.getFigure() != null) {
			typeOfTurn = ATTACK;
		} else if (typeOfTurn != CASTLE_LEFT && typeOfTurn != CASTLE_RIGHT) {
			typeOfTurn = MOVE;
		}

		// Setting figure, turn, eaten figure if any
		figure = start.getFigure();
		turn = Board.getBoard().getTurn();
		if (destination.getFigure() != null) {
			eatenFigure = destination.getFigure();
		}
	}

	public Square getDestination() {
		return destination;
	}

	public Figure getFigure() {
		return figure;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Square getStart() {
		return start;
	}

	public int getTurn() {
		return turn;
	}

	public int getTypeOfTurn() {
		return typeOfTurn;
	}

	public boolean isCheckmate() {
		return checkmate;
	}

	public boolean isPromotion() {
		return promotion;
	}

	public boolean isWhite() {
		return white;
	}

	/**
	 * Converts a character into number form Dependency: none Date created: 1
	 * December 2015 Last modified: 5 December 2015
	 * 
	 * @author Dennis Krasnov
	 * @param letter
	 *            that will be converted
	 * @return int number that corresponds with letter
	 * @throws returns
	 *             0 if invalid character is given
	 */
	private int returnColumbFromLetter(char letter) {
		switch (letter) {
		case 'a':
			return 1;
		case 'b':
			return 2;
		case 'c':
			return 3;
		case 'd':
			return 4;
		case 'e':
			return 5;
		case 'f':
			return 6;
		case 'g':
			return 7;
		case 'h':
			return 8;
		default:
			return 0;
		}

	}

	public void setTypeOfTurn(int typeOfTurn) {
		this.typeOfTurn = typeOfTurn;
	}

	public Figure getEatenFigure() {
		return eatenFigure;
	}

	/**
	 * To export move to history Dependency: none Date created: 1 December 2015
	 * Last modified: 5 December 2015
	 * 
	 * @author Dennis Krasnov
	 * @param none
	 * @return String move in string
	 * @throws none
	 */
	public String toString() {
		String moveText = "";
		// Empty space before turn that is 1-9
		if (turn < 10) {
			moveText += ' ';
		}

		if (typeOfTurn == CASTLE_LEFT) {
			// Castle left
			if (white) {
				moveText += turn + ": " + "0-0-0";
			} else {
				int spaces = Integer.toString(turn).length();
				for (int i = 1; i <= spaces; i++) {
					moveText += ' ';
				}
				moveText += "  " + "0-0-0";
			}
		} else if (typeOfTurn == CASTLE_RIGHT) {
			// Castle right
			if (white) {
				moveText += turn + ": " + "0-0";
			} else {
				int spaces = Integer.toString(turn).length();
				for (int i = 1; i <= spaces; i++) {
					moveText += ' ';
				}
				moveText += "  " + "0-0";
			}
		} else {
			// Assigns start, end, dashes, etc.
			if (white) {
				moveText += turn + ": " + figure.getCode() + " " + start.getHName() + start.getVPos() + "-"
						+ destination.getHName() + destination.getVPos();
			} else {
				int spaces = Integer.toString(turn).length();
				for (int i = 1; i <= spaces; i++) {
					moveText += ' ';
				}
				moveText += "  " + figure.getCode() + " " + start.getHName() + start.getVPos() + "-"
						+ destination.getHName() + destination.getVPos();
			}
		}

		// Adds type of turn if any
		if (check) {
			moveText += "+";
		} else if (checkmate) {
			moveText += "#";
		} else if (check) {
			moveText += "+";
		} else if (stalemate) {
			moveText += "=";
		}
		return moveText;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public void setCheckmate(boolean checkmate) {
		this.checkmate = checkmate;
	}

	public boolean isStalemate() {
		return stalemate;
	}

	public void setStalemate(boolean stalemate) {
		this.stalemate = stalemate;
	}

}
