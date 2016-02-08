/**
 * Frame which contains information about the program and project
 * Date created: 10 November 2015
 * Last modified: 21 December 2015
 * @author Dennis Krasnov
 */
package summativeChess;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class AboutFrame extends JFrame {
	public AboutFrame() {
		// Setting the title
		super("About");
		JPanel pane = (JPanel) getContentPane();
		pane.setLayout(new BorderLayout());
		// Padding around border
		pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// Custom icon
		setIconImage(new ImageIcon(getClass().getResource("chessIcon.png")).getImage());

		JTextArea textArea = new JTextArea(
				"Chess Game\n------------------\n\nDennis Krasnov\n\nBanting S.S.\nICS3U1\n2016\nMr.McKay\n\nRequires Java 8 to run\nCreated with eclipse mars");

		// Setting properties of scrollable textarea
		textArea.setMargin(new Insets(5, 5, 5, 5));
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setTabSize(1);

		 pane.add(textArea, BorderLayout.CENTER);

		// Size of the frame
		setMinimumSize(new Dimension(250, 260));
		// Center the frame on start
		setLocationRelativeTo(null);
		// Disable resize
		setResizable(false);
		pack();
		setVisible(true);
	}
}
