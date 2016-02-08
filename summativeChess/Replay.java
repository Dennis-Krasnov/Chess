/**
 * Opens a file, starts playing the game then continues the game if games hasn't ended
 * Date created: 10 November 2015
 * Last modified: 11 January 2016
 * @author Dennis Krasnov
 */
package summativeChess;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Replay implements Runnable {

	// If loop should be stopped or not
	public boolean stopLoop = false;
	// File to open
	private String file = "";

	public Replay(String file) {
		this.file = file;
	}

	/**
	 * Reads in a file, plays the game, restores setting, continues game if it
	 * has not ended Dependency: BufferedReader, ChessFrame, Board, Move,
	 * IOException Date created: 10 November 2015 Last modified: 20 December
	 * 2015
	 * 
	 * @author Dennis Krasnov
	 * @param none
	 * @return none
	 * @throws Will
	 *             notify if FileIo has gone wrong
	 */
	@Override
	public void run() {
		try {
			// File input
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			// Restarts board
			ChessFrame.getChessFrame().setUpBoard();
			String line = reader.readLine();
			// Clears history
			Board.getBoard().clearHistoryArray();

			// White player name
			Board.getBoard().getWhitePlayer().setName(line);

			// Data will be remembered to be set after game has been run

			line = reader.readLine();
			// Black player name
			Board.getBoard().getBlackPlayer().setName(line);
			line = reader.readLine();
			// Is white ai
			boolean whiteAI = Boolean.parseBoolean(line);

			line = reader.readLine();
			// Is black AI
			boolean blackAI = Boolean.parseBoolean(line);

			line = reader.readLine();
			int whiteMinutes = Integer.parseInt(line);

			line = reader.readLine();
			int whiteSeconds = Integer.parseInt(line);

			line = reader.readLine();
			int blackMinutes = Integer.parseInt(line);

			line = reader.readLine();
			int blackSeconds = Integer.parseInt(line);

			line = reader.readLine();
			if (whiteAI) {
				Board.getBoard().getWhitePlayer().setDifficulty(Integer.parseInt(line));
			} else {
				Board.getBoard().getWhitePlayer().setDifficulty(-1);
			}
			line = reader.readLine();
			if (blackAI) {
				Board.getBoard().getBlackPlayer().setDifficulty(Integer.parseInt(line));
			} else {
				Board.getBoard().getBlackPlayer().setDifficulty(-1);
			}
			line = reader.readLine();

			// End of settings

			if (line.equals("---")) {
				line = reader.readLine();
			} else {
				System.out.println("error");
			}
			// Reads an plays out game
			Board.getBoard().endOfGame = false;
			while (line != null && !stopLoop && !Board.getBoard().endOfGame) {
				Move move = new Move(line);
				Board.getBoard().performMove(move);
				ChessFrame.getChessFrame().updateBoard();
				try {
					int sleepTime = 7;
					for(int i = 0; i < sleepTime && !Board.getBoard().endOfGame; i++) {
						Thread.sleep(100);
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(Board.getBoard().endOfGame) {
					return;
				}
				line = reader.readLine();
			}
			reader.close();
			stopLoop = true;
			if (!Board.getBoard().endOfGame) {
				// Post-replay options set
				Board.getBoard().setWhiteAI(whiteAI);
				Board.getBoard().setBlackAI(blackAI);

				Board.getBoard().getWhitePlayer().setMinutes(whiteMinutes);
				Board.getBoard().getWhitePlayer().setSeconds(whiteSeconds);

				Board.getBoard().getBlackPlayer().setMinutes(blackMinutes);
				Board.getBoard().getBlackPlayer().setSeconds(blackSeconds);
				ChessFrame.getChessFrame().getTimer().start();
				ChessFrame.getChessFrame().updateBoard();
				
				// If next move is one of a Ai, will start the chain again
				if (Board.getBoard().isWhiteMove()) {
					if (Board.getBoard().isWhiteAI()) {
						Board.getBoard().aiTurn();
					}
				} else {
					if (Board.getBoard().isBlackAI()) {
						Board.getBoard().aiTurn();
					}
				}
			}

		} catch (IOException evt) {
		}
	}

}
