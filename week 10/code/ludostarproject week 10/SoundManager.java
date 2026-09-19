import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Handles sound effects and background music for Ludo Star.
 */
public class SoundManager {

    private Clip lobbyMusicClip;

    /**
     * Plays a sound effect once.
     */
    private void playSound(String fileName) {
        try {
            InputStream input = getClass().getResourceAsStream("/sounds/" + fileName);

            if (input == null) {
                System.out.println("Sound file not found: " + fileName);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(input);

            AudioFormat format = audioStream.getFormat();

            // Convert to standard PCM if necessary
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        format.getSampleRate(),
                        16,
                        format.getChannels(),
                        format.getChannels() * 2,
                        format.getSampleRate(),
                        false);

                audioStream = AudioSystem.getAudioInputStream(
                        decodedFormat,
                        audioStream);

                format = decodedFormat;
            }

            // Read the complete audio into memory.
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = audioStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            byte[] audioData = output.toByteArray();

            audioStream.close();
            input.close();

            if (audioData.length == 0) {
                System.out.println("Empty audio file: " + fileName);
                return;
            }

            Clip clip = AudioSystem.getClip();

            // Explicit byte length prevents "Audio data < 0"
            clip.open(format, audioData, 0, audioData.length);

            clip.start();

            clip.addLineListener(event -> {
                if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                    clip.close();
                }
            });

        } catch (Exception e) {
            System.out.println("Could not play sound: " + fileName);
            e.printStackTrace();
        }
    }

    /**
     * Starts the lobby music and loops it continuously.
     */
    public void playLobbyMusic() {
        try {
            // Prevent multiple lobby music clips from playing.
            stopLobbyMusic();

            InputStream input = getClass().getResourceAsStream("/sounds/lobby.wav");

            if (input == null) {
                System.out.println("Sound file not found: lobby.wav");
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(input);

            AudioFormat format = audioStream.getFormat();

            // Convert to standard PCM if necessary
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        format.getSampleRate(),
                        16,
                        format.getChannels(),
                        format.getChannels() * 2,
                        format.getSampleRate(),
                        false);

                audioStream = AudioSystem.getAudioInputStream(
                        decodedFormat,
                        audioStream);

                format = decodedFormat;
            }

            // Read the complete music file into memory.
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = audioStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            byte[] audioData = output.toByteArray();

            audioStream.close();
            input.close();

            if (audioData.length == 0) {
                System.out.println("Empty audio file: lobby.wav");
                return;
            }

            lobbyMusicClip = AudioSystem.getClip();

            lobbyMusicClip.open(
                    format,
                    audioData,
                    0,
                    audioData.length);

            // Loop continuously until stopped.
            lobbyMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            lobbyMusicClip.start();

        } catch (Exception e) {
            System.out.println("Could not play lobby music.");
            e.printStackTrace();
        }
    }

    /**
     * Stops the lobby music.
     */
    public void stopLobbyMusic() {
        if (lobbyMusicClip != null) {

            if (lobbyMusicClip.isRunning()) {
                lobbyMusicClip.stop();
            }

            lobbyMusicClip.close();
            lobbyMusicClip = null;
        }
    }

    public void playGameStartMusic() {
        playSound("game_start.wav");
    }

    public void playDiceRoll() {
        playSound("dice.wav");
    }

    public void playMove() {
        playSound("move.wav");
    }

    public void playEnterBoard() {
        playSound("enter.wav");
    }

    public void playCapture() {
        playSound("capture.wav");
    }

    public void playHome() {
        playSound("home.wav");
    }

    public void playWin() {
        playSound("win.wav");
    }
}