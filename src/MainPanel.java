import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.sound.sampled.*;
import javax.imageio.ImageIO;
import java.io.IOException;

public class  MainPanel extends JPanel implements PlaybackController.PlaybackListener {
    private PlaybackController controller;
    private AudioPlayer player;
    private JLabel songNameLabel;
    private JLabel timeLabel;
    private CircularProgressBar progressBar;
    private JButton playPauseBtn;
    private JButton shuffleBtn;
    private JButton repeatBtn;
    private JSlider volumeSlider;

    public MainPanel(PlaybackController controller) {
        this.controller = controller;
        this.player = AudioPlayer.getInstance();

        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(30, 30, 30));
        setBorder(new EmptyBorder(30, 50, 30, 50));

        add(createTopSection(), BorderLayout.NORTH);
        add(createCenterSection(), BorderLayout.CENTER);
        add(createControlSection(), BorderLayout.SOUTH);

        controller.addListener(this);
    }

    private JPanel createTopSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));

        songNameLabel = new JLabel("No song selected", SwingConstants.CENTER);
        songNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        songNameLabel.setForeground(Color.WHITE);

        panel.add(songNameLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCenterSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 30));

        JLabel discLabel = new JLabel();

        // Try to load disc image with transparency support - bigger size
        try {
            BufferedImage img = ImageIO.read(new File("icons/disc.png"));
            Image scaledImg = img.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            discLabel.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            // Fallback to emoji if image not found
            discLabel.setText("💿");
            discLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 180));
        }

        discLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(discLabel);
        return panel;
    }

    private JPanel createControlSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(new Color(30, 30, 30));

        panel.add(createProgressBar(), BorderLayout.NORTH);
        panel.add(createButtons(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createProgressBar() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(30, 30, 30));

        timeLabel = new JLabel("00:00 / 00:00", SwingConstants.CENTER);
        timeLabel.setForeground(Color.LIGHT_GRAY);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JSlider progressSlider = new JSlider(0, 100, 0);
        progressSlider.setBackground(new Color(30, 30, 30));
        progressSlider.setForeground(new Color(30, 215, 96));
        progressSlider.setFocusable(false); // Remove focus border

        // Custom UI for green thumb
        progressSlider.setUI(new javax.swing.plaf.basic.BasicSliderUI(progressSlider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(30, 215, 96)); // Green thumb
                int thumbSize = 14;
                g2d.fillOval(thumbRect.x, thumbRect.y + (thumbRect.height - thumbSize) / 2, thumbSize, thumbSize);
            }

            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                int cy = (trackRect.height / 2) - 2;
                int cw = trackRect.width;

                // Background track (gray)
                g2d.setColor(new Color(60, 60, 60));
                g2d.fillRoundRect(trackRect.x, trackRect.y + cy, cw, 4, 4, 4);

                // Played portion (green)
                int progressWidth = (int) (cw * (progressSlider.getValue() / 100.0));
                g2d.setColor(new Color(30, 215, 96));
                g2d.fillRoundRect(trackRect.x, trackRect.y + cy, progressWidth, 4, 4, 4);
            }

            @Override
            public void paintFocus(Graphics g) {
                // Override to remove focus painting
            }

            @Override
            protected TrackListener createTrackListener(JSlider slider) {
                return new TrackListener() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        // Calculate position based on click location
                        int value = valueForXPosition(e.getX());
                        progressSlider.setValue(value);

                        long duration = player.getDuration();
                        long newPosition = (long) ((value / 100.0) * duration);
                        player.setPosition(newPosition);
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        int value = valueForXPosition(e.getX());
                        progressSlider.setValue(value);

                        long duration = player.getDuration();
                        long newPosition = (long) ((value / 100.0) * duration);
                        player.setPosition(newPosition);
                    }

                    @Override
                    public boolean shouldScroll(int direction) {
                        return false;
                    }
                };
            }
        });



        panel.add(timeLabel, BorderLayout.NORTH);
        panel.add(progressSlider, BorderLayout.CENTER);

        this.progressBar = new CircularProgressBar(0); // Dummy reference for compatibility
        this.progressBar.progressSlider = progressSlider;

        return panel;
    }

    private JPanel createButtons() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Top row - playback controls centered
        JPanel playbackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        playbackPanel.setBackground(new Color(30, 30, 30));

        // Create shuffle button with initial icon
        shuffleBtn = createControlButtonWithImage("icons/shuffle.png", "🔀", "Shuffle");
        shuffleBtn.addActionListener(e -> {
            controller.toggleShuffle();
            updateShuffleButton();
        });

        JButton prevBtn = createControlButtonWithImage("icons/previous.png", "⏮", "Previous");
        prevBtn.addActionListener(e -> controller.previous());

        playPauseBtn = createControlButtonWithImage("icons/play.png", "▶", "Play");
        playPauseBtn.addActionListener(e -> controller.togglePlayPause());

        JButton nextBtn = createControlButtonWithImage("icons/next.png", "⏭", "Next");
        nextBtn.addActionListener(e -> controller.next());

        // Create repeat button with initial icon
        repeatBtn = createControlButtonWithImage("icons/repeat.png", "🔁", "Repeat");
        repeatBtn.addActionListener(e -> {
            controller.toggleRepeat();
            updateRepeatButton();
        });

        playbackPanel.add(shuffleBtn);
        playbackPanel.add(prevBtn);
        playbackPanel.add(playPauseBtn);
        playbackPanel.add(nextBtn);
        playbackPanel.add(repeatBtn);

        // Bottom row - volume controls centered
        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        volumePanel.setBackground(new Color(30, 30, 30));

        volumeSlider = new JSlider(0, 100, 80);
        volumeSlider.setPreferredSize(new Dimension(120, 40));
        volumeSlider.setBackground(new Color(30, 30, 30));
        volumeSlider.setForeground(new Color(30, 215, 96));
        volumeSlider.setFocusable(false); // Remove focus border

        // Custom UI for green volume slider
        volumeSlider.setUI(new javax.swing.plaf.basic.BasicSliderUI(volumeSlider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(30, 215, 96)); // Green thumb
                int thumbSize = 14;
                g2d.fillOval(thumbRect.x, thumbRect.y + (thumbRect.height - thumbSize) / 2, thumbSize, thumbSize);
            }

            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                int cy = (trackRect.height / 2) - 2;
                int cw = trackRect.width;

                // Background track (gray)
                g2d.setColor(new Color(60, 60, 60));
                g2d.fillRoundRect(trackRect.x, trackRect.y + cy, cw, 4, 4, 4);

                // Volume level (green)
                int volumeWidth = (int) (cw * (volumeSlider.getValue() / 100.0));
                g2d.setColor(new Color(30, 215, 96));
                g2d.fillRoundRect(trackRect.x, trackRect.y + cy, volumeWidth, 4, 4, 4);
            }

            @Override
            public void paintFocus(Graphics g) {
                // Override to remove focus painting
            }

            @Override
            protected TrackListener createTrackListener(JSlider slider) {
                return new TrackListener() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        // Calculate position based on click location
                        int value = valueForXPosition(e.getX());
                        volumeSlider.setValue(value);
                        player.setVolume(value / 100.0f);
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        int value = valueForXPosition(e.getX());
                        volumeSlider.setValue(value);
                        player.setVolume(value / 100.0f);
                    }

                    @Override
                    public boolean shouldScroll(int direction) {
                        return false;
                    }
                };
            }
        });



        JLabel volumeIcon = new JLabel();

        // Try to load volume icon with transparency support
        try {
            BufferedImage img = ImageIO.read(new File("icons/volume.png"));
            Image scaledImg = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            volumeIcon.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            // Fallback to emoji if image not found
            volumeIcon.setText("🔊");
            volumeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        }

        volumePanel.add(volumeIcon);
        volumePanel.add(volumeSlider);

        // Add both panels to main panel
        gbc.gridy = 0;
        mainPanel.add(playbackPanel, gbc);
        gbc.gridy = 1;
        mainPanel.add(volumePanel, gbc);

        return mainPanel;
    }

    private void updateShuffleButton() {
        try {
            String shufflePath = controller.isShuffle() ? "icons/shuffle_active.png" : "icons/shuffle.png";
            BufferedImage originalImg = ImageIO.read(new File(shufflePath));
            Image normalSize = originalImg.getScaledInstance(28, 28, Image.SCALE_SMOOTH);

            shuffleBtn.setIcon(new ImageIcon(normalSize));
            shuffleBtn.setText("");
        } catch (Exception e) {
            shuffleBtn.setText("🔀");
        }
    }

    private void updateRepeatButton() {
        try {
            String repeatPath = controller.isRepeat() ? "icons/repeat_active.png" : "icons/repeat.png";
            BufferedImage originalImg = ImageIO.read(new File(repeatPath));
            Image normalSize = originalImg.getScaledInstance(28, 28, Image.SCALE_SMOOTH);

            repeatBtn.setIcon(new ImageIcon(normalSize));
            repeatBtn.setText("");
        } catch (Exception e) {
            repeatBtn.setText("🔁");
        }
    }

    private JButton createControlButtonWithImage(String imagePath, String fallbackText, String tooltip) {
        JButton btn = new JButton();
        try {
            BufferedImage originalImg = ImageIO.read(new File(imagePath));
            Image normalSize = originalImg.getScaledInstance(28, 28, Image.SCALE_SMOOTH);

            btn.setIcon(new ImageIcon(normalSize));
        } catch (Exception e) {
            // Fallback to emoji if image not found
            btn.setText(fallbackText);
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        }
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false); // Make button transparent
        btn.setBorderPainted(false); // Remove border
        btn.setOpaque(false); // Make completely transparent
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    @Override
    public void onPlaybackStateChanged() {
        Song song = controller.getCurrentSong();
        songNameLabel.setText(song != null ? song.getName() : "No song selected");

        // Update play/pause button icon with transparency support
        try {
            String iconPath = controller.isPlaying() ? "icons/pause.png" : "icons/play.png";
            BufferedImage originalImg = ImageIO.read(new File(iconPath));
            Image normalSize = originalImg.getScaledInstance(28, 28, Image.SCALE_SMOOTH);

            playPauseBtn.setIcon(new ImageIcon(normalSize));
            playPauseBtn.setText("");
        } catch (Exception e) {
            // Fallback to emoji
            playPauseBtn.setText(controller.isPlaying() ? "⏸" : "▶");
        }

        // Update shuffle and repeat buttons
        updateShuffleButton();
        updateRepeatButton();
    }

    @Override
    public void onProgressUpdate() {
        long current = player.getCurrentPosition();
        long duration = player.getDuration();

        if (duration > 0 && progressBar != null && progressBar.progressSlider != null) {
            progressBar.progressSlider.setValue((int) ((current * 100) / duration));
            timeLabel.setText(formatTime(current) + " / " + formatTime(duration));
        }
    }

    private String formatTime(long microseconds) {
        long seconds = microseconds / 1000000;
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    // Inner class for compatibility - holds reference to progress slider
    private class CircularProgressBar extends JPanel {
        JSlider progressSlider;

        public CircularProgressBar(int size) {
            // Dummy constructor for compatibility
        }
    }
}