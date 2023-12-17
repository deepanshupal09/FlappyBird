import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Random;

class MyCanvas extends Canvas implements KeyListener, MouseListener {
    private int birdX,flagPoint, birdY, birdVelocity, pipesX[], pipesY[], pipeHeight[],score;
    private final int birdSize, heightLow, heightUpper,pipeVelocity;
    private boolean game;
    private BufferedImage offScreenImage;

    public MyCanvas() {
        birdX = 100;
        birdY = 250;
        birdSize = 20;
        birdVelocity = 0;
        pipeVelocity = 3;
        game = false;
        heightLow = 150;
        heightUpper = 250;
        pipesX = new int[6];
        pipesY = new int[6];
        flagPoint=0;
        pipeHeight = new int[6];
        score=0;

        createPipes();

        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
        requestFocus();
        setBackground(new Color(78, 193, 202));
        setSize(800, 600);


        Timer timer = new Timer(8, e -> {
            if(pipesX[0]<100||pipesX[1]<100){
                if(flagPoint==0){
                    score++;
                    flagPoint++;
                }
            }
            if (((pipesX[0]<=120&&pipesX[0]>=50)&&((birdY<=pipeHeight[0]+30)||(birdY+20>(600 - (pipeHeight[3]+70)))))||((pipesX[1]<=120&&pipesX[1]>=30)&&((birdY<=pipeHeight[1]+30)||(birdY+20>(600 - (pipeHeight[4]+70)))))||(birdY > 508 || birdY < 30) ){
                int result = JOptionPane.showOptionDialog(
                        null,
                        "Game Over!\nYour score is " + score +"\nDo you want to retry?",
                        "Game Over",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new Object[]{"Yes","No"},
                        ""
                );

                if (result == JOptionPane.YES_OPTION) {
                    birdX = 100;
                    birdY = 250;
                    birdVelocity = 0;
                    game = false;
                    pipesX = new int[6];
                    pipesY = new int[6];
                    flagPoint=0;
                    pipeHeight = new int[6];
                    score=0;
                    createPipes();
                    repaint();
                }
                else{
                    System.exit(0);
                }

            }
            if (game) {
                movePipes();
                if (birdVelocity < 0) {
                    mov(birdX, birdY + birdVelocity);
                } else {
                    mov(birdX, birdY + (birdVelocity++) / 5);
                }
            }

        });
        timer.start();
    }

    private void createPipes() {
        Random r = new Random();
        for (int i = 0; i < 3; i++) {
            pipeHeight[i] = r.nextInt(heightUpper - heightLow + 1) + heightLow;
            pipesY[i] =  30;
            pipesX[i] = 600 + 300 * (i % 3);
            pipeHeight[i+3] = 350-pipeHeight[i];
            pipesY[i+3] =(600 - (pipeHeight[i+3] + 70));
            pipesX[i+3] = 600 + 300 * ((i+3) % 3);
        }
    }

    private void destroyPipe() {
        flagPoint=0;
        for(int i=1;i<3;i++) {
            pipesX[i-1] = pipesX[i];
            pipesY[i-1] = pipesY[i];
            pipesX[i+3-1] = pipesX[i+3];
            pipesY[i+3-1] = pipesY[i+3];
            pipeHeight[i-1] = pipeHeight[i];
            pipeHeight[i+3-1] = pipeHeight[i+3];
        }
        pipeHeight[2] = new Random().nextInt(heightUpper - heightLow + 1) + heightLow;
        pipeHeight[5] = 350-pipeHeight[2];
        pipesX[2] = 900;
        pipesY[2] = 30;
        pipesX[5] = 900;
        pipesY[5] = 600 - (pipeHeight[5]+70);

    }


    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        if (offScreenImage == null) {
            offScreenImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        }

        Graphics offScreenGraphics = offScreenImage.getGraphics();
        //background
        offScreenGraphics.setColor(new Color(78, 193, 202));
        offScreenGraphics.fillRect(0, 0, getWidth(), getHeight());

        // boundaries
        offScreenGraphics.setColor(new Color(250, 183, 45));
        offScreenGraphics.fillRect(0, 0, getWidth(), 30);
        offScreenGraphics.fillRect(0, 530, getWidth(), 50);

        //pipes
        offScreenGraphics.setColor(new Color(115, 190, 46));
        for (int i = 0; i < 6; i++) {
            offScreenGraphics.fillRect(pipesX[i], pipesY[i], 50, pipeHeight[i]);
        }

        // chidiya
        offScreenGraphics.setColor(new Color(250, 183, 45));
        offScreenGraphics.fillRect(birdX, birdY, birdSize, birdSize);

        // score
        offScreenGraphics.setColor(new Color(255,255,255));
        offScreenGraphics.setFont(new Font("Elephant",Font.PLAIN, 70));
        offScreenGraphics.drawString(""+score,380,130);

        //drawing image
        g.drawImage(offScreenImage, 0, 0, this);
    }

    private void mov(int x, int y) {
        birdX = x;
        birdY = y;
        repaint();


    }

    private void movePipes() {
        for(int i=0;i<6;i++) {
            pipesX[i] -= pipeVelocity;
        }
        if (pipesX[0] < -50) {
            destroyPipe();
        }
        repaint();
    }

    private void updateVelocity() {
        Timer timer = new Timer(100, e -> {
            if (birdVelocity < 5) {
                birdVelocity += 2;
                updateVelocity();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    int count = 0;

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            game = true;
            if(birdVelocity<0) {
                birdVelocity=Math.max(-10,birdVelocity-6);
            }
            else {
                birdVelocity=-6;
            }
            updateVelocity();
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        game = true;
        if(birdVelocity<0){
            birdVelocity=Math.max(-10,birdVelocity-6);
        }
        else{
            birdVelocity=-6;
        }
        updateVelocity();
    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }
}

class FlappyBird extends JFrame {
    private MyCanvas c;

    public FlappyBird() {
        c = new MyCanvas();
        add(c);

        requestFocus();
        setFocusable(true);
        setVisible(true);
        setResizable(false);
        setTitle("Flappy Bird");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args) {
        new FlappyBird();
    }
}