package project12.group19.api.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame implements ActionListener {

    JFrame frame;
    JPanel field;
    JLayeredPane support;
    JPanel panel;
    JLabel l0, l1, l2;
    JTextField fieldx;
    JTextField fieldy;
    Font f1, f2;
    JButton hit, restart;
    JLabel ballLabel, infoPosition;
    ImageIcon image, background;
    GrassComponent grassCom;

    public GUI() {

        frame = new JFrame();
        panel = new JPanel();
        field = new JPanel();
        support = new JLayeredPane();
        l0 = new JLabel("Input your values:");
        l1 = new JLabel("X Velocity:");
        l2 = new JLabel("Y Velocity:");
        ballLabel = new JLabel();
        infoPosition = new JLabel("Current position: x = 0, y = 0, z = 0");
        fieldx = new JTextField();
        fieldy = new JTextField();
        f1 = new Font("Times New Roman", Font.ITALIC, 20);
        f2 = new Font("Times New Roman", Font.PLAIN, 25);
        hit = new JButton("HIT");
        restart = new JButton("RESTART");

        field.setLayout(null);
        field.setBounds(0, 0, 600, 600);

        background = new ImageIcon(getClass().getResource("fieldBack.png"));

        image = new ImageIcon(getClass().getResource("golfBall.png"));
        ballLabel.setIcon(image);
        ballLabel.setBounds(50, 50, 40, 40);

        infoPosition.setBounds(10, 530, 300, 20);
        infoPosition.setForeground(Color.WHITE);
        infoPosition.setFont(f1);

        frame.setLayout(null);
        // JLabel temp = new JLabel();
        // temp.setIcon(background);
        // frame.setContentPane(temp);
        // frame.setSize(900,600);

        l0.setForeground(new Color(33, 38, 41));
        l1.setForeground(new Color(33, 38, 41));
        l2.setForeground(new Color(33, 38, 41));

        l0.setFont(f2);
        l1.setFont(f1);
        l2.setFont(f1);

        l0.setBounds(10, 15, 285, 35);
        l1.setBounds(75, 70, 150, 35);
        fieldx.setBounds(75, 100, 150, 35);
        l2.setBounds(75, 145, 150, 35);
        fieldy.setBounds(75, 175, 150, 35);

        hit.setBounds(45, 450, 200, 40);
        hit.setBackground(new Color(36, 81, 107));
        hit.setOpaque(true);
        hit.addActionListener(this);
        hit.setFont(f1);

        restart.setBounds(45, 510, 200, 40);
        restart.setBackground(new Color(36, 81, 107));
        restart.setOpaque(true);
        restart.setFont(f1);

        panel.setBounds(600, 0, 300, 600);
        panel.setBackground(new Color(141, 191, 214));
        panel.setLayout(null);

        grassCom = new GrassComponent();
        field.setLayout(new GridLayout(1, 1));
        field.add(grassCom);
        // field.add(ballLabel);
        support.setBounds(0, 0, 600, 600);
        support.setBounds(0, 0, 600, 600);
        support.add(field, Integer.valueOf(0));
        support.add(ballLabel, Integer.valueOf(1));

        panel.add(fieldx);
        panel.add(fieldy);
        panel.add(l1);
        panel.add(l2);
        panel.add(l0);
        panel.add(hit);
        panel.add(restart);
        // frame.add(ballLabel);
        // frame.add(infoPosition);

        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(panel);
        frame.add(support);
        frame.setTitle("Golf");
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        new GUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == hit) {
            ballLabel.setLocation(100, 100);
            grassCom.repaint();

        }

    }
}
