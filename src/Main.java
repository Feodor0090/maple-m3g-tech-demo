import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;

public class Main extends MIDlet {
    protected void startApp() {
        MainCanvas f = new MainCanvas(this);
        Display.getDisplay(this).setCurrent(f);
        new Thread(f).start();
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean unconditional) {
    }
}
