package summativeChess;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Figure {

	public Rook(boolean whiteFigure) {
		super(whiteFigure, "R", false, 4);
	}

	@Override
	protected List<Move> getAllPossibilities() {
		Board board = Board.getBoard();
		Square currentSquare = board.whereAmI(this);

		List<Move> possibilities = new ArrayList<Move>();

		addLineToList(possibilities, currentSquare, 0, 1); // Up
		addLineToList(possibilities, currentSquare, 0, -1); // Down
		addLineToList(possibilities, currentSquare, 1, 0); // Right
		addLineToList(possibilities, currentSquare, -1, 0); // Left

		return possibilities;
	}

}
