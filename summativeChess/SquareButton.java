package summativeChess;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;

class SquareButton extends JButton {

	// Colours for selected square
	private static final Color WHITE_SQUARE_SELECTED = new Color(127, 212, 170);
	private static final Color BLACK_SQUARE_SELECTED = new Color(51, 151, 101);
	// Colours for attacked square
	private static final Color BLACK_SQUARE_ATTACK = new Color(159, 96, 96);
	private static final Color WHITE_SQUARE_ATTACK = new Color(225, 166, 165);
	// Square that is attached to the button
	private Square square;

	public SquareButton(Square square) {
		this.square = square;
		setBorder(null);
		setFocusPainted(false);
	}

	private void drawSymbol(Figure figure) {
		setText("");
	}

	public Square getSquare() {
		return square;
	}

	/**
	 * Updates graphical element of button based on board, etc. Dependency:
	 * Square, Move Date created: 10 January 2016 Last modified: 11 January 2016
	 * 
	 * @author Dennis Krasnov @param none @return none @throws
	 */
	public void updateButton() {
		Square selectedSquare = Board.getBoard().getSelectedSquare();

		// Settig as the default board
		if (square.isBlack()) {
			setBackground(Color.GRAY);
		} else {
			setBackground(Color.WHITE);
		}
		setText("");
		setForeground(Color.BLACK);

		drawSymbol(square.getFigure());
		if (selectedSquare == square) {
			// Selected colour
			if (square.isBlack()) {
				setBackground(BLACK_SQUARE_SELECTED); // Dark green
			} else {
				setBackground(WHITE_SQUARE_SELECTED); // Light green
			}

		} else if (selectedSquare != null && selectedSquare.getFigure() != null
				&& (selectedSquare.getFigure() instanceof Figure)) {
			boolean found = false;
			for (Move move : selectedSquare.getFigure().getAllMoves(Move.VALID_MOVES)) {
				if (!found && move.getDestination() == this.getSquare() && move.getTypeOfTurn() == Move.ATTACK) {
					found = true;
				}
			}
			if (found) {
				if (SettingsFrame.isShowHighlight()) {
					// Attacked colour
					if (square.isBlack()) {
						setBackground(BLACK_SQUARE_ATTACK); // Dark red
					} else {
						setBackground(WHITE_SQUARE_ATTACK); // Light red
					}
				}
			}
			found = false;
			boolean castle = false;
			for (Move move : selectedSquare.getFigure().getAllMoves(Move.VALID_MOVES)) {
				if (!found && move.getDestination() == this.getSquare() && (move.getTypeOfTurn() == Move.MOVE
						|| move.getTypeOfTurn() == Move.CASTLE_LEFT || move.getTypeOfTurn() == Move.CASTLE_RIGHT)) {
					found = true;
					if(move.getTypeOfTurn() != Move.MOVE) {
						castle = true;
					}
				}
			}
			if (found) {
				if (SettingsFrame.isShowHighlight()) {
					// Move text
					if(castle) {
						setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
						setText("\u00D7");
					}
					else {
						setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
						setText("\u25CF");
					}
				}
			}
		}
	}

	public void setSquare(Square square) {
		this.square = square;
	}

}
