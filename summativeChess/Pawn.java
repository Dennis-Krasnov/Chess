package summativeChess;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Figure {

	public Pawn(boolean whiteFigure) {
		super(whiteFigure, "P", false, 1);
	}

	@Override
	protected List<Move> getAllPossibilities() {
		Board board = Board.getBoard();
		Square currentSquare = board.whereAmI(this);

		List<Move> possibilities = new ArrayList<Move>();

		// Has 2 types of moves: 1 forward or 2 forward
		int vMove = 0;
		int initialVerticalPos = 0;
		if (isWhiteFigure()) {
			vMove = 1;
			initialVerticalPos = 2;
		} else {
			vMove = -1;
			initialVerticalPos = 7;
		}
		Square nextSquare = board.getNextSquare(currentSquare, 0, vMove);
		if (nextSquare != null && nextSquare.getFigure() == null) {
			addIfOnBoard(possibilities, nextSquare); // Forward
		}
		nextSquare = board.getNextSquare(currentSquare, 1, vMove);
		if (nextSquare != null) {
			addIfOnBoard(possibilities, nextSquare); // Forward-Right
		}
		nextSquare = board.getNextSquare(currentSquare, -1, vMove);
		if (nextSquare != null) {
			addIfOnBoard(possibilities, nextSquare); // Forward-Left
		}
		// If there are no figures and at initial location
		nextSquare = board.getNextSquare(currentSquare, 0, vMove + vMove);
		if (currentSquare.getVPos() == initialVerticalPos
				&& board.getNextSquare(currentSquare, 0, vMove).getFigure() == null && nextSquare.getFigure() == null) {
			addIfOnBoard(possibilities, nextSquare); // Forward-Forward
		}
		return possibilities;
	}

}
