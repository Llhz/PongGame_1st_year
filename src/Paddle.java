import java.awt.Color;
import java.awt.Graphics;

/**
 * Created by Lhz on 4/17/2017 AD.
 */
public class Paddle {
    public int padnum;
    public int x, y, w = 40, h = 280;
    public int score;
    public int padspeed;
    public Paddle(PongGame pong,int num, int sp) {
        this.padnum = num;
        padspeed = sp;
        if(padnum == 1) {
            this.x = 0;
        }
        else if(padnum == 2) {
            this.x = pong.width - this.w;
        }
        this.y = (pong.height - this.h)/ 2;
    }
    public void move(boolean u) { //Only need to move up or down, so lazy implement here;
        if(u) {
            if(this.y - this.padspeed > 0) {
                y -= padspeed;
            }
            else {
                y = 0;
            }
        }
        else {
            if(this.y + this.h + padspeed < PongGame.pong.height) {
                y += padspeed;
            }
            else {
                y = PongGame.pong.height - this.h;
            }
        }
    }
    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);
    }
}
