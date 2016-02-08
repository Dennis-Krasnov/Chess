/**
 * Frame where game is played, other frames are opened
 * Date created: 10 November 2015
 * Last modified: 21 December 2015
 * @author Dennis Krasnov
 */
package summativeChess;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;

public class ChessFrame extends JFrame implements ActionListener {

	// Background colour of white square that can move
	private static final Color WHITE_SQUARE_HASMOVES = new Color(165, 195, 225);

	// Background colour of black square that can move
	private static final Color BLACK_SQUARE_HASMOVES = new Color(70, 131, 193);

	// Instance of ChessFrame
	private static ChessFrame chessGame;

	// Turn timer (optional)
	private static Timer timer;

	public static ChessFrame getChessFrame() {
		return chessGame;
	}

	public static void main(String[] args) {

		chessGame = new ChessFrame();

		chessGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// Where the game is played
	private JPanel boardPanel;
	// The swappable side panel next to the game
	private JPanel sidePanel;
	// The announcer above the history
	private JTextField screamer;
	// The history of the turns
	private JTextArea history;
	// The dynamic layout of sidePanel
	private CardLayout sidePanelLayout;
	// The timer and names of the players
	private JPanel scorePanel;
	// The time and or name of the white player
	private JLabel player1TimerLabel;
	// The time and or name of the black player
	private JLabel player2TimerLabel;
	// Input for white player's name
	private JTextField whiteNameField;
	// Input for black player's name
	private JTextField blackNameField;
	// Input spinner specifying how long each timer should be
	private JSpinner timerSpinner;

	// Difficulty section for white player in new side panel
	private JPanel whitePlayerDifficultyPane;
	// Difficulty section for black player in new side panel
	private JPanel blackPlayerDifficultyPane;
	// Difficulty input for white CPU
	private JSlider whiteDifficultySlider;
	// Difficulty input for white CPU
	private JSlider blackDifficultySlider;

	// Replay that plays out turns
	private Replay replayRunner;

	// Sound clip for moving
	private Clip moveSound = openClip("move");

	// Sound clip for eating
	private Clip biteSound = openClip("bite");

	// Sound clip for checkmate
	private Clip checkmateSound = openClip("tadaa");

	// Sound clip for check
	private Clip checkSound = openClip("alarm");

	// Sound clip for stalemate
	private Clip stalemateSound = openClip("trombone");

	// Sound clip for castling
	private Clip castlingSound = openClip("castle");

	// Thread that runs the replay
	private Thread replayThread;

	// Is white player an AI input
	private JCheckBox whiteAiCheck;
	// Is black player an AI input
	private JCheckBox blackAiCheck;

	// Instance of SettingsFrame
	private SettingsFrame settings;

	public ChessFrame() {
		// Setting the title
		super("Chess by Dennis Krasnov");

		JPanel pane = (JPanel) getContentPane();
		JPanel leftPane = new JPanel();
		// Padding in between elements
		leftPane.setLayout(new BorderLayout(10, 10));
		// Padding around border
		leftPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Custom icon
		setIconImage(new ImageIcon(getClass().getResource("chessIcon.png")).getImage());

		// The menu for the program

		JMenuBar menuBar = new JMenuBar();

		JMenu file = new JMenu("File");

		JMenuItem newGame = new JMenuItem("New");
		newGame.addActionListener(this);
		file.add(newGame);

		file.addSeparator();

		JMenuItem openGame = new JMenuItem("Open");
		openGame.addActionListener(this);
		file.add(openGame);

		JMenuItem saveGame = new JMenuItem("Save");
		saveGame.addActionListener(this);
		file.add(saveGame);

		file.addSeparator();

		JMenuItem exitGame = new JMenuItem("Exit");
		exitGame.addActionListener(this);
		file.add(exitGame);

		menuBar.add(file);

		JMenu options = new JMenu("Options");

		JMenuItem settings = new JMenuItem("Settings");
		settings.addActionListener(this);
		options.add(settings);

		menuBar.add(options);

		JMenu help = new JMenu("Help");

		JMenuItem rules = new JMenuItem("Rules");
		rules.addActionListener(this);
		help.add(rules);

		JMenuItem about = new JMenuItem("About");
		about.addActionListener(this);
		help.add(about);

		menuBar.add(help);

		setJMenuBar(menuBar);

		// The panel containing timer and or player's name

		scorePanel = new JPanel();
		scorePanel.setLayout(new GridLayout(1, 2));
		// Padding in between elements
		scorePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Instance of the players

		Player p1 = Board.getBoard().getWhitePlayer();
		Player p2 = Board.getBoard().getBlackPlayer();

		// Action that happens every second
		ActionListener playerTaskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (timer.isRunning()) {
					// If seconds == 0, remove minute, set to 60 seconds
					// Remove second
					// If timer was enabled and minutes and seconds reach 0,
					// other player wins
					if (Board.getBoard().isWhiteMove()) {
						if (p1.getMinutes() > 0 || (p1.getMinutes() == 0 && p1.getSeconds() > 0)) {
							if (p1.getSeconds() == 0 && p1.getMinutes() > 0) {
								p1.setSeconds(60);
								p1.setMinutes(p1.getMinutes() - 1);
							}
							p1.setSeconds(p1.getSeconds() - 1);
						} else if (p1.getMinutes() == 0 && p1.getSeconds() == 0) {
							timer.stop();
							Board.getBoard().endOfGame = true;
							winner(false);
						}
						setTimerText();
					} else {
						if (p2.getMinutes() > 0 || (p2.getMinutes() == 0 && p2.getSeconds() > 0)) {
							if (p2.getSeconds() == 0 && p2.getMinutes() > 0) {
								p2.setSeconds(60);
								p2.setMinutes(p2.getMinutes() - 1);
							}
							p2.setSeconds(p2.getSeconds() - 1);
						} else if (p2.getMinutes() == 0 && p2.getSeconds() == 0) {
							timer.stop();
							Board.getBoard().endOfGame = true;
							winner(true);
						}
						setTimerText();
					}
					if (p1.getMinutes() < 0 || p2.getMinutes() < 0) {
						timer.stop();
						player1TimerLabel.setText(p1.getName());
						player2TimerLabel.setText(p2.getName());
					}
				}
			}
		};

		player1TimerLabel = new JLabel();

		player1TimerLabel.setFont(new Font("Monospaced", Font.PLAIN, 22));
		scorePanel.add(player1TimerLabel);

		timer = new Timer(1000, playerTaskPerformer);
		timer.start();

		player2TimerLabel = new JLabel();

		player2TimerLabel.setFont(new Font("Monospaced", Font.PLAIN, 22));

		scorePanel.add(player2TimerLabel);

		leftPane.add(scorePanel, BorderLayout.NORTH);

		// Panel containing the game itself
		boardPanel = new JPanel();
		// 8x8 grid
		boardPanel.setLayout(new GridLayout(8, 8));
		// Padding around border
		boardPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		boardPanel.setBackground(Color.BLACK);

		setUpBoard();

		updateBoard();

		leftPane.add(boardPanel, BorderLayout.CENTER);

		pane.add(leftPane);

		// Adding on the side panel

		sidePanel = new JPanel();
		sidePanelLayout = new CardLayout(10, 10);
		sidePanel.setLayout(sidePanelLayout);

		JPanel inGamePanel = new JPanel();
		// Padding in between elements
		inGamePanel.setLayout(new BorderLayout(0, 10));

		screamer = new JTextField();
		screamer.setFont(new Font("Monospaced", Font.BOLD, 20));
		if (Board.getBoard() != null) {
			// Ensures that when there gets added an extra digit, the units
			// digit won't move
			String spaces = "";
			for (int i = 1; i >= Integer.toString(Board.getBoard().getTurn()).length(); i--) {
				spaces += ' ';
			}
			if (Board.getBoard().isWhiteMove()) {
				screamer.setText(spaces + Board.getBoard().getTurn() + ": White");
			} else {
				screamer.setText(spaces + Board.getBoard().getTurn() + ": Black");
			}
		}
		screamer.setColumns(16);
		screamer.setEditable(false);
		screamer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		inGamePanel.add(screamer, BorderLayout.NORTH);

		history = new JTextArea(); // "History"
		history.setEditable(false);
		history.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		history.setFont(new Font("Monospaced", Font.PLAIN, 24));

		// To always update scroll position to bottom -- http://goo.gl/4818K2
		DefaultCaret caret = (DefaultCaret) history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		history.setCaret(caret);

		JScrollPane historyScollPane = new JScrollPane(history);
		inGamePanel.add(historyScollPane, BorderLayout.CENTER);

		sidePanel.add(inGamePanel, "InGame");

		// Create game sidepanel

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new GridLayout(1, 2, 5, 0));

		JButton startButton = new JButton("Start");
		startButton.addActionListener(this);
		buttonPane.add(startButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setBackground(Color.LIGHT_GRAY);
		buttonPane.add(cancelButton);

		JLabel whiteNameLabel = new JLabel("White Player");

		whiteNameField = new JTextField("White pl.");
		whiteNameField.addActionListener(this);

		JLabel blackNameLabel = new JLabel("Black Player");

		blackNameField = new JTextField("Black CPU");
		blackNameField.setEnabled(false);
		blackNameField.addActionListener(this);

		JLabel whiteDifficultyLabel = new JLabel("Difficulty");

		Hashtable labelTable = new Hashtable();
		labelTable.put(0, new JLabel("Passive"));
		labelTable.put(1, new JLabel("Aggressive"));
		labelTable.put(2, new JLabel("Easy"));
		labelTable.put(3, new JLabel("Medium"));

		whiteDifficultySlider = new JSlider(JSlider.VERTICAL, 0, 3, 1);
		whiteDifficultySlider.setPreferredSize(new Dimension(20, 80));
		whiteDifficultySlider.setMajorTickSpacing(1);
		whiteDifficultySlider.setSnapToTicks(true);
		whiteDifficultySlider.setPaintTicks(true);
		whiteDifficultySlider.setPaintLabels(true);
		whiteDifficultySlider.setLabelTable(labelTable);
		whiteDifficultySlider.setEnabled(false);

		JLabel blackDifficultyLabel = new JLabel("Difficulty");

		blackDifficultySlider = new JSlider(JSlider.VERTICAL, 0, 3, 1);
		blackDifficultySlider.setPreferredSize(new Dimension(20, 80));
		blackDifficultySlider.setMajorTickSpacing(1);
		blackDifficultySlider.setSnapToTicks(true);
		blackDifficultySlider.setPaintTicks(true);
		blackDifficultySlider.setPaintLabels(true);
		blackDifficultySlider.setLabelTable(labelTable);

		JPanel timerSpinnerPane = new JPanel();
		timerSpinnerPane.setLayout(new GridLayout(2, 2));

		JLabel timerLabel = new JLabel("Timer");
		timerSpinnerPane.add(timerLabel);

		SpinnerModel timerSpinnerModel = new SpinnerNumberModel(0, 0, 60, 1);
		timerSpinner = new JSpinner(timerSpinnerModel);

		timerSpinnerPane.add(timerSpinner);

		JLabel emptyTimerLabel = new JLabel("");
		timerSpinnerPane.add(emptyTimerLabel);

		JLabel timerDisableLabel = new JLabel("(0 to disable)");
		timerDisableLabel.setHorizontalAlignment(JLabel.HORIZONTAL);
		timerSpinnerPane.add(timerDisableLabel);

		JLabel whiteAiCheckLabel = new JLabel("White CPU");

		whiteAiCheck = new JCheckBox(" ");
		whiteAiCheck.setFocusPainted(false);
		whiteAiCheck.addActionListener(this);

		JLabel blackAiCheckLabel = new JLabel("Black CPU");

		blackAiCheck = new JCheckBox("  ");
		blackAiCheck.setFocusPainted(false);
		blackAiCheck.setSelected(true);
		blackAiCheck.addActionListener(this);

		// Putting the elements together for new game side panel
		// Rigid areas are for spaces in between some elements

		JPanel gamePane = new JPanel();
		gamePane.setLayout(new BorderLayout());

		JPanel toTopPane = new JPanel();
		toTopPane.setLayout(new BoxLayout(toTopPane, BoxLayout.PAGE_AXIS));

		toTopPane.add(Box.createRigidArea(new Dimension(0, 50)));
		toTopPane.add(new JSeparator());
		toTopPane.add(Box.createRigidArea(new Dimension(0, 10)));

		toTopPane.add(Box.createRigidArea(new Dimension(0, 10)));

		JPanel whitePlayerBase = new JPanel();
		whitePlayerBase.setLayout(new GridLayout(2, 2, 0, 5));

		whitePlayerBase.add(whiteNameLabel);
		whitePlayerBase.add(whiteNameField);

		whitePlayerBase.add(whiteAiCheckLabel);
		whitePlayerBase.add(whiteAiCheck);

		toTopPane.add(whitePlayerBase);

		whitePlayerDifficultyPane = new JPanel();
		whitePlayerDifficultyPane.setLayout(new GridLayout(1, 2));

		whitePlayerDifficultyPane.add(whiteDifficultyLabel);
		whitePlayerDifficultyPane.add(whiteDifficultySlider);

		toTopPane.add(Box.createRigidArea(new Dimension(0, 10)));

		toTopPane.add(whitePlayerDifficultyPane);

		toTopPane.add(Box.createRigidArea(new Dimension(0, 10)));

		toTopPane.add(new JSeparator());

		toTopPane.add(Box.createRigidArea(new Dimension(0, 10)));

		JPanel blackPlayerBase = new JPanel();
		blackPlayerBase.setLayout(new GridLayout(2, 2, 0, 5));

		blackPlayerBase.add(blackNameLabel);
		blackPlayerBase.add(blackNameField);

		blackPlayerBase.add(blackAiCheckLabel);
		blackPlayerBase.add(blackAiCheck);

		toTopPane.add(blackPlayerBase);

		blackPlayerDifficultyPane = new JPanel();
		blackPlayerDifficultyPane.setLayout(new GridLayout(1, 2));

		blackPlayerDifficultyPane.add(blackDifficultyLabel);
		blackPlayerDifficultyPane.add(blackDifficultySlider);

		toTopPane.add(blackPlayerDifficultyPane);

		toTopPane.add(Box.createRigidArea(new Dimension(0, 10)));

		toTopPane.add(new JSeparator());

		toTopPane.add(Box.createRigidArea(new Dimension(0, 10)));

		toTopPane.add(timerSpinnerPane);

		gamePane.add(toTopPane, BorderLayout.NORTH);
		gamePane.add(buttonPane, BorderLayout.SOUTH);

		sidePanel.add(gamePane, "NewGame");

		pane.add(sidePanel, BorderLayout.EAST);
		updateCursor();

		// Size of the frame
		setMinimumSize(new Dimension(734, 600)); // Minimum size of the window

		// Center the frame on start
		setLocationRelativeTo(null);
		// Disable resize
		setResizable(false);
		pack();

		// Play splash screen before start (useless but looks cool)
		splashScreen();

		setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		// Menu
		if (action.equals("Exit")) {
			// Kills the program
			System.exit(0);
		}

		else if (action.equals("New")) {
			// Stops game, opens new game sidepanel
			timer.stop();
			sidePanelLayout.show(sidePanel, "NewGame");
			// getBoardPanel().setEnabled(false);
			setBoardEnabled(false);
			SettingsFrame.resetOptions();
			updateBoard();
		} else if (action.equals("Save")) {
			// Saves games with file chooser, must be a .chess file
			timer.stop();
			String directory = "";
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Save to a .chess file");
			boolean accepted = false;
			while (!accepted) {
				int returnValue = fileChooser.showSaveDialog(this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					if (selectedFile.getName().substring(selectedFile.getName().indexOf('.') + 1).equals("chess")) {
						directory = selectedFile.getAbsolutePath();
						accepted = true;
						timer.start();
						System.out.println(directory);
					} else if (selectedFile.getName().length() == 0 || selectedFile.getName().indexOf('.') == -1) {
					} else {
						directory = selectedFile.getAbsolutePath();
						directory += ".chess";
						accepted = true;
						timer.start();
						System.out.println(directory);
					}
				} else {
					timer.start();
					return;
				}
			}
			try {
				// Write all parameters to be read by replay
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(directory)));
				writer.println(Board.getBoard().getWhitePlayer().getName());
				writer.println(Board.getBoard().getBlackPlayer().getName());
				writer.println(Board.getBoard().isWhiteAI());
				writer.println(Board.getBoard().isBlackAI());
				writer.println(Board.getBoard().getWhitePlayer().getMinutes());
				writer.println(Board.getBoard().getWhitePlayer().getSeconds());
				writer.println(Board.getBoard().getBlackPlayer().getMinutes());
				writer.println(Board.getBoard().getBlackPlayer().getSeconds());
				writer.println(Board.getBoard().getWhitePlayer().getDifficulty());
				writer.println(Board.getBoard().getBlackPlayer().getDifficulty());
				writer.println("---");
				for (Move m : Board.getBoard().getMoveHistory()) {
					writer.println(m.toString());
				}
				writer.close();
			} catch (IOException evt) {
				System.out.println("save failure");
			}
		} else if (action.equals("Open")) {
			// Opens game with file chooser, will kill previous threads, must
			// be .chess file
			timer.stop();
			String directory = "";
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Open a .chess file");
			boolean accepted = false;
			while (!accepted) {
				int returnValue = fileChooser.showOpenDialog(this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					if (selectedFile.getName().substring(selectedFile.getName().lastIndexOf('.') + 1).equals("chess")) {
						directory = selectedFile.getAbsolutePath();
						System.out.println(directory);
						accepted = true;
						Board.getBoard().endOfGame = true;
						// If there is a turn still being played by an AI, it
						// will stop thinking by the maximum of 1/10 of a
						// second, to
						// completely stop the game
						try {
							Thread.sleep(120);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				} else {
					timer.start();
					return;
				}
			}
			if (accepted) {
				setUpBoard();
				Board.getBoard().setWhiteAI(false);
				Board.getBoard().setBlackAI(false);
				replayRunner = new Replay(directory);
				replayThread = new Thread(replayRunner);
				Board.getBoard().clearHistoryArray();
				sidePanelLayout.show(sidePanel, "InGame");
				updateBoard();
				replayThread.start();
			}
		} else if (action.equals("Rules")) {
			// Opens rules frame
			RuleFrame rules = new RuleFrame();
			rules.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else if (action.equals("About")) {
			// Opens rules frame
			AboutFrame about = new AboutFrame();
			about.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else if (action.equals("Settings")) {
			// Opens settings frame
			if (settings == null || !settings.isVisible()) {
				settings = new SettingsFrame();
				settings.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			} else {
				settings.toFront();
			}
		} else if (action.equals("Start")) {
			// Starts the game with the specified user input
			if (whiteNameField.getText().trim().length() > 10) {
				JOptionPane.showMessageDialog(chessGame, "White's name must not exceed 10 characters",
						"Name over 10 characters", JOptionPane.PLAIN_MESSAGE);
			} else if (whiteNameField.getText().trim().length() == 0) {
				JOptionPane.showMessageDialog(chessGame, "White's name must not be empty", "Name has no characters",
						JOptionPane.PLAIN_MESSAGE);
			} else if (blackNameField.getText().trim().length() > 10) {
				JOptionPane.showMessageDialog(chessGame, "Black's name must not exceed 10 characters",
						"Name over 10 characters", JOptionPane.PLAIN_MESSAGE);
			} else if (blackNameField.getText().trim().length() == 0) {
				JOptionPane.showMessageDialog(chessGame, "Black's name must not be empty", "Name has no characters",
						JOptionPane.PLAIN_MESSAGE);
			} else {
				Board.getBoard().endOfGame = true;
				try {
					Thread.sleep(120);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				SettingsFrame.resetOptions();
				replayRunner = null;

				if ((int) timerSpinner.getValue() == 0) {
					Board.getBoard().getWhitePlayer().setMinutes(-1);
					Board.getBoard().getBlackPlayer().setMinutes(-1);
				} else {
					Board.getBoard().getWhitePlayer().setMinutes((int) timerSpinner.getValue());
					Board.getBoard().getBlackPlayer().setMinutes((int) timerSpinner.getValue());
				}
				timer.restart();
				Board.getBoard().clearHistoryArray();

				Board.getBoard().getWhitePlayer().setSeconds(00);
				Board.getBoard().getBlackPlayer().setSeconds(00);

				Board.getBoard().getWhitePlayer().setName(whiteNameField.getText().trim());
				Board.getBoard().getBlackPlayer().setName(blackNameField.getText().trim());

				Board.getBoard().setWhiteAI(whiteAiCheck.isSelected());
				Board.getBoard().setBlackAI(blackAiCheck.isSelected());

				if (Board.getBoard().isWhiteAI()) {
					Board.getBoard().getWhitePlayer().setDifficulty(whiteDifficultySlider.getValue());
				}
				if (Board.getBoard().isBlackAI()) {
					Board.getBoard().getBlackPlayer().setDifficulty(blackDifficultySlider.getValue());
				}

				player1TimerLabel.setText(Board.getBoard().getWhitePlayer().getName());
				player2TimerLabel.setText(Board.getBoard().getBlackPlayer().getName());

				sidePanelLayout.show(sidePanel, "InGame");
				// getBoardPanel().setEnabled(true);
				setBoardEnabled(true);
				setUpBoard();
				Board.getBoard().endOfGame = false;
				if (Board.getBoard().isWhiteAI() && !Board.getBoard().isBlackAI()) {
					// Will rotate board if white is ai and black is player, for
					// player convenience
					rotateBoard();
				}
				if (Board.getBoard().isWhiteAI() && Board.getBoard().isWhiteMove()) {
					// Will start the ai turns if a white AI starts the game
					Board.getBoard().aiTurn();
				}
				SettingsFrame.updateButtons();
				updateBoard();
			}

		} else if (action.equals("Cancel")) {
			// Returns game to normal from its new game state

			sidePanelLayout.show(sidePanel, "InGame");
			timer.start();
			setBoardEnabled(true);
			updateBoard();
		} else if (replayRunner != null && !replayRunner.stopLoop) {
			// Do nothing if playing
		}
		// Buttons, will not work if its during an ai turn
		else if (e.getSource() instanceof SquareButton && !Board.getBoard().endOfGame) {
			if (Board.getBoard().isWhiteMove()) {
				if (Board.getBoard().isWhiteAI()) {
					return;
				}
			} else {
				if (Board.getBoard().isBlackAI()) {
					return;
				}
			}
			SquareButton btn = (SquareButton) e.getSource();
			Square square = btn.getSquare();
			square.onClick(); // Update game data
			updateBoard();
		} else if (e.getSource() instanceof JCheckBox) {
			// If one of the AI check boxes have been touched, will change
			// elements
			if (action.equals(" ")) {
				if (((JCheckBox) e.getSource()).isSelected()) {
					whiteNameField.setText("White CPU");
					whiteDifficultySlider.setEnabled(true);
					whiteNameField.setEnabled(false);
				} else {
					whiteNameField.setText("White pl.");
					whiteDifficultySlider.setEnabled(false);
					whiteNameField.setEnabled(true);
				}
			} else if (action.equals("  ")) {
				if (((JCheckBox) e.getSource()).isSelected()) {
					blackNameField.setText("Black CPU");
					blackDifficultySlider.setEnabled(true);
					blackNameField.setEnabled(false);
				} else {
					blackNameField.setText("Black pl.");
					blackDifficultySlider.setEnabled(false);
					blackNameField.setEnabled(true);
				}
			}
		}
	}

	private void setBoardEnabled(boolean enable) {
		Component[] components = boardPanel.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof SquareButton) {
				components[i].setEnabled(enable);
			}
		}
	}

	public JPanel getBoardPanel() {
		return boardPanel;
	}

	/**
	 * Opens select audio clip from folder Dependency: AudioSystem, Clip,
	 * AudioInputStream, Exception Date created: 4 January 2015 Last modified: 4
	 * January 2016
	 * 
	 * @author Dennis Krasnov @param name what file is called @return Clip
	 *         opened clip @throws will notify if something breaks
	 */
	public Clip openClip(String name) {
		AudioInputStream audioInputStream;
		Clip clip = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource(name + ".wav"));
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clip;
	}

	public void playBiteSound() {
		biteSound.setFramePosition(0);
		biteSound.start();
	}

	public void playCheckmateSound() {
		checkmateSound.setFramePosition(0);
		checkmateSound.start();
	}

	public void playMoveSound() {
		moveSound.setFramePosition(0);
		moveSound.start();
	}

	public void playCheckSound() {
		checkSound.setFramePosition(0);
		checkSound.start();
	}

	public void playStalemateSound() {
		stalemateSound.setFramePosition(0);
		stalemateSound.start();
	}
	
	public void playCastleSound() {
		castlingSound.setFramePosition(0);
		castlingSound.start();
	}

	/**
	 * Resets both player's timer, starts universal timer Dependency: Player,
	 * Board Date created: 4 January 2015 Last modified: 4 January 2016
	 * 
	 * @author Dennis Krasnov @param none @return none @throws will notify if
	 *         something breaks
	 */
	private void resetTimer() {
		// Full reset of both player's timer, will set to -1 if chose to be
		// disabled
		// White player
		Player player1 = Board.getBoard().getWhitePlayer();
		// Black player
		Player player2 = Board.getBoard().getBlackPlayer();
		if (timerSpinner != null) {
			if ((int) timerSpinner.getValue() == 0) {
				Board.getBoard().getWhitePlayer().setMinutes(-1);
				Board.getBoard().getBlackPlayer().setMinutes(-1);
			} else {
				Board.getBoard().getWhitePlayer().setMinutes((int) timerSpinner.getValue());
				Board.getBoard().getBlackPlayer().setMinutes((int) timerSpinner.getValue());
			}
		} else {
			Board.getBoard().getWhitePlayer().setMinutes(-1);
			Board.getBoard().getBlackPlayer().setMinutes(-1);
		}

		timer.restart();

		Board.getBoard().getWhitePlayer().setSeconds(00);
		Board.getBoard().getBlackPlayer().setSeconds(00);

		timer.start();

		updateBoard();
	}

	/**
	 * Put all squares from button into list, invert list, put back into buttons
	 * Dependency: ArrayList, Square, SquareButton Date created: 4 January 2015
	 * Last modified: 4 January 2016
	 * 
	 * @author Dennis Krasnov @param none @return none @throws none
	 */
	public void rotateBoard() {
		// Get all squares and buttons
		// All buttons
		ArrayList<SquareButton> buttons = new ArrayList<SquareButton>();
		// All squares in buttons
		ArrayList<Square> squares = new ArrayList<Square>();
		for (Component c : boardPanel.getComponents()) {
			if (c instanceof SquareButton) {
				buttons.add((SquareButton) c);
				squares.add(((SquareButton) c).getSquare());
			}
		}
		// Temporary Square for switching positions
		Square temp;
		// Invert squares
		for (int i = 0; i < (int) squares.size() / 2; i++) {
			temp = squares.get(i);
			squares.set(i, squares.get(squares.size() - 1 - i));
			squares.set(squares.size() - 1 - i, temp);
		}
		// Put back in
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).setSquare(squares.get(i));
		}
	}

	/**
	 * Creates board for the start of the game Dependency: Board, Square,
	 * SquareButton Date created: 4 January 2015 Last modified: 4 January 2016
	 * 
	 * @author Dennis Krasnov @param none @return none @throws none
	 */
	public void setUpBoard() {
		// Instance of Board
		Board board = Board.getBoard();
		// White always moves first
		if (!board.isWhiteMove()) {
			board.setWhiteMove(true);
		}
		// Removing all buttons, making new ones
		boardPanel.removeAll();
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				Square square = board.getSquare(j, 9 - i); // new Square( j, 9 -
															// i );
				SquareButton squareButton = new SquareButton(square);
				squareButton.addActionListener(this);
				boardPanel.add(squareButton);
			}
		}
		// Reseting timer, other gui elements
		resetTimer();
		board.clearHistory();
		board.resetTurn();
		board.resetPieceLocation();
	}

	private void splashScreen() {
		JWindow splashWindow = new JWindow();
		JPanel pane = (JPanel) splashWindow.getContentPane();

		// Padding in between elements
		pane.setLayout(new BorderLayout(5, 5));
		// Creating visible border
		pane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
		// Gif
		JLabel animation = new JLabel(new ImageIcon(getClass().getResource("animation.gif")));
		pane.add(animation, BorderLayout.CENTER);

		// Program name
		JLabel programName = new JLabel("Chess");
		programName.setHorizontalAlignment(JLabel.CENTER);
		programName.setFont(new Font("Serif", Font.BOLD, 32));
		pane.add(programName, BorderLayout.NORTH);

		JPanel bottomSplashPane = new JPanel();
		bottomSplashPane.setLayout(new BorderLayout(5, 5));

		// Author label
		JLabel authorLabel = new JLabel("By Dennis Krasnov");
		authorLabel.setHorizontalAlignment(JLabel.CENTER);
		authorLabel.setFont(new Font("Serif", Font.BOLD, 20));
		bottomSplashPane.add(authorLabel, BorderLayout.NORTH);

		// Progress bar
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setFont(new Font("Serif", Font.BOLD, 16));
		progressBar.setForeground(Color.DARK_GRAY);

		bottomSplashPane.add(progressBar, BorderLayout.SOUTH);

		pane.add(bottomSplashPane, BorderLayout.SOUTH);

		splashWindow.pack();

		splashWindow.setLocationRelativeTo(null);

		splashWindow.setVisible(true);

		// Goes from 1-100% gradually faster to make it look cool
		for (int i = 1; i <= 100; i++) {
			progressBar.setValue(i);
			try {
				Thread.sleep((101 - i) / 5); // ( 120 - i ) / 2 for release
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		splashWindow.setVisible(false);
		splashWindow.dispose();
	}

	/**
	 * Updates every single visual element Dependency: Component, SquareButton,
	 * Board, Move, Player, AbstractButton, SettingsFrame Date created: 4
	 * January 2015 Last modified: 4 January 2016
	 * 
	 * @author Dennis Krasnov @param none @return none @throws none
	 */
	public void updateBoard() {
		// Updates each button individually
		for (Component c : boardPanel.getComponents()) {
			if (c instanceof SquareButton) {
				SquareButton b = (SquareButton) c;
				b.updateButton();
			}
		}
		// Setting screamer text
		if (Board.getBoard() != null && screamer != null) {
			String spaces = "";
			for (int i = 1; i >= Integer.toString(Board.getBoard().getTurn()).length(); i--) {
				spaces += ' ';
			}
			if (Board.getBoard().isWhiteMove()) {
				screamer.setText(spaces + Board.getBoard().getTurn() + ": White");
			} else {
				screamer.setText(spaces + Board.getBoard().getTurn() + ": Black");
			}
			if (Board.getBoard().getScreamerText() != null && Board.getBoard().getTurn() != 1) {
				screamer.setText(spaces + Board.getBoard().getTurn() + ": " + Board.getBoard().getScreamerText());
			}
		}
		// Setting history if there is any
		if (Board.getBoard() != null && history != null) {
			String historyText = "";
			for (Move m : Board.getBoard().getMoveHistory()) {
				historyText += m.toString() + "\n";
			}
			history.setText(historyText);
		}
		// Update to most current player's cursor
		updateCursor();

		// Instance of white player
		Player player1 = Board.getBoard().getWhitePlayer();
		// Instance of black player
		Player player2 = Board.getBoard().getBlackPlayer();
		// Update timers
		if (player1.getMinutes() > 0 || player2.getMinutes() > 0) {
			setTimerText();
		}

		// Sets correct icon for each button, sets as updated theme
		for (Component c : boardPanel.getComponents()) {
			if (c instanceof SquareButton) {
				Figure fig = ((SquareButton) c).getSquare().getFigure();
				if (fig != null) {
					((AbstractButton) c).setIcon(fig.getFigureIcon());
				} else {
					((AbstractButton) c).setIcon(null);
				}
			}
		}
		Figure.themeChanged = false;

		// Sets to correct move highlight, if any
		for (Component c : boardPanel.getComponents()) {
			if (c instanceof SquareButton) {
				if (((SquareButton) c).getSquare().getFigure() != null && ((SquareButton) c).getSquare().getFigure()
						.isWhiteFigure() == Board.getBoard().isWhiteMove()) {
					if (Board.getBoard().getSelectedSquare() == null
							&& ((SquareButton) c).getSquare().getFigure().getAllMoves(Move.VALID_MOVES) != null
							&& ((SquareButton) c).getSquare().getFigure().getAllMoves(Move.VALID_MOVES).size() > 0) {
						if (SettingsFrame.isShowHighlight()) {
							if (((SquareButton) c).getSquare().isBlack()) {
								c.setBackground(BLACK_SQUARE_HASMOVES); // Dark
																		// blue
							} else {
								c.setBackground(WHITE_SQUARE_HASMOVES); // Light
																		// blue
							}
						}
					}
				}
			}
		}

	}

	/**
	 * Sets current player's timer text (only one that can be changed during his
	 * turn) Dependency: Player Date created: 4 January 2015 Last modified: 4
	 * January 2016
	 * 
	 * @author Dennis Krasnov @param none @return none @throws none
	 */
	private void setTimerText() {
		// Instance of white player
		Player player1 = Board.getBoard().getWhitePlayer();
		// Instance of black player
		Player player2 = Board.getBoard().getBlackPlayer();
		// Setting proper time format
		player1TimerLabel.setText(
				player1.getName() + ": " + player1.getMinutes() + ":" + String.format("%02d", player1.getSeconds()));
		player2TimerLabel.setText(
				player2.getName() + ": " + player2.getMinutes() + ":" + String.format("%02d", player2.getSeconds()));
	}

	/**
	 * Sets cursor to current player's colour Dependency: Toolkit, ImageIcon,
	 * Cursor Date created: 4 January 2015 Last modified: 4 January 2016
	 * 
	 * @author Dennis Krasnov @param none @return none @throws none
	 */
	private void updateCursor() {
		// Application's toolkit
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		// Image Icon for cursor
		ImageIcon imageIco;
		// Sets to correct cursor (different colour)
		if (Board.getBoard().isWhiteMove()) {
			imageIco = new ImageIcon(getClass().getResource("handCursor32White.png"));
		} else {
			imageIco = new ImageIcon(getClass().getResource("handCursor32Black.png"));
		}
		Cursor c = toolkit.createCustomCursor(imageIco.getImage(), new Point(2, 3), "img");
		boardPanel.setCursor(c);
	}

	/**
	 * Stops timers, creates dialog message with winner Dependency: Player,
	 * JOptionPane, Board Date created: 4 January 2015 Last modified: 4 January
	 * 2016
	 * 
	 * @author Dennis Krasnov @param none @return none @throws none
	 */
	public void winner(boolean player1Won) {
		timer.stop();
		// Instance of white player
		Player player1 = Board.getBoard().getWhitePlayer();
		// Instance of black player
		Player player2 = Board.getBoard().getBlackPlayer();
		updateBoard();
		// Creates appropriate winner dialog
		if (player1Won) {
			JOptionPane.showMessageDialog(chessGame, player1.getName() + " wins", "White player wins",
					JOptionPane.PLAIN_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(chessGame, player2.getName() + " wins", "Black player wins",
					JOptionPane.PLAIN_MESSAGE);
		}
		Board.getBoard().endOfGame = true;
	}

	public Timer getTimer() {
		return timer;
	}
}
