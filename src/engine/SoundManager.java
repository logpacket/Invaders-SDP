package engine;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Manages sound effects and BGM.
 *
 * @author <a href="mailto:dpdudyyy@gmail.com">Yun Yeyoung</a>
 *
 */

public class SoundManager {

    /** Singleton instance of the class. */
    private static SoundManager instance;
    /** Save the sound file **/
    private static HashMap<Sound, Clip> soundClips;
    /** Application logger. */
    private static Logger logger;
    /** Sound manager activation flag */
    private boolean soundEnabled;
    /** Value of current volume */
    private static int currentVolume = 10;
    /** Maximum and minimum values of volume */
    private final float MIN_VOL = -80.0f;
    private final float MAX_VOL = 6.0f;
    /** Current playing BGM */
    private Sound currentBGM;

    /** Sound clip pools for simultaneous playback */
    private static Map<Sound, List<Clip>> soundPools;
    /** Pool size for each sound */
    private static final int POOL_SIZE = 8;

    /**
     * Private constructor.
     */
    private SoundManager() {
        logger = Core.getLogger();
        logger.info("Started loading sound resources.");

        soundClips = new HashMap<>();
        soundPools = new HashMap<>();

        soundEnabled = true;
        try {
            loadSound(Sound.MENU_BACK, "res/sound/SFX/menuBack.wav");
            loadSound(Sound.MENU_CLICK, "res/sound/SFX/menuClick.wav");
            loadSound(Sound.MENU_MOVE, "res/sound/SFX/menuMove.wav");
            loadSound(Sound.MENU_TYPING, "res/sound/SFX/nameTyping.wav");
            loadSound(Sound.COUNTDOWN, "res/sound/SFX/countdown.wav");
            loadSound(Sound.ALIEN_HIT, "res/sound/SFX/alienHit.wav");
            loadSound(Sound.ALIEN_LASER, "res/sound/SFX/alienLaser.wav");
            loadSound(Sound.PLAYER_HIT, "res/sound/SFX/playerHit.wav");
            loadSound(Sound.PLAYER_LASER, "res/sound/SFX/playerLaser.wav");
            loadSound(Sound.PLAYER_MOVE, "res/sound/SFX/playerMove.wav");
            loadSound(Sound.COIN_INSUFFICIENT, "res/sound/SFX/coinInsufficient.wav");
            loadSound(Sound.COIN_USE, "res/sound/SFX/coinUse.wav");
            loadSound(Sound.GAME_END, "res/sound/SFX/gameEnd.wav");
            loadSound(Sound.UFO_APPEAR, "res/sound/SFX/ufoAppear.wav");
            loadSound(Sound.BGM_MAIN, "res/sound/BGM/MainTheme.wav");
            loadSound(Sound.BGM_GAMEOVER, "res/sound/BGM/GameOver.wav");
            loadSound(Sound.BGM_SHOP, "res/sound/BGM/Shop.wav");
            loadSound(Sound.BGM_LV1, "res/sound/BGM/Lv1.wav");
            loadSound(Sound.BGM_LV2, "res/sound/BGM/Lv2.wav");
            loadSound(Sound.BGM_LV3, "res/sound/BGM/Lv3.wav");
            loadSound(Sound.BGM_LV4, "res/sound/BGM/Lv4.wav");
            loadSound(Sound.BGM_LV5, "res/sound/BGM/Lv5.wav");
            loadSound(Sound.BGM_LV6, "res/sound/BGM/Lv6.wav");
            loadSound(Sound.BGM_LV7, "res/sound/BGM/Lv7.wav");

            setVolume(currentVolume);
            logger.info("Finished loading all sounds.");

        } catch (IOException e) {
            soundEnabled = false;
            logger.warning("Loading failed: IO Exception");
        } catch (UnsupportedAudioFileException e) {
            soundEnabled = false;
            logger.warning("Loading failed: Unsupported audio file.");
        } catch (LineUnavailableException | IllegalArgumentException e) {
            soundEnabled = false;
            logger.warning("Loading failed: Sound device not found.");
        }
    }

    /**
     * Returns shared instance of SoundManager.
     *
     * @return Shared instance of SoundManager.
     */
    public static SoundManager getInstance() {
        if (instance == null)
            instance = new SoundManager();
        return instance;
    }

    /**
     * Load the sound and save the map.
     *
     * @param sound Key value of sound
     * @param filePath Path of the sound file
     * @throws IOException,UnsupportedAudioFileException,LineUnavailableException,IllegalArgumentException exception
     */
    public void loadSound(Sound sound, String filePath) throws IOException, UnsupportedAudioFileException, LineUnavailableException, IllegalArgumentException {
        File soundFile = new File(filePath);
        if (!soundFile.exists()) {
            throw new IOException("Sound file not found: " + filePath);
        }

        List<Clip> clipPool = new ArrayList<>();
        for (int i = 0; i < POOL_SIZE; i++) {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clipPool.add(clip);
        }

        soundPools.put(sound, clipPool);
        soundClips.put(sound, clipPool.get(0));

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);

        soundClips.put(sound, clip);
    }

    /**
     * Apply volume to all audio files by converting integer volume to decibels non-linearly.
     *
     * @param volume Int value of volume (0-10)
     */
    private void setVolume(int volume) {
        float newVolume = MIN_VOL + (float)(Math.log(volume + 1) / Math.log(11)) * (MAX_VOL - MIN_VOL);

        for (List<Clip> clipPool : soundPools.values()) {
            for (Clip clip : clipPool) {
                try {
                    FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    volumeControl.setValue(newVolume);
                } catch (IllegalArgumentException e) {
                    logger.warning("Failed to set volume: " + e.getMessage());
                }
            }
        }
    }

    /**
     * @return current volume
     * */
    public int getVolume() { return currentVolume; }

    /**
     * @return current playing BGM
     * */
    public Sound getCurrentBGM() { return currentBGM; }

    /**
     * Increases the volume of all sounds by 1.
     */
    public void volumeUp() {
        if (soundEnabled && (currentVolume < 10)) {
            currentVolume++;
            setVolume(currentVolume);
        }
    }

    /**
     * Decreases the volume of all sounds by 1.
     */
    public void volumeDown() {
        if (soundEnabled && (currentVolume > 0)) {
            currentVolume--;
            setVolume(currentVolume);
        }
    }

    /**
     * Play the sound file.
     *
     * @param sound Key value of sound
     */
    public void playSound(Sound sound) {
        playSound(sound, 0.0f);
    }

    /**
     * Play the sound file.
     *
     * @param sound Key value of sound
     * @param balance Balance value (-1.0 for left, 1.0 for right, 0.0 for center)
     */
    public void playSound(Sound sound, float balance) {
        if (soundEnabled) {
            List<Clip> clipPool = soundPools.get(sound);
            if (clipPool != null) {
                Clip availableClip = clipPool.stream()
                        .filter(clip -> !clip.isRunning())
                        .findFirst()
                        .orElse(null);

                if (availableClip != null) {
                    availableClip.setFramePosition(0);
                    try {
                        FloatControl panControl = (FloatControl) availableClip.getControl(FloatControl.Type.PAN);
                        panControl.setValue(balance);
                    } catch (IllegalArgumentException e) {
                        logger.warning("Failed to set balance for sound: " + sound);
                    }
                    availableClip.start();
                } else {
                    logger.warning("No available clips in pool for sound: " + sound);
                }
            } else {
                logger.warning("Sound not found: " + sound);
            }
        }
    }

    /**
     * Stop the sound file.
     *
     * @param sound Key value of sound
     */
    public void stopSound(Sound sound) {
        if (soundEnabled) {
            List<Clip> clipPool = soundPools.get(sound);
            if (clipPool != null) {
                clipPool.forEach(clip -> {
                    if (clip.isRunning()) {
                        clip.stop();
                    }
                });
            } else {
                logger.warning("Sound not playing or not found: " + sound);
            }
        }
    }

    /**
     * Checks if the specified sound is currently playing.
     *
     * @param sound Key value of the sound to check.
     * @return true if the sound is playing, false otherwise.
     */
    public boolean isSoundPlaying(Sound sound) {
        if (soundEnabled) {
            Clip clip = soundClips.get(sound);
            if (clip != null) {
                return clip.isRunning();
            } else {
                logger.warning("Sound not found: " + sound);
            }
        }
        return false;  // Return false if sound is not enabled or not found
    }

    /**
     * Loop the sound file.
     *
     * @param sound Key value of sound
     */
    public void loopSound(Sound sound) {
        if (soundEnabled) {
            Clip clip = soundClips.get(sound);
            if (clip != null) {
                currentBGM = sound;
                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                logger.warning("Sound not found: " + sound);
            }
        }
    }

    /** Stop and close all sound files **/
    public void closeAllSounds() {
        if (soundEnabled) {
            for (List<Clip> clipPool : soundPools.values()) {
                for (Clip clip : clipPool) {
                    if (clip != null) {
                        if (clip != null && clip.isRunning())
                            clip.stop();
                        if (clip != null)
                            clip.close();
                    }
                }
            }
        }
    }

}