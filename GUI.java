import javax.swing.*;
import java.awt.*;

public class GUI {

    // Initializing the Objects
    JFrame frame;
    JPanel field, panel;
    JLayeredPane support;
    JLabel l0, l1, l2, ballLabel, infoPosition, message;
    JTextField fieldx, fieldy;
    JButton hit, restart;
    Font f1, f2;
    ImageIcon image;
    GrassComponent grassCom;
    int counter = 0;

    //get from config
    int ballX = 0;
    int ballY = 0;
    int ballZ = 0;
    
    /**
     * Constructor of the graphic interface of the
     * golf game. Objects and created and added to
     * JPanel and JFrame.
     */
    public GUI() {

        // Creating the Objects
        frame = new JFrame();
        panel = new JPanel();
        field = new JPanel();
        support = new JLayeredPane();
        l0 = new JLabel("Input your values:");
        l1 = new JLabel("X Velocity:");
        l2 = new JLabel("Y Velocity:");
        message = new JLabel();
        ballLabel = new JLabel();
        infoPosition = new JLabel("Current position: x = 0, y = 0, z = 0");
        fieldx = new JTextField();
        fieldy = new JTextField();
        f1 = new Font("Times New Roman", Font.ITALIC, 20);
        f2 = new Font("Times New Roman", Font.PLAIN, 25);
        hit = new JButton("HIT");
        restart = new JButton("RESTART");
        image = new ImageIcon(getClass().getResource("golfBall.png"));
        grassCom = new GrassComponent();

        message.setForeground(new Color(33, 38, 41));
        message.setFont(f2);
        message.setBounds(30, 275, 240, 80);

        field.setLayout(null);
        field.setBounds(0, 0, 600, 600);

        ballLabel.setIcon(image);
        ballLabel.setBounds(280, 280, 40, 40);

        infoPosition.setBounds(10, 570, 400, 20);
        infoPosition.setForeground(Color.WHITE);
        infoPosition.setFont(f1);

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
        hit.setBackground(Color.white);
        hit.setOpaque(true);
        hit.addActionListener(e -> hitBall());
        hit.setFont(f1);

        restart.setBounds(45, 510, 200, 40);
        restart.setBackground(Color.WHITE);
        restart.setOpaque(true);
        restart.setFont(f1);
        restart.addActionListener(e -> {
            ballLabel.setLocation(10, 20);
            grassCom.repaint();
        });

        // Setting the user-friendly Panel
        panel.setBounds(600, 0, 300, 600);
        panel.setBackground(new Color(141, 191, 214));
        panel.setLayout(null);

        // Setting the game Panel
        field.setLayout(new GridLayout(1, 1));
        field.add(grassCom);
        support.setBounds(0, 0, 600, 600);
        support.setBounds(0, 0, 600, 600);

        support.add(field, Integer.valueOf(0));

        support.add(ballLabel, Integer.valueOf(1));
        support.add(infoPosition, Integer.valueOf(2));

        // Adding Objects to the user-friendly Panel
        panel.add(fieldx);
        panel.add(fieldy);
        panel.add(l1);
        panel.add(l2);
        panel.add(l0);
        panel.add(hit);
        panel.add(restart);
        panel.add(message);

        // Setting the Frame and adding Panels to it
        frame.setSize(900, 628);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(panel);
        frame.add(support);
        frame.setTitle("Golf");
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setVisible(true);

    }

    /**
     * Main method to launch the gold game program.
     *
     * @param args an array of Strings passed as
     *             parameters when you are running your
     *             application through command line in the OS.
     */
    public static void main(String[] args) {
        new GUI();
    }

    /**
     * The ball is hit once the button "HIT"
     * is clicked. Solver function calculates
     * the position of the ball after the hit
     * and this method places it accordingly.
     */
    public void hitBall() {

        int xVel = Integer.parseInt(fieldx.getText());
        int yVel = Integer.parseInt(fieldy.getText());
        System.out.println(xVel + "  " + yVel);
        //input for velocity to solver


        //assign result values from solver
        int newX = xVel;
        int newY = yVel;
        int newZ; //get from solver/engine

        infoPosition.setText("Current position: x = " + newX + ", y = " + newY + ", z = 0");
        //multiply by 6 both values here
        newX = newX * 12;
        newY = newY * 12;

        //adjust to panel coordinates
        newX = GrassComponent.coorToSwingX(newX);
        newY = GrassComponent.coorToSwingY(newY);

        System.out.println(newX + "  " + newY); // we'll leave it here now for testing

        ballLabel.setLocation(newX - 20, newY - 20);
        grassCom.repaint();

        int state = 0; //get from solver/engine
        if (counter < 3) {
            switch (state) {
                case 1:
                    counter++;
                    message.setText("<html>" + "The ball is out of field. <br> Try again. <br> You have " +
                            (4 - counter) + " tries left.");
                    break;
                case 2:
                    message.setText("<html>" + "You have " + (4 - counter) + " tries left.");
                    break;
                case 3:
                    message.setText("");
                    JFrame win = new JFrame();
                    JLabel youWon = new JLabel();

                    youWon.setText("Congratulations! You won!");
                    youWon.setBounds(10, 15, 280, 30);
                    youWon.setFont(f2);
                    youWon.setForeground(new Color(33, 38, 41));

                    win.setSize(300, 100);
                    win.getContentPane().setBackground(new Color(141, 191, 214));
                    win.setResizable(false);
                    win.setDefaultCloseOperation(win.DISPOSE_ON_CLOSE);
                    win.setLayout(null);
                    win.add(youWon);
                    win.setLocationRelativeTo(null);
                    win.setTitle("WIN! WIN! WIN!");
                    win.setVisible(true);
                    break;
            }
        } else {
            message.setText("");
            JFrame loss = new JFrame();
            JLabel youLose = new JLabel();

            youLose.setText("You are a loser :(");
            youLose.setBounds(10, 15, 280, 30);
            youLose.setFont(f2);
            youLose.setForeground(new Color(33, 38, 41));

            loss.setSize(300, 100);
            loss.getContentPane().setBackground(new Color(141, 191, 214));
            loss.setResizable(false);
            loss.setDefaultCloseOperation(loss.DISPOSE_ON_CLOSE);
            loss.setLayout(null);
            loss.add(youLose);
            loss.setLocationRelativeTo(null);
            loss.setTitle("LOSER");
            loss.setVisible(true);
        }
    }
}
    