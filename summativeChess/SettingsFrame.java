package summativeChess;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.print.attribute.standard.JobHoldUntil;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class SettingsFrame extends JFrame implements ActionListener {

	// Highlight help enabled
	private static boolean showHighlight = true;
	// Sound effect enabled
	private static boolean soundEffects = true;
	// Rotate board enabled
	private static boolean rotateBoard = false;
	// Theme of the pieces
	private static String theme = "Classic";

	// Buttons for highlight
	private static JToggleButton enableHighlightButton;
	private static JToggleButton disableHighlightButton;

	// Buttons for sound effects
	private static JToggleButton enableSoundEffectsButton;
	private static JToggleButton disableSoundEffectsButton;

	// Buttons for rotate board
	private static JToggleButton enableRotateBoardButton;
	private static JToggleButton disableRotateBoardButton;

	// Button for reseting options to default
	private static JButton resetOptionsButton;

	// Dropdown for themes
	private static JComboBox themeDropDown;
	// The available themes to choose from
	private static String[] availableThemes = new String[] { "Classic", "Cats", "Eyes", "Shapy", "Skulls" };

	public static String getTheme() {
		return theme;
	}

	public static boolean isRotateBoard() {
		return rotateBoard;
	}

	public static boolean isShowHighlight() {
		return showHighlight;
	}

	public static boolean isSoundEffects() {
		return soundEffects;
	}

	/**
	 * Resets the options to default Dependency: none Date created: 10 January
	 * 2016 Last modified: 11 January 2016
	 * 
	 * @author Dennis Krasnov @param none @return none @throws
	 */
	public static void resetOptions() {
		showHighlight = true;
		soundEffects = true;
		rotateBoard = false;
		theme = "Classic";
	}

	public static void setRotateBoard(boolean rotateBoard) {
		SettingsFrame.rotateBoard = rotateBoard;
	}

	public static void setShowHighlight(boolean showHighlight) {
		SettingsFrame.showHighlight = showHighlight;
	}

	public static void setSoundEffects(boolean soundEffects) {
		SettingsFrame.soundEffects = soundEffects;
	}

	/**
	 * Updates the buttons based on the options selected, sets rotateboard
	 * buttons as either enabled or disabled dependant on who's playing
	 * Dependency: none Date created: 10 January 2016 Last modified: 11 January
	 * 2016
	 * 
	 * @author Dennis Krasnov @param none @return none @throws
	 */
	public static void updateButtons() {
		// Ensures that the frame has been initialized before calling
		if (enableHighlightButton != null) {
			// Sets button selection to options
			enableHighlightButton.setSelected(showHighlight);
			disableHighlightButton.setSelected(!showHighlight);
			enableSoundEffectsButton.setSelected(soundEffects);
			disableSoundEffectsButton.setSelected(!soundEffects);
			enableRotateBoardButton.setSelected(rotateBoard);
			disableRotateBoardButton.setSelected(!rotateBoard);
			themeDropDown.setSelectedIndex(Arrays.asList(availableThemes).indexOf(theme));
			// Enables/disables rotateboard options based on who's playing
			if (Board.getBoard().isWhiteAI() || Board.getBoard().isBlackAI()) {
				rotateBoard = false;
				enableRotateBoardButton.setEnabled(false);
				disableRotateBoardButton.setEnabled(false);
			} else {
				enableRotateBoardButton.setEnabled(true);
				disableRotateBoardButton.setEnabled(true);
			}
		}
	}

	public SettingsFrame() {
		// Setting title
		super("Settings");
		JPanel pane = (JPanel) getContentPane();
		pane.setLayout(new BorderLayout());
		// Setting space between elements
		pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// Custom icon
		setIconImage(new ImageIcon(getClass().getResource("chessIcon.png")).getImage());

		// Labels, enaable/disable buttons
		JPanel settingsPane = new JPanel();
		settingsPane.setLayout(new GridLayout(5, 3, 10, 10));

		JLabel highlightLabel = new JLabel("Highlight");
		settingsPane.add(highlightLabel);

		enableHighlightButton = new JToggleButton("Enable");
		enableHighlightButton.setSelected(showHighlight);
		enableHighlightButton.addActionListener(this);
		settingsPane.add(enableHighlightButton);

		disableHighlightButton = new JToggleButton("Disable");
		disableHighlightButton.setSelected(!showHighlight);
		disableHighlightButton.addActionListener(this);
		settingsPane.add(disableHighlightButton);

		JLabel soundEffectLabel = new JLabel("Sound Effects");
		settingsPane.add(soundEffectLabel);

		enableSoundEffectsButton = new JToggleButton(" Enable ");
		enableSoundEffectsButton.setSelected(soundEffects);
		enableSoundEffectsButton.addActionListener(this);
		settingsPane.add(enableSoundEffectsButton);

		disableSoundEffectsButton = new JToggleButton(" Disable ");
		disableSoundEffectsButton.setSelected(!soundEffects);
		disableSoundEffectsButton.addActionListener(this);
		settingsPane.add(disableSoundEffectsButton);

		JLabel rotateBoardLabel = new JLabel("Rotate Board");
		settingsPane.add(rotateBoardLabel);

		enableRotateBoardButton = new JToggleButton("  Enable  ");
		enableRotateBoardButton.setSelected(rotateBoard);
		enableRotateBoardButton.addActionListener(this);
		settingsPane.add(enableRotateBoardButton);

		disableRotateBoardButton = new JToggleButton("  Disable  ");
		disableRotateBoardButton.setSelected(!rotateBoard);
		disableRotateBoardButton.addActionListener(this);
		settingsPane.add(disableRotateBoardButton);

		JLabel themesLabel = new JLabel("Themes");
		settingsPane.add(themesLabel);

		themeDropDown = new JComboBox(availableThemes);
		themeDropDown.setSelectedIndex(Arrays.asList(availableThemes).indexOf(theme));
		themeDropDown.addActionListener(this);
		settingsPane.add(themeDropDown);

		settingsPane.add(new JLabel());

		pane.add(settingsPane, BorderLayout.NORTH);

		resetOptionsButton = new JButton("Reset to default");
		resetOptionsButton.addActionListener(this);
		pane.add(resetOptionsButton);

		if (Board.getBoard().isWhiteAI() || Board.getBoard().isBlackAI()) {
			enableRotateBoardButton.setEnabled(false);
			disableRotateBoardButton.setEnabled(false);
		} else {
			enableRotateBoardButton.setEnabled(true);
			disableRotateBoardButton.setEnabled(true);
		}

		// Size of frame
		setMinimumSize(new Dimension(350, 250));
		setPreferredSize(new Dimension(350, 250));
		// Default location to center
		setLocationRelativeTo(null);
		// Disabling resize
		setResizable(false);
		pack();
		setVisible(true);
	}

	/**
	 * If button has been clicked, switch to its option Dependency: ChessFrame
	 * Date created: 10 January 2016 Last modified: 11 January 2016
	 * 
	 * @author Dennis Krasnov @param e actionEvent @return none @throws
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		// Finds correct button (each has different spaces; don't know how to do
		// it otherwise)
		if (action.equals("Enable")) {
			showHighlight = true;
		} else if (action.equals("Disable")) {
			showHighlight = false;
		} else if (action.equals(" Enable ")) {
			soundEffects = true;
		} else if (action.equals(" Disable ")) {
			soundEffects = false;
		} else if (action.equals("  Enable  ")) {
			if (rotateBoard == false && !Board.getBoard().isWhiteMove()) {
				ChessFrame.getChessFrame().rotateBoard();
			}
			rotateBoard = true;
		} else if (action.equals("  Disable  ")) {
			if (rotateBoard == true && !Board.getBoard().isWhiteMove()) {
				ChessFrame.getChessFrame().rotateBoard();
			}
			rotateBoard = false;
		} else if (action.equals("Reset to default")) {
			// Reseting options
			resetOptions();
		} else {
			// Theme dropdown
			theme = themeDropDown.getItemAt(themeDropDown.getSelectedIndex()).toString();
			Figure.themeChanged = true;
		}
		updateButtons();
		ChessFrame.getChessFrame().updateBoard();
	}

}
