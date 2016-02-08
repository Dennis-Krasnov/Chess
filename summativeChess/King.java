package summativeChess;

import java.util.ArrayList;
import java.util.List;

public class King extends Figure {

	public King(boolean whiteFigure) {
		super(whiteFigure, "K", true, 16);
	}

	@Override
	protected List<Move> getAllPossibilities() {
		Board board = Board.getBoard();
		Square currentSquare = board.whereAmI(this);

		List<Move> possibilities = new ArrayList<Move>();

		addIfOnBoard(possibilities, board.getNextSquare(currentSquare, 0, 1)); // Up
		addIfOnBoard(possibilities, board.getNextSquare(currentSquare, 1, 1)); // Up-Right
		addIfOnBoard(possibilities, board.getNextSquare(currentSquare, -1, 1)); // Up-Left
		addIfOnBoard(possibilities, board.getNextSquare(currentSquare, 1, 0)); // Right
		addIfOnBoard(possibilities, board.getNextSquare(currentSquare, -1, 0)); // Left
		addIfOnBoard(possibilities, board.getNextSquare(currentSquare, 1, -1)); // Down-Right
		addIfOnBoard(possibilities, board.getNextSquare(currentSquare, -1, -1)); // Down-Left
		addIfOnBoard(possibilities, board.getNextSquare(currentSquare, 0, -1)); // Down

		// Castling
		if (!isHasMoven() && (currentSquare == board.getSquare(5, 1) || currentSquare == board.getSquare(5, 8))) {
			// Left castle
			Figure leftRook = board.getNextSquare(currentSquare, -4, 0).getFigure();
			if (leftRook != null && !leftRook.isHasMoven() && leftRook instanceof Rook) {
				// Check for empty squares
				if (board.getNextSquare(currentSquare, -1, 0).getFigure() == null
						&& board.getNextSquare(currentSquare, -2, 0).getFigure() == null
						&& board.getNextSquare(currentSquare, -3, 0).getFigure() == null) {
					// Adding if qualifies
					Move castleLeftMove = new Move(this, currentSquare,
							board.getNextSquare(currentSquare, -2, 0));
					castleLeftMove.setTypeOfTurn(Move.CASTLE_LEFT);
					possibilities.add(castleLeftMove);
				}

			}
			// Right castle
			Figure rightRook = board.getNextSquare(currentSquare, 3, 0).getFigure();
			if (rightRook != null && !rightRook.isHasMoven() && rightRook instanceof Rook) {
				// Check for empty squares
				if (board.getNextSquare(currentSquare, 1, 0).getFigure() == null
						&& board.getNextSquare(currentSquare, 2, 0).getFigure() == null) {
					// Checking for attacks

					// Adding if qualifies
					Move castleRightMove = new Move(this, currentSquare,
							board.getNextSquare(currentSquare, 2, 0));
					castleRightMove.setTypeOfTurn(Move.CASTLE_RIGHT);
					possibilities.add(castleRightMove);
				}
			}

		}

		return possibilities;
	}

}
