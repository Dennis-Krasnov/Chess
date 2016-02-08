package summativeChess;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Queen extends Figure {

   public Queen( boolean whiteFigure ) {
      super( whiteFigure, "Q", false, 8 );
   }
   
   @Override
   protected List<Move> getAllPossibilities() {
      Board board = Board.getBoard();
      Square currentSquare = board.whereAmI( this );

      List<Move> possibilities = new ArrayList<Move>();

      addLineToList( possibilities, currentSquare, 0, 1 ); // Up
      addLineToList( possibilities, currentSquare, 0, -1 ); // Down
      addLineToList( possibilities, currentSquare, 1, 0 ); // Right
      addLineToList( possibilities, currentSquare, -1, 0 ); // Left

      addLineToList( possibilities, currentSquare, 1, 1 ); // Up-Right
      addLineToList( possibilities, currentSquare, -1, 1 ); // Up-Left
      addLineToList( possibilities, currentSquare, 1, -1 ); // Down-Right
      addLineToList( possibilities, currentSquare, -1, -1 ); // Down-Left
      return possibilities;
   }
}
