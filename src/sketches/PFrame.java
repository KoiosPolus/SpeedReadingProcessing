package sketches;

public class PFrame extends EmbeddedPApplet {
    @Override
    public void settings() {
        size(300, 300, P2D);
    }

    @Override
    public void setup() {

    }

    @Override
    public void draw() {
        background(255, 0, 0);
        ellipse(mouseX, mouseY, 10, 20);
    }
}
