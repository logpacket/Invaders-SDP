package screen;

import engine.FileManager;
import engine.Menu;
import engine.Sound;
import engine.SoundManager;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

public class CreditScreen extends Screen{

    private int currentFrame;
    private List<String> creditlist;
    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();

    public CreditScreen(final int width, final int height, final int fps){
        super(width, height, fps);

        this.menu = Menu.MAIN;
        this.currentFrame = 0;

        try{
            this.creditlist = FileManager.getInstance().loadCreditList();
            logger.info(""+this.creditlist);
        }  catch (NumberFormatException | IOException e) {
            logger.warning("Couldn't load credit list!");
        }
    }

    @Override
    protected final void update() {
        super.update();
        currentFrame++;

        if (currentFrame > 50 * 60) {//임시로 50초
            this.isRunning = false;
        }

        draw();
        if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)
                && this.inputDelay.checkFinished()) {
            this.isRunning = false;
            soundManager.playSound(Sound.MENU_BACK);
        }
    }

    private void draw(){
        renderer.initDrawing(this);
        renderer.drawEndingCredit(this,this.creditlist, currentFrame);
        renderer.completeDrawing(this);
    }

}
