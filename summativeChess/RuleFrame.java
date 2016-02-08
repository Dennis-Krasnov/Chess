package summativeChess;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class RuleFrame extends JFrame {
   public RuleFrame() {
	  // Setting the title
      super( "Rules" );
      JPanel pane = (JPanel)getContentPane();
      // Padding between elements
      pane.setLayout( new BorderLayout( 10, 0 ) );
      // Padding around border
      pane.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );

      // Custom image
      setIconImage( new ImageIcon( getClass().getResource( "chessIcon.png" ) ).getImage() );

      // Giant line of rules of chess from http://goo.gl/1S5dx, formatted with online Java string generator
      JTextArea textArea = new JTextArea(
       "The Goal of Chess\n-----------------------\nChess is a game played between two opponents on opposite sides of a board containing 64 squares of alternating colors. Each player has 16 pieces: 1 king, 1 queen, 2 rooks, 2 bishops, 2 knights, and 8 pawns. The goal of the game is to checkmate the other king. Checkmate happens when the king is in a position to be captured (in check) and cannot escape from capture. \n\nStarting a Game\n-----------------------\nAt the beginning of the game the chessboard is laid out so that each player has the white (or light) color square in the bottom right-hand side. The chess pieces are then arranged the same way each time. The second row (or rank) is filled with pawns. The rooks go in the corners, then the knights next to them, followed by the bishops, and finally the queen, who always goes on her own matching color (white queen on white, black queen on black), and the king on the remaining square. \nThe player with the white pieces always moves first. Therefore, players generally decide who will get to be white by chance or luck such as flipping a coin or having one player guess the color of the hidden pawn in the other player's hand. White then makes a move, followed by black, then white again, then black and so on until the end of the game. \n\nHow the Pieces Move\n----------------------------\nEach of the 6 different kinds of pieces moves differently. Pieces cannot move through other pieces (though the knight can jump over other pieces), and can never move onto a square with one of their own pieces. However, they can be moved to take the place of an opponent's piece which is then captured. Pieces are generally moved into positions where they can capture other pieces (by landing on their square and then replacing them), defend their own pieces in case of capture, or control important squares in the game. \n\nThe King\n------------\nThe king is the most important piece, but is one of the weakest. The king can only move one square in any direction - up, down, to the sides, and diagonally. Click on the '>' button in the diagram below to see how the king can move around the board. The king may never move himself into check (where he could be captured).\n\nThe Queen\n---------------\nThe queen is the most powerful piece. She can move in any one straight direction - forward, backward, sideways, or diagonally - as far as possible as long as she does not move through any of her own pieces. And, like with all pieces, if the queen captures an opponent's piece her move is over. Click through the diagram below to see how the queens move. Notice how the white queen captures the black queen and then the black king is forced to move.\n\nThe Rook\n-------------\nThe rook may move as far as it wants, but only forward, backward, and to the sides. The rooks are particularly powerful pieces when they are protecting each other and working together!\n\nThe Bishop\n---------------\nThe bishop may move as far as it wants, but only diagonally. Each bishop starts on one color (light or dark) and must always stay on that color. Bishops work well together because they cover up each other\u2019s weaknesses.\n\nThe Knight\n--------------\nKnights move in a very different way from the other pieces \u2013 going two squares in one direction, and then one more move at a 90 degree angle, just like the shape of an \u201CL\u201D. Knights are also the only pieces that can move over other pieces.\n\nThe Pawn\n-------------\nPawns are unusual because they move and capture in different ways: they move forward, but capture diagonally. Pawns can only move forward one square at a time, except for their very first move where they can move forward two squares. Pawns can only capture one square diagonally in front of them. They can never move or capture backwards. If there is another piece directly in front of a pawn he cannot move past or capture that piece.\n\nPromotion\n--------------\nPawns have another special ability and that is that if a pawn reaches the other side of the board it can become any other chess piece (called promotion). A pawn may be promoted to any piece. [NOTE: A common misconception is that pawns may only be exchanged for a piece that has been captured. That is NOT true.] A pawn is usually promoted to a queen. Only pawns may be promoted.\n\nEn Passant\n---------------\nThe last rule about pawns is called \u201Cen passant,\u201D which is French for \u201Cin passing\u201D. If a pawn moves out two squares on its first move, and by doing so lands to the side of an opponent\u2019s pawn (effectively jumping past the other pawn\u2019s ability to capture it), that other pawn has the option of capturing the first pawn as it passes by. This special move must be done immediately after the first pawn has moved past, otherwise the option to capture it is no longer available. Click through the example below to better understand this odd, but important rule.\n1. e4 White moves a pawn, trying to move past black's pawn. That black pawn now can capture this pawn, but must do it on this NEXT move or lose the opportunity. \n1... dxe3 2. dxe3 e5 White can now capture the black pawn via en passant. \n3. fxe6 fxe6 4. g4 g5 5. h3 Black cannot capture the pawn en passant now - the chance passed last move. \n5... b5 6. axb6 axb6 \n\nCastling\n----------\nOne other special rule is called castling. This move allows you to do two important things all in one move: get your king to safety (hopefully), and get your rook out of the corner and into the game. On a player\u2019s turn he may move his king two squares over to one side and then move the rook from that side\u2019s corner to right next to the king on the opposite side. (See the example below.) However, in order to castle, the following conditions must be met: \n\u2022\u0009it must be that king\u2019s very first move\n\u2022\u0009it must be that rook\u2019s very first move\n\u2022\u0009there cannot be any pieces between the king and rook to move\n\u2022\u0009the king may not be in check or pass through check\nNotice that when you castle one direction the king is closer to the side of the board. That is called castling kingside. Castling to the other side, through where the queen sat, is called castling queenside. Regardless of which side, the king always moves only two squares when castling. \n\nCheck & Checkmate\n---------------------------\nAs stated before, the purpose of the game is to checkmate the opponent\u2019s king. This happens when the king is put into check and cannot get out of check. There are only three ways a king can get out of check: move out of the way (though he cannot castle!), block the check with another piece, or capture the piece threatening the king. If a king cannot escape checkmate then the game is over. Customarily the king is not captured or removed from the board, the game is simply declared over.\n\nDraws \n---------\nOccasionally chess games do not end with a winner, but with a draw. There are 5 reasons why a chess game may end in a draw: \n\u2022\u0009The position reaches a stalemate where it is one player\u2019s turn to move, but his king is NOT in check and yet he does not have another legal move \n\u2022\u0009The players may simply agree to a draw and stop playing \n\u2022\u0009There are not enough pieces on the board to force a checkmate (example: a king and a bishop vs.a king) \n\u2022\u0009A player declares a draw if the same exact position is repeated three times (though not necessarily three times in a row) \n\u2022\u0009Fifty consecutive moves have been played where neither player has moved a pawn or captured a piece.\n" );
            
      // Setting properties of scrollable textarea
      textArea.setMargin( new Insets( 5, 5, 5, 5 ) );
      textArea.setEditable( false );
      textArea.setWrapStyleWord( true );
      textArea.setLineWrap( true );
      textArea.setTabSize( 1 );

      JScrollPane scrollPane = new JScrollPane( textArea );
      scrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
      pane.add( scrollPane, BorderLayout.CENTER );

      // Setting size of the frame
      setMinimumSize( new Dimension( 600, 500 ) );
      setPreferredSize( new Dimension( 600, 500 ) );
      // Default position
      setLocationRelativeTo( null );
      // Disabling resize
      setResizable( false );
      pack();
      setVisible( true );
   }
}
