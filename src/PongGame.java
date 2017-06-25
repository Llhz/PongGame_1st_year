import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

/**
 * Created by Lhz on 4/17/2017 AD.
 */
public class PongGame implements ActionListener, KeyListener {
    public static PongGame pong;
    public int width = 1280, height = 720;
    public Renderer renderer;
    public JFrame jFrame;
    public Paddle pad1;
    public Paddle pad2;
    public Ball ball;
    public Ball[] exball, exball2;
    public boolean p1up, p1dn, p2up, p2dn, botup, botdn;
    public int gamestate = 0;
    public int winsc = 100, pwin = 0;
    public int ballspeed = 5, ballsize = 50, ballscore = 10, extraball = 10;
    public int pad1sp = 30, pad2sp = 30;
    public boolean extramode = false , firstbonus = true, secondbonus = true;
    public long startTime;
    public boolean AIEnable = false;
    public int timerbonus = 20;
    public PongGame() {
        Timer timer = new Timer(20, this);
        jFrame = new JFrame("PongGame");
        renderer = new Renderer();
        jFrame.setSize(width, height);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.add(renderer);
        jFrame.addKeyListener(this);
        timer.start();
    }
    public void start() {
        gamestate = 1;
        pad1 = new Paddle(this, 1, pad1sp);
        pad2 = new Paddle(this, 2, pad2sp);
        ball = new Ball(this, ballspeed, ballsize, ballscore, "Normal");
        startTime = System.nanoTime();
    }
    public void update() {
        long duration = System.nanoTime() - startTime;
        duration /= 1000000000;
        if(pad1.score >= winsc) {
            pwin = 1;
            gamestate = 2;
        }
        else if(pad2.score >= winsc) {
            pwin = 2;
            gamestate = 2;
        }
        if(p1up) {
            pad1.move(true);
        }
        if(p1dn) {
            pad1.move(false);
        }
        if(p2up && !AIEnable) {
            pad2.move(true);
        }
        if(p2dn && !AIEnable) {
            pad2.move(false);
        }
        //Simple AI here; , check center of ball, pad and move accordingly
        if(AIEnable) {
            if((pad2.y + (pad2.y + pad2.h))/2 > (ball.y + (ball.y + ball.h))/2) {
                pad2.move(true);
                botup = true;
                botdn = false;
            }
            else if((pad2.y + (pad2.y + pad2.h))/2 < (ball.y + (ball.y + ball.h))/2) {
                pad2.move(false);
                botup = false;
                botdn = true;
            }
        }
        ball.update(pad1, pad2);
        if(extramode && firstbonus && (pad1.score >= winsc/2 || pad2.score >= winsc/2)) { //Create new exball for first time
            firstbonus = false;
            int exbspd = ballspeed, exbs = ballsize/2, exbsc = ballscore/5;
            exball = new Ball[extraball];
            Random rnd = new Random();
            for(int i = 0; i < extraball; ++i) {
                int rndspd = rnd.nextInt(extraball) + 1;
                rndspd /= 5;
                exbspd += rndspd;
                exball[i] = new Ball(this, exbspd, exbs, exbsc, "Extra");
            }
        }
        if(extramode && (pad1.score >= winsc/2 || pad2.score >= winsc/2)) { //render exball
            for(int i = 0; i < extraball; ++i) {
                exball[i].update(pad1, pad2);
            }
        }
        if(extramode && secondbonus && duration >= timerbonus) {
            secondbonus = false;
            int exbspd = ballspeed, exbs = ballsize/2, exbsc = ballscore/5;
            exball2 = new Ball[extraball];
            Random rnd = new Random();
            for(int i = 0; i < extraball; ++i) {
                int rndspd = rnd.nextInt(extraball) + 1;
                rndspd /= 5;
                exbspd += rndspd;
                exball2[i] = new Ball(this, exbspd, exbs, exbsc, "Extra");
            }
        }
        if(extramode && duration >= timerbonus) {
            for(int i = 0; i < extraball; ++i) {
                exball2[i].update(pad1, pad2);
            }
        }
    }
    public void render(Graphics2D g) {
        if(gamestate == 0) { //Main Menu
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, width/16));
            g.drawString("PongGame", width / 4 + 100, height/9);
            g.setFont(new Font("Arial", 1, width/32));
            g.drawString("Press Space to Play", width / 3  , height / 2 + 50);
            g.drawString("Press Shift to config", width / 3  , height / 2 + 100);
            String ai = (AIEnable ? "ON" : "OFF");
            g.drawString("Press X to toggle AI : " + ai, width / 3  , height / 2 + 150);
        }
        else if(gamestate == 1) { //Game
            long duration = System.nanoTime() - startTime;
            duration /= 1000000000;
            g.setColor(Color.PINK);
            g.fillRect(0,0, width/2, height);
            g.setColor(Color.CYAN);
            g.fillRect(width/2, 0, width, height);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2f));
            g.drawOval(width / 2 - 200, height / 2 - 200, 400, 400);
            g.drawLine(width/2, 0, width/2, height);
            pad1.render(g);
            pad2.render(g);
            ball.render(g);
            if(extramode && (pad1.score >= winsc/2 || pad2.score >= winsc/2)) {
                for(int i = 0; i < extraball; ++i) {
                    exball[i].render(g);
                }
            }
            if(extramode && duration >= timerbonus) {
                for(int i = 0; i < extraball; ++i) {
                    exball2[i].render(g);
                }
            }
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, width/32));
            g.drawString("Score", width / 2 - 60  , height/15);
            g.drawString(String.valueOf(pad1.score), width / 2 - 100, height/9);
            g.drawString(String.valueOf(pad2.score), width / 2 + 75, height/9);
        }
        else if(gamestate == 2) { //Result
            g.setColor(Color.PINK);
            g.fillRect(0,0, width/2, height);
            g.setColor(Color.CYAN);
            g.fillRect(width/2, 0, width, height);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, width/16));
            g.drawString("PongGame", width / 4 + 100, height/9);
            g.setFont(new Font("Arial", 1, width/32));
            g.drawString("Player " + pwin + " Wins!", width / 2 - 150, height/3);
            g.drawString("Score : " + pad1.score + " - " + pad2.score, width / 2 - 150, height/3 + 100);
            g.drawString("Press Space to Play Again", width / 2 - 225, height / 2 + 50);
            g.drawString("Press Escape for Main menu", width / 2 - 250, height / 2 + 100);
            firstbonus = true;
            secondbonus = true;
        }
        else if(gamestate == 3) { //Option
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, width/16));
            g.drawString("Configuration", width / 4 + 50, height/9);
            g.setFont(new Font("Arial", 1, width/32));
            String ext = (extramode ? "ON" : "OFF");
            g.drawString(" Press X to toggle Extra Mode :  " + ext , width / 2 - 325, height / 4);
            g.drawString("(H)<< Winning Score : " + winsc + " >>(L)", width / 2 - 300, height / 3);
            g.drawString("(R)<< Amount of extraball : " + extraball + " >>(T)", width / 2 - 350, height / 3 + 50);
            g.drawString("(J)<< BallSpeed : " + ballspeed + " >>(K)", width / 2 - 250, height / 3 + 100);
            g.drawString("(V)<< BallSize : " + ballsize + " >>(M)", width / 2 - 250, height / 3 + 150);
            g.drawString("(B)<< Score per Ball : " + ballscore + " >>(N)", width / 2 - 300, height / 3 + 200);
            g.drawString("(Y)<< Paddle 1 Speed : " + pad1sp + " >>(O)", width / 2 - 300, height / 3 + 250);
            g.drawString("(U)<< Paddle 2 Speed : " + pad2sp + " >>(I)", width / 2 - 300, height / 3 + 300);
            g.drawString("(F)<< Time Interval for Extra mode : " + timerbonus + " Second >>(G)", width / 2 - 500, height / 3 + 350);
        }
    }
    public static void main(String[] args) {
        pong = new PongGame();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(gamestate == 1) {
            update();
        }
        renderer.repaint();
    }
    @Override
    public void keyPressed(KeyEvent e) {
        int keycode = e.getKeyCode();
        if(keycode == KeyEvent.VK_W) {
            p1up = true;
        }
        if(keycode == KeyEvent.VK_S) {
            p1dn = true;
        }
        if(keycode == KeyEvent.VK_UP) {
            p2up = true;
        }
        if(keycode == KeyEvent.VK_DOWN) {
            p2dn = true;
        }
        if(keycode == KeyEvent.VK_SPACE && (gamestate == 0 || gamestate == 2)) {
            start();
        }
        if(keycode == KeyEvent.VK_ESCAPE) {
            gamestate = 0;
        }
        if(keycode == KeyEvent.VK_SHIFT) {
            gamestate = 3;
        }
        if(keycode == KeyEvent.VK_H && gamestate == 3) {
            winsc--;
            if(winsc < 1) {
                winsc = 1;
            }
        }
        if(keycode == KeyEvent.VK_L && gamestate == 3) {
            winsc++;
        }
        if(keycode == KeyEvent.VK_J && gamestate == 3) {
            ballspeed--;
            if(ballspeed < 1) {
                ballspeed = 1;
            }
        }
        if(keycode == KeyEvent.VK_K && gamestate == 3) {
            ballspeed++;
        }
        if(keycode == KeyEvent.VK_V && gamestate == 3) {
            ballsize--;
            if(ballsize < 1) {
                ballsize = 1;
            }
        }
        if(keycode == KeyEvent.VK_M && gamestate == 3) {
            ballsize++;
        }
        if(keycode == KeyEvent.VK_B && gamestate == 3) {
            ballscore--;
            if(ballscore < 1) {
                ballscore = 1;
            }
        }
        if(keycode == KeyEvent.VK_N && gamestate == 3) {
            ballscore++;
        }
        if(keycode == KeyEvent.VK_R && gamestate == 3) {
            extraball--;
            if(extraball < 1) {
                extraball = 1;
            }
        }
        if(keycode == KeyEvent.VK_T && gamestate == 3) {
            extraball++;
        }
        if(keycode == KeyEvent.VK_Y && gamestate == 3) {
            pad1sp--;
            if(pad1sp < 1) {
                pad1sp = 1;
            }
        }
        if(keycode == KeyEvent.VK_O && gamestate == 3) {
            pad1sp++;
        }
        if(keycode == KeyEvent.VK_U && gamestate == 3) {
            pad2sp--;
            if(pad2sp < 1) {
                pad2sp = 1;
            }
        }
        if(keycode == KeyEvent.VK_I && gamestate == 3) {
            pad2sp++;
        }
        if(keycode == KeyEvent.VK_X && gamestate == 3) {
            extramode = !extramode;
        }
        if(keycode == KeyEvent.VK_F && gamestate == 3) {
            timerbonus--;
            if(timerbonus < 1) {
                timerbonus = 1;
            }
        }
        if(keycode == KeyEvent.VK_G && gamestate == 3) {
            timerbonus++;
        }
        if(keycode == KeyEvent.VK_X && gamestate == 0) {
            AIEnable = !AIEnable;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int keycode = e.getKeyCode();
        if(keycode == KeyEvent.VK_W) {
            p1up = false;
        }
        if(keycode == KeyEvent.VK_S) {
            p1dn = false;
        }
        if(keycode == KeyEvent.VK_UP) {
            p2up = false;
        }
        if(keycode == KeyEvent.VK_DOWN) {
            p2dn = false;
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }
}
