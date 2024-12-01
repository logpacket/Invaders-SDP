package engine;

import screen.Screen;

import javax.swing.*;
import java.awt.*;

/**
 * Implements a frame to show screens on.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
@SuppressWarnings("serial")
public class Frame extends JFrame {

	/** Frame width. */
	private final int width;
	/** Frame height. */
	private final int height;

    /**
	 * Initializes the new frame.
	 * 
	 * @param width
	 *            Frame width.
	 * @param height
	 *            Frame height.
	 */
	public Frame(final int width, final int height) {
		setSize(width, height);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setLocationRelativeTo(null);
		setVisible(true);

		Insets insets = getInsets();
		this.width = width - insets.left - insets.right;
		this.height = height - insets.top - insets.bottom;
		setTitle("Invaders");

		addKeyListener(InputManager.getInstance());
	}

	/**
	 * Sets current screen.
	 * 
	 * @param screen
	 *            Screen to show.
	 * @return Return code of the finished screen.
	 */
	public final Menu setScreen(final Screen screen) {
        /* Screen currently shown. */
        screen.initialize();
		return screen.run();
	}

	/**
	 * Getter for frame width.
	 *
	 * @return Frame width.
	 */
	@Override
	public final int getWidth() {
		return this.width;
	}

	/**
	 * Getter for frame height.
	 *
	 * @return Frame height.
	 */

	@Override
	public final int getHeight() {
		return this.height;
	}

	public void moveToMiddle() {setLocationRelativeTo(null);}
}
