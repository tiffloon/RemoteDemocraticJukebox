package Player;
//import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

/*import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
*/
import PlayerCommands.PauseCommand;
import PlayerCommands.PlayCommand;
import PlayerCommands.PlayerCommand;
import PlayerCommands.ResumeCommand;
import PlayerCommands.StopCommand;
import javazoom.jl.decoder.JavaLayerException;
//import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.Player;


/**
 * MusicHandler class, this launches and controls a thread that acts as the main
 * music player.
 * @author Group 25
 */
public class MusicHandler implements Runnable {
	
	/**
	 * Enumeration of the current state of the player.
	 */
	enum STATE {
		NOT_STARTED,
		PLAYING,
		PAUSED,
		FINISHED
	}
    /**
     * Queue of commands for the player.
     */
	static public LinkedBlockingQueue<PlayerCommand> commands = new LinkedBlockingQueue<PlayerCommand>();
	/**
	 * The music player.
	 */
    private Player player;
    /**
     * Locking object used to communicate with player thread.
     */
    private final Object playerLock = new Object();
    /**
     * Status variable describing the state of the player.
    */
    private STATE playerState;

    
    /**
    * Constructor, sets the playerState to NOTSTARTED.
    */
    public MusicHandler(){
    	playerState = STATE.NOT_STARTED;
    }

    
    /**
    * Constructor, starts the player playing the passed InputStream.
    * @param inputStream InputStream of playable shit.
    * @throws JavaLayerException If player fails to start properly on the input
    * stream.
    */
    public MusicHandler(final InputStream inputStream) throws JavaLayerException {
        this.player = new Player(inputStream);
    }

    
    /**
    * Starts playback (resumes if paused).
    * @throws JavaLayerException On player failure.
    */
    public void play() throws JavaLayerException {
        synchronized (playerLock) {
            switch (playerState) {
                case NOT_STARTED:
                	/**
                	 * New thread for playback.
                	 */
                    final Runnable r = new Runnable() {
                    	/**
                    	 * Run method for this thread, sets it's state and
                    	 * begins playback of next song.
                    	 */
                        public void run() {
                            playInternal();
                            MusicPlayerFrame.playNextSong();
                        }
                    };
                    final Thread t = new Thread(r);
                    t.setDaemon(true);
                    t.setPriority(Thread.MAX_PRIORITY);
                    playerState = STATE.PLAYING;
                    t.start();
                    break;
                case PAUSED:
                    resume();
                    break;
                default:
                    break;
            }
        }
    }
    

    /**
    * Pauses playback. Returns true if new state is PAUSED.
    * @return True if the new state is PAUSED, False if something got boned up.
    */
    public boolean pause() {
        synchronized (playerLock) {
            if (playerState == STATE.PLAYING) {
                playerState = STATE.PAUSED;
            }
            return playerState == STATE.PAUSED;
        }
    }

    
    /**
     * Resumes playback.
     * @return True if the new state is PLAYING, False if something got boned.
     */
    public boolean resume() {
        synchronized (playerLock) {
            if (playerState == STATE.PAUSED) {
                playerState = STATE.PLAYING;
                playerLock.notifyAll();
            }
            return playerState == STATE.PLAYING;
        }
    }

    
    /**
     * Stops playback. If not playing, does nothing.
     */
    public void stop() {
        synchronized (playerLock) {
            playerState = STATE.FINISHED;
            player.close();
            playerLock.notifyAll();
        }
    }

   
    /**
     * Makes sure the player is ready to begin playback.
     */
    private void playInternal() {
        while (playerState != STATE.FINISHED) {
            try {
                if (!player.play(1)) {
                    break;
                }
            } catch (final JavaLayerException e) {
                break;
            }
            // check if paused or terminated
            synchronized (playerLock) {
                while (playerState == STATE.PAUSED) {
                    try {
                        playerLock.wait();
                    } catch (final InterruptedException e) {
                        // terminate player
                        break;
                    }
                }
            }
        }
        close();
    }

    /**
     * Closes the player, regardless of current state.
     */
    public void close() {
        synchronized (playerLock) {
            playerState = STATE.FINISHED;
        }
        //try {
            //player.close();
        //} catch (final Exception e) {
            // ignore, we are terminating anyway
       // }
    }

    /**
    * Run method for the Thread.
    */
	public void run() {

		while (true) {
			if (commands.size() > 0) {
				Object command = commands.poll();
				if (command instanceof PlayCommand) {
					try {
						final PlayCommand playCommand = (PlayCommand) command;
						this.playSong(playCommand.getFileToPlay());
						System.out.println(playerState);
					} catch(Exception e) {
						// file not found or otherwise unable to play? handle me here.
						e.printStackTrace();
					}
				}
				else if (command instanceof PauseCommand)
					this.pause();
				else if(command instanceof ResumeCommand)
					this.resume();
				else if(command instanceof StopCommand){
					this.stop();
				}

			}

		}
	}

	
	/**
	 * Takes an Mp3 object and begins playback on that track.
	 * @param song Mp3 object to be played.
	 */
    public void playSong(Mp3 song) {
        try {
            FileInputStream input = new FileInputStream(song.getFilePath());
            this.player = new Player(input);
            playerState = STATE.NOT_STARTED;

            // start playing
            this.play();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    /**
     * Returns this player's state.
     * @return This instance's playerState value.
     */
	public STATE getPlayerState() {
		return this.playerState;
	}


}
