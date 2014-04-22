package Player;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Music Library class, contains an array of Mp3 objects.
 * @author Group 25
 */
public class MusicLibrary {
	/**
	 * Name given to this MusicLibrary instance.  Utterly useless and
	 * unnecessary, but whatev's.
	 */
	private String name;
	/**
	 * ArrayList of Mp3 objects.
	 */
	private ArrayList<Mp3> mp3List;
	/**
	 * The next integer to be applied to the next song imported, to help keep
	 * Mp3 instances organized and identifiable.
	 */
	private int nextSongId;
	
	
	/**
	 * MusicLibrary constructor, initiates mp3List, nextSongId, and name values
	 * to defaults (empty, 1, and the input listName String, respectively).
	 * @param listName Name to be applied to the MusicLibrary.
	 */
	public MusicLibrary(String listName) {
		name = listName;
		nextSongId = 1;
		mp3List = new ArrayList<Mp3>();
	}

	
	/**
	 * Creates an Mp3 instance and adds it to the mp3List array.
	 * @param filePath String path to the mp3 we want to add.
	 */
	public void addSong(String filePath) {
		Mp3 mp3 = new Mp3(filePath, nextSongId);
		mp3List.add(mp3);
		nextSongId++;
		sortPlaylist();
		return;
	}
	
	/**
	 * Returns the total number of upvotes.  (Previous comment: "this is dumb".
	 * Agreed.  Yet, we use this somewhere in MusicPlayerFrame.  Why?  I'll
	 * figure that out later, but, for now, this is staying.)
	 * @return Total number of upvotes on all Mp3 instances in the mp3List.
	 */
	public int totalUpvotes() {
		int total = 0;
		for (Mp3 mp3: mp3List) {
			total += mp3.getUpvotes();
		}
		return total;
	}
	
	
	/**
	 * Returns this MusicLibrary's mp3List.
	 * @return This MusicLibrary's mp3List.
	 */
	public ArrayList<Mp3> getMp3List(){
		return mp3List;
		
	}
	
	/**
	 * Returns the index of a specified Mp3 instance.
	 * @param needle The Mp3 to be found out of the haystack.
	 * @return The index in the mp3List array of the Mp3 instance given.  -1 if
	 * not found.
	 */
	public int getMp3Index(Mp3 needle) {
		int i = 0;
		for (Mp3 haystack : mp3List) {
			if (haystack == needle) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	
	/**
	 * Returns the Mp3 object out of mp3List referenced by the integer id given
	 * as input.
	 * @param id Integer identifier that *should* be specific to one of the Mp3
	 * instances in mp3List.
	 * @return Mp3 instance referenced by id input value.  If not found, returns
	 * null.
	 */
	public Mp3 getMp3ById(int id) {
		for(Mp3 item : mp3List)
			if(item.getSongId() == id)
				return item;
		return null;
	}
	
	
	/**
	 * Returns all track information from the mp3List as a double-array object,
	 * for display purposes.
	 * @return Double-array of Mp3 metadata.  If none, returns null.
	 */
	public Object[][] getSongListInfo() {
		if(mp3List.size() == 0)
			return null;
		
		Object[][] data = new Object[mp3List.size()][];
		for (int i = 0; i < mp3List.size(); i++){
			data[i] = mp3List.get(i).parseMetaData();
		}
		
		return data;
	}
	
	
	/**
	 * Returns MusicLibrary's name value.
	 * @return String name of this MusicLibrary.
	 */
	public String getName(){
		return name;
	}
	
	
	/**
	 * Sorts the mp3List based on upvote count.  Praise Allah for overrides.
	 */
	public void sortPlaylist() {
		Collections.sort(mp3List);
		
	}

	
	/**
	 * Prints the titles of all Mp3 objects in the mp3List.  Useful for
	 * debuggin' purposes.
	 */
	public void printMp3List() {
		for(Mp3 item : mp3List)
			System.out.println(item.getTitle());
		
	}
}
