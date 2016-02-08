package summativeChess;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class PromotionDialog extends JDialog implements ActionListener {
	// Location of move
   private Square location;
   // Instance of each figure - one of them will be inserted
   private Rook   rook   = new Rook( Board.getBoard().isWhiteMove() );
   private Bishop bishop = new Bishop( Board.getBoard().isWhiteMove() );
   private Knight knight = new Knight( Board.getBoard().isWhiteMove() );
   private Queen  queen  = new Queen( Board.getBoard().isWhiteMove() );

   public PromotionDialog( Square location ) {
	   // Creating promotion dialog
      if(Board.getBoard().isWhiteMove()) {
         setTitle( "Promotion for " + Board.getBoard().getWhitePlayer().getName() + " - Choose a figure" );
      }
      else {
         setTitle( "Promotion for " + Board.getBoard().getBlackPlayer().getName() + " - Choose a figure" );
      }
      
      this.location = location;
      JPanel pane = (JPanel)getContentPane();
      pane.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
      pane.setLayout( new GridLayout( 1, 4, 10, 10 ) );

      JButton rookButton = new JButton();
      rookButton.setIcon( rook.getFigureIcon() );
      rookButton.addActionListener( this );
      rookButton.setFocusPainted( false );
      pane.add( rookButton );

      JButton knightButton = new JButton();
      knightButton.setIcon( knight.getFigureIcon() );
      knightButton.addActionListener( this );
      knightButton.setFocusPainted( false );
      pane.add( knightButton );
      
      JButton bishopButton = new JButton();
      bishopButton.setIcon( bishop.getFigureIcon() );
      bishopButton.addActionListener( this );
      bishopButton.setFocusPainted( false );
      pane.add( bishopButton );

      JButton queenButton = new JButton();
      queenButton.setIcon( queen.getFigureIcon() );
      queenButton.addActionListener( this );
      queenButton.setFocusPainted( false );
      pane.add( queenButton );

      // Has to choose
      setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
      // Set to be always on top
      setAlwaysOnTop( true );// http://goo.gl/h3vnma
      // Makes dialog customizable
      setModal( true );
      // Disable resize
      setResizable( false );
      // Makes the dialog like an application (with swing components, etc.)
      setModalityType( ModalityType.APPLICATION_MODAL );
      // Size of dialog
      setMinimumSize( new Dimension( 300, 100 ) );
      // Centering by default
      setLocationRelativeTo( null );
      pack();
      setVisible( true );
      
   }

   @Override
   public void actionPerformed( ActionEvent e ) {
	   // If figure icon matches the the button selected's icon - insert figure into board
      Icon icon = ( (JButton)e.getSource() ).getIcon();
      if ( icon.equals( rook.getFigureIcon() ) ) {
         location.setFigure( rook );
      }
      else if(icon.equals( knight.getFigureIcon() )) {
         location.setFigure( knight );
      }
      else if(icon.equals( bishop.getFigureIcon() )) {
         location.setFigure( bishop );
      }
      else if(icon.equals( queen.getFigureIcon() )) {
         location.setFigure( queen );
      }
      // Delete frame when clicked
      dispose();
   }
}
