import java.awt.*;
import java.util.random.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;

public class newFlappybird extends JPanel implements ActionListener, KeyListener {
    int boardwidth = 360;
    int boardheight = 640;
    // var for img
    Image backgroundImage;
    Image birdImage;
    Image buildImage;
    Image botbuildImage;

    // bird size
    int birdx = boardwidth / 8;
    int birdy = boardheight / 2;
    int birdwidth = 42;
    int birdheight = 40;

    // to hold these vars easily we can create a class
    class bird {
        int x = birdx;
        int y = birdy;
        int width = birdwidth;
        int height = birdheight;
        Image img;

        bird(Image img) {
            this.img = img;
        }
    }

    // building
    int buildx = boardwidth;
    int buildy = 0;
    int buildwidth = 64;
    int buildheight = 512;

    class build {
        int x = buildx;
        int y = buildy;
        int width = buildwidth;
        int height = buildheight;
        Image img;
        boolean passed = false;

        build(Image img) {
            this.img = img;
        }

    }

    // game logic - feild for bird
    bird bird;
    int velx = -4;
    int velY = 0;
    int gravity = 1;

    Timer gameloop;

    // random placement of builds
    Random random = new Random();

    Timer placebuildTimer;
    boolean gameOver = false;
    ArrayList<build> builds;
    double score = 0;

    newFlappybird() {
        setPreferredSize(new Dimension(boardwidth, boardheight));
        // setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        // load images
        backgroundImage = new ImageIcon(getClass().getResource("./back.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./bird.png")).getImage();
        buildImage = new ImageIcon(getClass().getResource("./buildings.png")).getImage();
        botbuildImage = new ImageIcon(getClass().getResource("./bbuilding.png")).getImage();

        // bird
        bird = new bird(birdImage);
        // arraylist for buildings
        builds = new ArrayList<build>();
        // place build timer

        placebuildTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeBuild();
            }
        });
        placebuildTimer.start();

        // game timer
        gameloop = new Timer(1000 / 60, this);
        gameloop.start();
    }

    public void placeBuild() {
        // mathrandom = 0-1 * 512/2 --> 256
        // 0 - 128 - (0-256) ---> pipeheight -- 3/4 pipeheight
        int randbuildy = (int) (buildy - buildheight / 4 - Math.random() * (buildheight / 2));
        int openingSpace = boardheight / 4;

        build topbuild = new build(buildImage);
        topbuild.y = randbuildy;
        builds.add(topbuild);

        build botbuild = new build(botbuildImage);
        botbuild.y = topbuild.y + buildheight + openingSpace;
        builds.add(botbuild);
    }

    public void paintComponent(Graphics g) {
        // bcz of this a function of jpannel we should use super
        // super reffers to Jpannel
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        // background draw
        g.drawImage(backgroundImage, 0, 0, boardwidth, boardheight, null);
        // bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        // draw builds
        for (int i = 0; i < builds.size(); i++) {
            build build = builds.get(i);
            g.drawImage(build.img, build.x, build.y, build.width, build.height, null);
        }
        // score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over : " + String.valueOf((int) score), 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        // bird
        velY += gravity;
        bird.y += velY;
        bird.y = Math.max(bird.y, 0);

        // builds
        for (int i = 0; i < builds.size(); i++) {
            build build = builds.get(i);
            build.x += velx;

            if (!build.passed && bird.x > build.x + build.width) {
                build.passed = true;
                score += 0.5;
            }

            if (collision(bird, build)) {
                gameOver = true;
            }
        }

        if (bird.y > boardheight) {
            gameOver = true;
        }
    }

    public boolean collision(bird a, build b) {
        return a.x < b.x + b.width && // a's top left corner doesnt reach b's corner
                a.x + a.width > b.x && // a's top right corner passes b's top left corner
                a.y < b.y + b.height && // a's top left corner doesnt reach b's bottom left corner
                a.y + a.height > b.y; // a's bottom left corner passes b's top left corner

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            // stop producing more pipes
            placebuildTimer.stop();
            // stop creating frames
            gameloop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velY = -6;
        }
        if (gameOver) {
            // restart
            bird.y = birdy;
            velY = 0;
            builds.clear();
            score = 0;
            gameOver = false;
            gameloop.start();
            placebuildTimer.start();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
