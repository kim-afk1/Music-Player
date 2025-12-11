import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AllSongsDialog extends JDialog {
    private PlaybackController controller;

    public AllSongsDialog(Window owner, List<Song> songs, PlaybackController controller) {
        super(owner, "All Songs", ModalityType.MODELESS);
        this.controller = controller;

        setSize(500, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(25, 25, 25));

        JLabel titleLabel = new JLabel("All Uploaded Songs (" + songs.size() + ")");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        JButton playAllBtn = new JButton("Play All");
        playAllBtn.setFocusPainted(false);
        playAllBtn.addActionListener(e -> playAllSongs(songs));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(playAllBtn, BorderLayout.EAST);

        // Song list with custom renderer
        DefaultListModel<Song> listModel = new DefaultListModel<>();
        for (Song song : songs) {
            listModel.addElement(song);
        }

        JList<Song> songList = new JList<>(listModel);
        songList.setFont(new Font("Arial", Font.PLAIN, 14));
        songList.setBackground(new Color(30, 30, 30));
        songList.setForeground(Color.WHITE);
        songList.setSelectionBackground(new Color(50, 150, 50));
        songList.setSelectionForeground(Color.WHITE);

        // Custom cell renderer for better appearance
        songList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value instanceof Song) {
                    Song song = (Song) value;
                    label.setText("  ♪ " + song.getName());
                    label.setBorder(new EmptyBorder(8, 10, 8, 10));
                }

                if (!isSelected) {
                    label.setBackground(index % 2 == 0 ? new Color(30, 30, 30) : new Color(35, 35, 35));
                }

                return label;
            }
        });

        // Double-click to play song
        songList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = songList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        playSongFromLibrary(songs, index);
                    }
                }
            }
        });

        // Right-click context menu
        JPopupMenu popup = new JPopupMenu();
        JMenuItem playItem = new JMenuItem("Play");
        JMenuItem playNextItem = new JMenuItem("Play Next");
        JMenuItem addToQueueItem = new JMenuItem("Add to Queue");

        playItem.addActionListener(e -> {
            int index = songList.getSelectedIndex();
            if (index >= 0) {
                playSongFromLibrary(songs, index);
            }
        });

        popup.add(playItem);
        popup.add(playNextItem);
        popup.add(addToQueueItem);

        songList.setComponentPopupMenu(popup);

        JScrollPane scrollPane = new JScrollPane(songList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(25, 25, 25));
        bottomPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JButton closeBtn = new JButton("Close");
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());

        bottomPanel.add(closeBtn);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Dark theme
        getContentPane().setBackground(new Color(25, 25, 25));
    }

    private void playSongFromLibrary(List<Song> songs, int index) {
        controller.setPlaylist(songs);
        controller.playSong(index);
    }

    private void playAllSongs(List<Song> songs) {
        if (!songs.isEmpty()) {
            controller.setPlaylist(songs);
            controller.playSong(0);
        }
    }
}