package utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Background music player for the game.
 * Plays BGM.mp3 in a loop on a separate thread.
 * Does not affect game architecture - runs independently.
 */
public final class BGMPlayer {
    
    private static Clip clip;
    private static boolean isPlaying = false;
    
    private BGMPlayer() {}
    
    /**
     * Starts playing BGM in a loop.
     * Safe to call multiple times - will only start if not already playing.
     * 
     * @param bgmFilePath path to the BGM file (e.g., "BGM.mp3")
     */
    public static void start(String bgmFilePath) {
        if (isPlaying) {
            return; // Already playing
        }
        
        Thread bgmThread = new Thread(() -> {
            try {
                File audioFile = new File(bgmFilePath);
                if (!audioFile.exists()) {
                    System.err.println("BGM file not found: " + bgmFilePath);
                    return;
                }
                
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
                isPlaying = true;
                
                // Keep thread alive
                while (isPlaying) {
                    Thread.sleep(1000);
                }
                
            } catch (UnsupportedAudioFileException e) {
                System.err.println("BGM format not supported. Continuing without music.");
            } catch (IOException e) {
                System.err.println("Could not read BGM file. Continuing without music.");
            } catch (LineUnavailableException e) {
                System.err.println("Audio line unavailable. Continuing without music.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        bgmThread.setDaemon(true); // Don't prevent JVM exit
        bgmThread.start();
    }
    
    /**
     * Stops the BGM.
     */
    public static void stop() {
        isPlaying = false;
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}

