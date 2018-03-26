package sketches;

import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PSurface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class EmbeddedPApplet extends PApplet {
    // Keep track of otherwise package-private variables independently
    String renderer;
    int height;
    int width;

    EmbeddedPApplet() {
        createEmbeddedCanvas(this);
    }

    private static void createEmbeddedCanvas(EmbeddedPApplet applet) {
        //create your JFrame
        JFrame frame = new JFrame("Custom Processing JFrame");

        //create and initialize your sketch
//        ProcessingTestSettings applet = new ProcessingTestSettings();
        applet.init();

        //Catch exceptions thrown by the sketch
        try {
            frame.add(applet.getCanvasAWT());//get the Applet Canvas as a component and add it to the JFrame
            applet.getSurface().startThread(); //start the sketch
        } catch (NullPointerException | ClassCastException e) {
            System.out.println(e);
        }

        //Custom closeHandlers
        applyCustomClosingHandlers(frame, applet);

        /*make JFrame visible
        Apparently the frame can only be set to visible after all other modifications are made */
        frame.setLocationRelativeTo(null);
        frame.setSize(applet.width, applet.height);
        frame.setMinimumSize(new Dimension(300, 300));
        frame.setVisible(true);
    }

    private static void applyCustomClosingHandlers(JFrame frame, EmbeddedPApplet applet) {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                int confirm = JOptionPane.showOptionDialog(frame,
                        "Want to save all unsaved data?",
                        "Exit confirmation", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                switch (confirm) {
                    case JOptionPane.YES_OPTION:
                        System.out.println("Yes");
                        // Save data
                        applet.getSurface().stopThread();
                        applet.exit();
                        frame.dispose();
                        System.exit(0);
                        break;
                    case JOptionPane.NO_OPTION:
                        System.out.println("No");
                        applet.getSurface().stopThread();
                        applet.exit();
                        frame.dispose();
                        System.exit(0);
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        System.out.println("Cancel");
                        // Do nothing
                        break;
                }
            }
        });
    }

    /* Override size function as Processing as it required size to be in settings, yet processing seems to ignore settings.
    this allows for unedited Processing code to be to be run though height and width will be ignored */
    public void size(int width, int height, String renderer) {
//        System.out.println("Renderer = " + renderer);
        int testing = (int) 0.7;
        if (!renderer.equals(this.renderer)) {
            this.width = width;
            this.height = height;
            this.renderer = renderer;
        }
    }

    /* Override primary graphics constructor and set custom values,
    otherwise they will be ignored as sketchWidth(), sketchHeight(), and sketchRenderer() are final
    setting height and width seems to be irrelevant as the canvas component will automatically rescale*/
//    protected PGraphics createPrimaryGraphics() {
//        return this.makeGraphics(width, height, renderer, this.sketchOutputPath(), true);
//    }

    /* function to call the otherwise protected initSurface() function.
    Must be manually initialized after the applet has been created to prevent early termination*?*/
    protected void init() {
        System.out.println("Initialized");
        settings(); //This method of initialization doesn't call settings, so it must be called here.
        this.initSurface();
        this.setSize(width, height);
        this.renderer = P2D;
        renderer = sketchRenderer();
    }

    //depending on the chosen renderer, return the applicable canvas component.
    Object getCanvasWorking() {
        PSurface ps = this.getSurface();
        if (PConstants.JAVA2D.equals(renderer)) {
            return ps.getNative();
        } else if (PConstants.P2D.equals(renderer) || PConstants.P3D.equals(renderer)) {
            return ps.getNative();
        }
        throw new RuntimeException(renderer + " is not a valid Processing Renderer");
    }

    Component getCanvasAWT() {
        PSurface ps = this.getSurface();
        if (PConstants.JAVA2D.equals(renderer)) {
            return (Component) ps.getNative();
        } else if (PConstants.P2D.equals(renderer) || PConstants.P3D.equals(renderer)) {
            GLWindow joglWindow = (GLWindow) ps.getNative();
            return new NewtCanvasAWT(joglWindow);
        }
        throw new RuntimeException(renderer + " is not a valid Processing Renderer");
    }

    javax.swing.JComponent getJavaFXCanvas() {
        PSurface ps = this.getSurface();
//        panel.setPreferredSize(new Dimension(width, height));
        if (PConstants.JAVA2D.equals(renderer)) {
            return (JComponent) ps.getNative();
        } else if (PConstants.P2D.equals(renderer) || PConstants.P3D.equals(renderer)) {
            return (JComponent) ps.getNative();
        } else {
            throw new RuntimeException(renderer + " is not a valid Processing Renderer");
        }
    }

    JPanel getCanvasJPanel() { // pretty much useless as it limits usage to AWT
        PSurface ps = this.getSurface();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(width, height));
        panel.setBounds(0, 0, width, height);
        panel.setSize(new Dimension(width, height));
        panel.setMaximumSize(new Dimension(width, height));
        if (PConstants.JAVA2D.equals(renderer)) {
            panel.add((Component) ps.getNative(), BorderLayout.CENTER);
        } else if (PConstants.P2D.equals(renderer) || PConstants.P3D.equals(renderer)) {
            GLWindow joglWindow = (GLWindow) ps.getNative();
            panel.add(new NewtCanvasAWT(joglWindow), BorderLayout.CENTER);
        } else {
            throw new RuntimeException(renderer + " is not a valid Processing Renderer");
        }
        return panel;
    }
}