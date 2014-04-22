package PlayerCommands;

import Player.Mp3;

public class PlayCommand implements PlayerCommand {
	private Mp3 fileToPlay;
	
	public PlayCommand(Mp3 fileToPlay) {
		this.fileToPlay = fileToPlay;
	}
	
	public Mp3 getFileToPlay() {
		return fileToPlay;
	}
	
}