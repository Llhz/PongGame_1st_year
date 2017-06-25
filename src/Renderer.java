import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 * Created by Lhz on 4/17/2017 AD.
 */
public class Renderer extends JPanel {
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        PongGame.pong.render((Graphics2D) g);
    }
}
