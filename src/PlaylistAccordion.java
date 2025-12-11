import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.sound.sampled.*;
import java.io.IOException;

public class PlaylistAccordion extends JPanel {
    private Playlist playlist;
    private PlaybackController controller;
    private MusicLibrary library;
    private JPanel songPanel;
    private boolean expanded = false;

    public PlaylistAccordion(Playlist playlist, PlaybackController controller, MusicLibrary library) {
        this.playlist = playlist;
        this.controller = controller;
        this.library = library;

        setLayout(new BorderLayout());
        setBackground(new Color(50, 50, 50));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(playlist.getName());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleExpanded();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) showPopupMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showPopupMenu(e);
            }
        });

        add(titleLabel, BorderLayout.CENTER);

        songPanel = new JPanel();
        songPanel.setLayout(new BoxLayout(songPanel, BoxLayout.Y_AXIS));
        songPanel.setBackground(new Color(35, 35, 35));
        songPanel.setVisible(false);
    }

    private void toggleExpanded() {
        expanded = !expanded;

        if (expanded) {
            collapseOthers();
            refreshSongs();
            add(songPanel, BorderLayout.SOUTH);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
            songPanel.setVisible(true);
        } else {
            remove(songPanel);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            songPanel.setVisible(false);
        }

        revalidate();
        repaint();
        getParent().revalidate();
        getParent().repaint();
    }

    private void collapseOthers() {
        Container parent = getParent();
        for (Component comp : parent.getComponents()) {
            if (comp instanceof PlaylistAccordion && comp != this) {
                ((PlaylistAccordion) comp).collapse();
            }
        }
    }

    private void collapse() {
        if (expanded) {
            expanded = false;
            remove(songPanel);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            songPanel.setVisible(false);
            revalidate();
            repaint();
        }
    }

    private void refreshSongs() {
        songPanel.removeAll();

        JButton addSongBtn = new JButton("+ Add Songs");
        addSongBtn.setForeground(Color.WHITE);
        addSongBtn.setBackground(new Color(50, 50, 50));
        addSongBtn.setFocusPainted(false);
        addSongBtn.addActionListener(e -> addSongsToPlaylist());
        songPanel.add(addSongBtn);

        for (Song song : playlist.getSongs()) {
            JLabel songLabel = new JLabel("  ♪ " + song.getName());
            songLabel.setForeground(Color.LIGHT_GRAY);
            songLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            songLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            songLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    controller.setPlaylist(playlist.getSongs());
                    controller.playSong(playlist.getSongs().indexOf(song));
                }
            });
            songPanel.add(songLabel);
        }
    }

    private void addSongsToPlaylist() {
        List<Song> availableSongs = library.getAllSongs();
        if (availableSongs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No songs available. Please add songs first.");
            return;
        }

        JList<Song> songList = new JList<>(availableSongs.toArray(new Song[0]));
        songList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        int result = JOptionPane.showConfirmDialog(this, new JScrollPane(songList),
                "Select songs to add", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            for (Song song : songList.getSelectedValuesList()) {
                playlist.addSong(song);
            }
            refreshSongs();
            revalidate();
            repaint();
        }
    }

    private void showPopupMenu(MouseEvent e) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete Playlist");
        deleteItem.addActionListener(ev -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete playlist '" + playlist.getName() + "'?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                library.removePlaylist(playlist);
            }
        });
        popup.add(deleteItem);
        popup.show(e.getComponent(), e.getX(), e.getY());
    }
}