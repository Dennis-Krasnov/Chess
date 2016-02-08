package summativeChess;

import java.util.List;

public class Square {
	private int hPos;
	private int vPos;
	private Figure figure;

	public Square(int hPos, int vPos) {
		this.hPos = hPos;
		this.vPos = vPos;
	}

	public Figure getFigure() {
		return figure;
	}

	public char getHName() {
		return Board.LETTERS[hPos];
	}

	public int getHPos() {
		return hPos;
	}

	public int getVPos() {
		return vPos;
	}

	public boolean isBlack() {
		boolean evenRow = vPos % 2 == 0;
		boolean evenCol = hPos % 2 == 0;
		return !(evenRow ^ evenCol);
	}

	/**
	 * Does action based on what was clicked and the state of the board
	 * Dependency: Board, Move, Figure Date created: 10 January 2016 Last
	 * modified: 11 January 2016
	 * 
	 * @author Dennis Krasnov @param none @return none @throws
	 */
	public void onClick() {
		// Instance of board
		Board board = Board.getBoard();
		// Already selected square
		Square alreadySelected = board.getSelectedSquare();
		if (alreadySelected != null) {
			// Unselect when click on an already selected square
			if (this == alreadySelected) {
				board.setSelectedSquare(null);
			} else {
				// Move figure
				Figure fig = alreadySelected.getFigure();
				Move myMove = null;
				for (Move move : fig.getAllMoves(Move.VALID_MOVES)) {
					if (myMove == null && move.getDestination().equals(this)) {
						myMove = move;
					}
				}
				if (myMove != null) {
					// Goes through with the turn
					board.performMove(myMove);
				} else {
					if (figure != null && figure.isWhiteFigure() == board.isWhiteMove()
							&& (fig.getAllMoves(Move.VALID_MOVES) != null)) {
						// Switches selected
						board.setSelectedSquare(this);
					} else {
						// Cancels selected
						board.setSelectedSquare(null);
					}
				}
			}
		} else if (figure != null && figure.isWhiteFigure() == board.isWhiteMove()) {
			// Switch selected if one has been already selected
			board.setSelectedSquare(this);
			if (figure.getAllMoves(Move.VALID_MOVES) == null) {
				board.setSelectedSquare(null);
			}
		}
	}

	public void setFigure(Figure figure) {
		this.figure = figure;
	}

	public void setHPos(int hPos) {
		this.hPos = hPos;
	}

	/**
	 * Sets the correct figure for the square Dependency: (every) Figure
	 * Date created: 10 January 2016 Last modified: 11 January 2016
	 * 
	 * @author Dennis Krasnov @param none @return none @throws
	 */
	public void setUpPieces() {
		// Pawns
		int v = getVPos();
		int h = getHPos();
		if (v == 2 && h >= 1 && h <= 8) {
			setFigure(new Pawn(true));
		} else if (v == 7 && h >= 1 && h <= 8) {
			setFigure(new Pawn(false));
		}
		// Rooks
		else if (v == 1 && (h == 1 || h == 8)) {
			setFigure(new Rook(true));
		} else if (v == 8 && (h == 1 || h == 8)) {
			setFigure(new Rook(false));
		}
		// Bishops
		else if (v == 1 && (h == 3 || h == 6)) {
			setFigure(new Bishop(true));
		} else if (v == 8 && (h == 3 || h == 6)) {
			setFigure(new Bishop(false));
		}
		// Knights
		else if (v == 1 && (h == 2 || h == 7)) {
			setFigure(new Knight(true));
		} else if (v == 8 && (h == 2 || h == 7)) {
			setFigure(new Knight(false));
		}
		// Queens
		else if (v == 1 && h == 4) {
			setFigure(new Queen(true));
		} else if (v == 8 && h == 4) {
			setFigure(new Queen(false));
		}
		// Kings
		else if (v == 1 && h == 5) {
			setFigure(new King(true));
		} else if (v == 8 && h == 5) {
			setFigure(new King(false));
		}
	}

	public void setVPos(int vPos) {
		this.vPos = vPos;
	}

	public boolean squareIsInList(List<Square> listOfSquares) {
		for (Square r : listOfSquares) {
			if (r.hPos == hPos && r.vPos == vPos) {
				return true;
			}
		}
		return false;
	}

}
