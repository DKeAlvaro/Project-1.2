package project12.group19.api.ui;

import project12.group19.api.domain.Player;
import project12.group19.api.domain.State;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.motion.MotionState;
import project12.group19.support.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class GUI implements Renderer {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;

    public static final CoordinateTranslator TRANSLATOR = new CoordinateTranslator(12, 12, WIDTH, HEIGHT);

    private final HitTransmitter transmitter = new HitTransmitter();
    private final HeightProfile surface;

    // Initializing the Objects
    JFrame frame;
    JPanel field, panel;
    JLayeredPane support;
    JLabel l0, l1, l2, ballLabel, infoPosition, message;
    JLabel statusPositionLabel;
    JTextPane statusPositionMessage;
    JLabel statusGameState;
    JTextField fieldx, fieldy;
    JButton hit, restart;
    Font f1, f2;
    ImageIcon image;
    GrassComponent grassCom;
    int counter = 0;

    //get from config
    int ballX;
    int ballY;
    int ballZ;

    int targetX;
    int targetY;
    double targetR;

    int initialX;
    int initialY;

    /**
     * Constructor of the graphic interface of the
     * golf game. Objects and created and added to
     * JPanel and JFrame.
     */
    public GUI(HeightProfile surface, int targetX, int targetY, double targetR, int initialX, int initialY, int initialZ) {
        this.surface = surface;
        // Configuring the locations of ball and target
        ballX = initialX * 12;
        ballY = initialY * 12;
        ballZ = initialZ;

        this.initialX = initialX * 12;
        this.initialY = initialY * 12;

        System.out.println("after constructor call: " + ballX + " " + ballY);

        ballX = GrassComponent.coorToSwingX(ballX) - 20;
        ballY = GrassComponent.coorToSwingY(ballY) - 20;

        System.out.println("after transformation: " + ballX + " " + ballY);

        this.targetX = targetX;
        this.targetY = targetY;
        this.targetR = targetR;

        // Creating the Objects
        frame = new JFrame();
        panel = new JPanel();
        field = new JPanel();
        support = new JLayeredPane();
        support.setVisible(true);
        l0 = new JLabel("Input your values:");
        l1 = new JLabel("X Velocity:");
        l2 = new JLabel("Y Velocity:");
        message = new JLabel();

        statusPositionLabel = new JLabel();
        statusPositionLabel.setText("Position:");
        statusPositionLabel.setBackground(new Color(141, 191, 214));
        statusPositionLabel.setVisible(true);
        statusPositionLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPositionLabel.setBounds(10, 250, 200, 50);
        statusPositionMessage = new JTextPane();
        statusPositionMessage.setBackground(new Color(141, 191, 214));
        statusPositionMessage.setBounds(30, 300, 200, 50);
        statusGameState = new JLabel();
        statusGameState.setHorizontalAlignment(SwingConstants.LEFT);
        statusGameState.setBackground(new Color(141, 191, 214));
        statusGameState.setBounds(10, 400, 200, 50);

        ballLabel = new JLabel();
        infoPosition = new JLabel();
        fieldx = new JTextField();
        fieldy = new JTextField();
        f1 = new Font("Times New Roman", Font.ITALIC, 20);
        f2 = new Font("Times New Roman", Font.PLAIN, 25);
        hit = new JButton("HIT");
        restart = new JButton("RESTART");
        image = new ImageIcon(ResourceLoader.load("golfBall.png"));
        grassCom = new GrassComponent(surface, targetX, targetY, targetR);

        message.setForeground(new Color(33, 38, 41));
        message.setFont(f2);
        message.setBounds(30, 275, 240, 80);

        field.setLayout(null);
        field.setBounds(0, 0, 600, 600);

        ballLabel.setIcon(image);
        ballLabel.setBounds(ballX, ballY, 40, 40);

        infoPosition.setBounds(10, 570, 400, 20);
        infoPosition.setForeground(Color.WHITE);
        infoPosition.setText("Current position: x = " + initialX + ", y = "+ initialY + ", z = " + initialZ);
        infoPosition.setFont(f1);

        statusGameState.setFont(f2);
        statusPositionLabel.setFont(f2);

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

        hit.setBounds(45, 510, 200, 40);
        hit.setBackground(Color.white);
        hit.setOpaque(true);
        hit.setFocusable(false);
//        hit.addActionListener(e -> hitBall());
        hit.addActionListener(e -> {
            transmitter.record(Player.Hit.create(
                    Double.parseDouble(fieldx.getText()),
                    Double.parseDouble(fieldy.getText())
            ));
        });
        hit.setFont(f1);

        restart.setBounds(45, 510, 200, 40);
        restart.setBackground(Color.WHITE);
        restart.setOpaque(true);
        restart.setFont(f1);
        restart.setFocusable(false);
//        restart.addActionListener(e -> {
//            ballLabel.setLocation(initialX, initialY);
//            grassCom.repaint();
//        });

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
//        support.add(infoPosition, Integer.valueOf(2));

        // Adding Objects to the user-friendly Panel
        panel.add(fieldx);
        panel.add(fieldy);
        panel.add(l1);
        panel.add(l2);
        panel.add(l0);

        panel.add(message);
        panel.add(hit);
        panel.add(statusPositionLabel);
        panel.add(statusPositionMessage);
        panel.add(statusGameState);
        //panel.add(restart);

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

//        // Configuring the locations of ball and target
//        ballX = initialX;
//        ballY = initialY;
//        ballZ = initialZ;
//
//        System.out.println(" after constructor call: " + ballX + " " + ballY);
//
//        ballX = GrassComponent.coorToSwingX(initialX)-20;
//        ballY = GrassComponent.coorToSwingX(initialY)-20;
//
//        System.out.println("after transformation: " + ballX + " " + ballY);
//
//        this.targetX = targetX;
//        this.targetY = targetX;
//        this.targetR = targetR;

    }

    /**
     * Main method to launch the gold game program.
     *
     * @param args an array of Strings passed as
     *             parameters when you are running your
     *             application through command line in the OS.
     */
    public static void main(String[] args) {
        new GUI((x, y) -> 1 + 0.1 * x, -10,18,2,-10,18,5);
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
        System.out.println("Velocity from field: " + xVel + "  " + yVel);
        //input for velocity to solver


        //assign result values from solver
        int newX = xVel;
        int newY = yVel;
        int newZ = 0; //get from solver/engine

        infoPosition.setText("Current position: x = " + newX + ", y = " + newY + ", z = " + newZ);
        //multiply by 6 both values here
        newX = newX * 12;
        newY = newY * 12;

        //adjust to panel coordinates
        newX = GrassComponent.coorToSwingX(newX);
        newY = GrassComponent.coorToSwingY(newY);

        System.out.println("From coordinates to Swing: " + (newX-20) + "  " + (newY-20)); // we'll leave it here now for testing

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

    @Override
    public void render(State state) {
        MotionState ballState = state.getBallState();
        if (!state.isStatic() && !state.isTerminal()) {
            statusPositionMessage.setText(String.format(
                    "x=%.3f\ny=%.3f\nz=%.3f",
                    ballState.getXPosition(),
                    ballState.getYPosition(),
                    surface.getHeight(ballState.getXPosition(), ballState.getYPosition())
            ));
        }

        if (state.isTerminal()) {
            statusGameState.setText(state.getFouls() < 4 ? "Won!" : "Lost!");
        } else if (state.getFouls() > 0 && state.getFouls() < 4) {
            statusGameState.setText("Fouls: " + state.getFouls() + "/3");
        }

        ballLabel.setLocation(
                TRANSLATOR.toPixelX(ballState.getXPosition()) - (image.getIconWidth() / 2),
                TRANSLATOR.toPixelY(ballState.getYPosition()) - (image.getIconHeight() / 2)
        );

        grassCom.repaint();
    }

    public Player getController() {
        return transmitter;
    }

    public static class HitTransmitter implements Player {
        private final AtomicReference<Hit> memory = new AtomicReference<>();

        @Override
        public Optional<Hit> play(State state) {
            return Optional.ofNullable(memory.getAndSet(null));
        }

        protected void record(Hit hit) {
            memory.set(hit);
        }
    }
}
