import javax.swing.*;
import java.awt.event.*;

public class createAccount extends JDialog {
    private JPanel contentPane;
    private JButton createCA;
    private JButton cancelCA;
    private JTextField userFieldCA;
    private JPasswordField passFieldCA;
    private JLabel userCA;
    private JLabel passCA;
    private JPasswordField confirmPassCA;
    private JLabel feedback;
    private JTextField emailFieldCA;
    private MemberList memberList;

    public createAccount(MemberList memberList) {
        this.memberList = memberList;
        setTitle("Create a new account");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(createCA);

        try {
            setIconImage(new ImageIcon(getClass().getResource("/assets/disc.png")).getImage());
        } catch (Exception e) {
            System.err.println("Warning: Could not load icon: " + e.getMessage());
        }

        createCA.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        cancelCA.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        try {
            String username = userFieldCA.getText().trim();
            String password = new String(passFieldCA.getPassword());
            String confirmPassword = new String(confirmPassCA.getPassword());
            String email = emailFieldCA.getText().trim();
            
            feedback.setText(" ");

            // field cehcker
            if(username.isEmpty()) {
                feedback.setText("Enter a username!");
                return;
            }

            if(email.isEmpty()) {
                feedback.setText("Enter an email address!");
                return;
            }

            if(password.isEmpty()) {
                feedback.setText("Create a password.");
                return;
            }

            if(confirmPassword.isEmpty()) {
                feedback.setText("Confirm your password.");
                return;
            }

            if(!confirmPassword.equals(password)) {
                feedback.setText("Passwords do not match!");
                return;
            }

            if(!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
                feedback.setText("Invalid email format!");
                return;
            }

            // Create new member
            Member newMember = new Member(username, email, password);
            memberList.addMember(newMember);

            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!\nUsername: " + username,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Close dialog
            dispose();

        } catch (Exception ex) {
            // Catch any unexpected errors
            feedback.setText("Error creating account!");
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "An error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        dispose();
    }
}