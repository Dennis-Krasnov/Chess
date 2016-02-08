/**
 * Figure class which all figures extend from
 * Date created: 10 November 2015
 * Last modified: 21 December 2015
 * @author Dennis Krasnov
 */
package summativeChess;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public abstract class Figure {

	// To check if figure's icon needs to be updated
	public static boolean themeChanged = false;

	// Has figure moved yet, used to check if castle is valid
	private boolean hasMoven = false;
	
	// The colour of the figure in boolean
	private boolean whiteFigure;
	// The letter for each figure
	private String code;
	// Is the figure required to not loose (king)
	private boolean isImportant;
	// Icon for figure, displayed on board
	private ImageIcon icon;
	// The score for the figure, used to find which turns are best
	private int score;
	// All valid moves for this figure for cache
	private List<Move> validMoves;

	public Figure(boolean whiteFigure, String code, boolean isImportant, int score) {
		this.whiteFigure = whiteFigure;
		this.code = code;
		this.isImportant = isImportant;
		this.score = score;
	}

	public boolean isWhiteFigure() {
		return whiteFigure;
	}

	public boolean isImportant() {
		return isImportant;
	}

	public String getCode() {
		return code;
	}

	/**
	 * Different for each figure, will return a list of moves that include
	 * everything (including invalids, etc.) Dependency: none Date created: 10
	 * November 2015 Last modified: 21 December 2015
	 * 
	 * @author Dennis Krasnov
	 * @param none
	 * @return List<Move> all possibilities for a figure's move
	 * @throws none
	 */
	protected abstract List<Move> getAllPossibilities();

	/**
	 * Will return a specified type of moves for a figure Dependency: List, Move
	 * Date created: 10 November 2015 Last modified: 21 December 2015
	 * 
	 * @author Dennis Krasnov
	 * @param type
	 *            which kind of moves to return
	 * @return List<Move> moves of a certain type for a figure
	 * @throws If
	 *             an invalid type was given, null will be returned
	 */
	public List<Move> getAllMoves(int type) {
		if (Board.getBoard().endOfGame) {
			return null;
		}
		List<Move> posibilities = getAllPossibilities();
		if (type == Move.ALL_POSSIBILITIES) {
			return posibilities;
		} else if (type == Move.VALID_MOVES) {
			// Caching valid moves, as this way it won't have to be recalcuated
			// every time
			if (validMoves == null) {
				validMoves = extractValidMoves(posibilities);
			}
			return validMoves;
		} else if (type == Move.ATTACKABLE_SQUARES) {
			return extractAttackableSquares(posibilities);
		} else {
			System.out.println("Unknown type: " + type);
			return null;
		}
	}

	/**
	 * Will return only valid moves from list of moves Dependency: Move, List
	 * Date created: 10 November 2015 Last modified: 21 December 2015
	 * 
	 * @author Dennis Krasnov
	 * @param allPossibilites
	 *            every possible move for figure
	 * @return List<Move> valid moves for figure
	 * @throws none
	 */
	private List<Move> extractValidMoves(List<Move> allPossibilities) {
		List<Move> valid = new ArrayList<Move>();
		for (Move move : allPossibilities) {
			if (move.getTypeOfTurn() != Move.ALLIED_ATTACK) {
				if (Board.getBoard().isMoveValid(move)) {
					move.setScore(score);
					valid.add(move);
				}
			}
		}
		return valid;
	}

	/**
	 * Will return only moves that attack anything from list of moves (including
	 * allied figures) Dependency: Move, List Date created: 10 November 2015
	 * Last modified: 21 December 2015
	 * 
	 * @author Dennis Krasnov
	 * @param allPossibilites
	 *            every possible move for figure
	 * @return List<Move> moves that attack anything for figure
	 * @throws none
	 */
	private List<Move> extractAttackableSquares(List<Move> allPossibilities) {
		List<Move> attackable = new ArrayList<Move>();
		for (Move move : allPossibilities) {
			if (move.getTypeOfTurn() == Move.ATTACK || move.getTypeOfTurn() == Move.ALLIED_ATTACK
					|| (move.getTypeOfTurn() == Move.MOVE && move.getFigure().getCode() != "P")) {
				attackable.add(move);
			}
		}
		return attackable;
	}

	/**
	 * Checks if square is on the board and inserts it into list if it is
	 * Dependency: List, Square Date created: 10 November 2015 Last modified: 21
	 * December 2015
	 * 
	 * @author Dennis Krasnov
	 * @param list
	 *            which might be added onto
	 * @param square
	 *            the square that's checked if its on the board
	 * @return none
	 * @throws none
	 */
	protected void addIfOnBoard(List<Move> list, Square square) {
		if (square != null) {
			list.add(new Move(this, Board.getBoard().whereAmI(this), square));
		}
	}

	/**
	 * Gets the always updated figure icon for the figure Dependency: ImageIcon,
	 * SettingsFrame Date created: 10 November 2015 Last modified: 20 December
	 * 2015
	 * 
	 * @author Dennis Krasnov
	 * @param none
	 * @return ImageIcon the icon of the figure
	 * @throws If
	 *             there is no icon or the theme has changed, it will fetch the
	 *             new icon, otherwise it will simply fetch to save resources
	 */
	public ImageIcon getFigureIcon() {
		if (icon == null || themeChanged) {
			// Putting together the file name
			String filename = "/summativeChess/img/" + SettingsFrame.getTheme().toLowerCase() + "/";
			if (whiteFigure) {
				filename += "w";
			} else {
				filename += "b";
			}
			filename += code.toLowerCase() + ".png";
			// Setting new icon
			icon = new ImageIcon(getClass().getResource(filename));
		}
		return icon;
	}

	/**
	 * From a position and vector path, will go in that path until collision
	 * adding moves, WILL include move with last figure (if there is one) NO
	 * MATTER what side its from Dependency: Board, Square, ArrayList Date
	 * created: 10 November 2015 Last modified: 20 December 2015
	 * 
	 * @author Dennis Krasnov
	 * @param none
	 * @return none
	 * @throws Won't
	 *             go outside of board
	 */
	protected void addLineToList(List<Move> possibilities, Square currentSquare, int moveH, int moveV) {
		// Instance of board
		Board board = Board.getBoard();
		// Used to go on until path is no longer clear
		boolean pathIsClear = true;
		// Position of square
		Square square = currentSquare;
		while (pathIsClear) {
			// Moves position of square by specified x, y
			square = board.getNextSquare(square, moveH, moveV);
			// To stop from going outside of board
			if (square != null) {
				possibilities.add(new Move(currentSquare.getFigure(), currentSquare, square));
				// To go on while squares are not occupied
				pathIsClear = square.getFigure() == null;
			} else {
				pathIsClear = false;
			}
		}
	}

	public int getScore() {
		return score;
	}

	public void resetValidMoves() {
		validMoves = null;
	}
	
	protected boolean isHasMoven() {
		return hasMoven;
	}

	protected void setHasMoven(boolean hasMoven) {
		this.hasMoven = hasMoven;
	}

}
