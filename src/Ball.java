import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 * Created by Lhz on 4/17/2017 AD.
 */
public class Ball {
    public int x, y, w, h;
    public int ballscore;
    public int ballsize;
    public int vx = 0, vy = 0;
    public int basespeed = 5;
    public int hitcount;
    public int wallcount;
    public String type;
    public boolean hitp1, hitp2;
    private PongGame pong;
    public Random rnd;
    public Ball(PongGame pong, int sp, int bz, int bs, String t) {
        this.pong = pong;
        this.basespeed = sp;
        this.ballscore = bs;
        this.ballsize = bz;
        this.w = this.ballsize;
        this.h = this.ballsize;
        this.type = t;
        rnd = new Random();
        spawn();
    }
    public void update(Paddle pad1, Paddle pad2) {
        //moving ball around
        this.x += vx * basespeed;
        this.y += vy * basespeed;
        //Check with top,down wall, low velocity change, only y direction
        if((this.y + this.h + vy*basespeed) > pong.height) { // hit down wall
            this.y = pong.height - this.h;
            this.vy -= wallcount/4; // wall absorb speed
            this.vy *= -1;
            if(this.vy >= 0) { // wall hug -> change back to 1
                this.vy = -1;
                wallcount = 0;
            }
            wallcount++;
        }
        else if(this.y + vy*basespeed < 0) { //hit top wall
            this.y = 0;
            this.vy += wallcount/4;
            this.vy *= -1;
            if(this.vy <= 0) {
                this.vy = 1;
                wallcount = 0;
            }
            wallcount++;
        }
        //Collision with pad -> Change velocity/Direction
        if(checkpadcol(pad1) == 1 && !hitp1) {
            hitp1 = true;
            hitp2 = false;
            this.vx = 2 + (hitcount/5);
            if(pong.p1up && pad1.y > 0) {
                this.vy -= pad1.padspeed/10;
                this.vx += pad1.padspeed/10;
            }
            else if(pong.p1dn && (pad1.y + pad1.h < pong.height)) {
                this.vy += pad1.padspeed/10;
                this.vx += pad1.padspeed/10;
            }
            else {
                this.vy *= -1;
                this.vx -= 1;
            }
            hitcount++;
            wallcount = 0;
        }
        else if(checkpadcol(pad2) == 1 && !hitp2) {
            hitp2 = true;
            hitp1 = false;
            this.vx = -2 - (hitcount/5);
            hitcount++;
            if((pong.p2up || (pong.botup && pong.AIEnable)) && pad2.y > 0) {
                this.vy -= pad2.padspeed/10;
                this.vx -= pad2.padspeed/10;
            }
            else if((pong.p2dn || (pong.botdn && pong.AIEnable)) && (pad2.y + pad2.h < pong.height)) {
                this.vy += pad2.padspeed/10;
                this.vx -= pad2.padspeed/10;
            }
            else {
                this.vy *=-1;
                this.vx += 1;
            }
            wallcount = 0;
        }
        //Score
        if(checkpadcol(pad1) == 2) {
            pad2.score += ballscore;
            if(this.type.equals("Normal")) {
                spawn();
            }
            else {
                this.ballscore = 0;
                this.ballsize = 0;
                this.basespeed = 0;
            }
        }
        else if(checkpadcol(pad2) == 2) {
            pad1.score += ballscore;
            if(this.type.equals("Normal")) {
                spawn();
            }
            else {
                this.ballscore = 0;
                this.ballsize = 0;
                this.basespeed = 0;
            }
        }
    }
    public void spawn() {
        hitcount = 0;
        wallcount = 0;
        this.hitp1 = false;
        this.hitp2 = false;
        this.x = (pong.width - this.w) / 2;
        this.y = (pong.height - this.h) / 2;
        if(type.equals("Extra")) { // random spawn spot for extra ball
            this.y = rnd.nextInt(pong.height*5/7) + pong.height/7;
        }
        //Random y direction veolocity after spawn
        this.vy = -5 + rnd.nextInt(10);
        //Random ball x direction (left or right)
        if(rnd.nextBoolean()) {
            vx = 1;
        }
        else {
            vx = -1;
        }
    }
    public int checkpadcol(Paddle pad) {
        if(pad.padnum == 1) { //check col with pad1
            if( (this.x < pad.x + pad.w) && (this.y < pad.y + pad.h) && (this.y + this.h > pad.y)) {
                return 1;
            }
        }
        if(pad.padnum == 2) { // check col with pad2
            if( (this.x + this.w > pad.x) && (this.y < pad.y + pad.h) && (this.y + this.h > pad.y )) {
                return 1;
            }
        }
        if(((pad.x > this.x + this.w) && (pad.padnum == 1)) || ((this.x > pad.x + pad.w) && (pad.padnum == 2))) { //check score both side
            return 2;
        }
        return 0; //base case
    }
    public void render(Graphics g) {
        g.setColor(Color.BLACK);
        if(this.type.equals("Extra")) {
            g.setColor(Color.GREEN);
        }
        g.fillOval(x, y, w, h);
    }
}