import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class libraryBorrow extends JFrame {
    private JButton homeButton;
    private JButton listOfBooksButton;
    private JButton borrowedButton;
    private JPanel mainPanel;
    private MemberList memberList;

    public libraryBorrow(String title) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        setIconImage(new ImageIcon(getClass().getResource("/images/icon64.png")).getImage());
        memberList = new MemberList();

        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginWindow dialog = new loginWindow("Log-in", memberList);
                dialog.pack();
                dialog.setLocationRelativeTo(libraryBorrow.this);
                dialog.setVisible(true);
            }
        });
    }
}