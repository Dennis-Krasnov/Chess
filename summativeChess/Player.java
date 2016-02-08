package summativeChess;

public class Player {

	// Id of player
	private int id;
	// Player's name
	private String name;
	// Timer minutes (-1 if disabled)
	private int minutes;
	// Second minutes
	private int seconds;
	// Difficulty of player
	private int difficulty;

	public Player(int id, String name, int minutes, int seconds) {
		this.id = id;
		this.name = name;
		this.minutes = minutes;
		this.seconds = seconds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public int getId() {
		return id;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

}
