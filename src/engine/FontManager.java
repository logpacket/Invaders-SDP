package engine;

import java.awt.*;
import java.io.IOException;

public class FontManager {
    private static Font fontSmall;
    private static Font fontRegular;
    private static Font fontBig;

    private static FontMetrics fontSmallMetrics;
    private static FontMetrics fontRegularMetrics;
    private static FontMetrics fontBigMetrics;


    static {
        FileManager fileManager = FileManager.getInstance();

        try {
            fontSmall = fileManager.loadFont(10f);
            fontRegular = fileManager.loadFont(14f);
            fontBig = fileManager.loadFont(24f);
        } catch (IOException _) {

        } catch (FontFormatException _) {

		}

    }

    public static void initializeMetrics(Graphics g){
        fontSmallMetrics = g.getFontMetrics(fontSmall);
		fontRegularMetrics = g.getFontMetrics(fontRegular);
		fontBigMetrics = g.getFontMetrics(fontBig);
    }

    public static Font getFontSmall(){ return fontSmall; }
    public static Font getFontRegular() { return fontRegular; }
    public static Font getFontBig() { return fontBig; }

    public static FontMetrics getFontSmallMetrics() { return fontSmallMetrics; }
    public static FontMetrics getFontRegularMetrics() { return fontRegularMetrics; }
    public static FontMetrics getFontBigMetrics() { return fontBigMetrics; }
}
