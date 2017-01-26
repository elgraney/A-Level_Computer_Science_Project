import sample.SImage;

import java.io.File;

/**
 * Created by ma.crane on 26/01/2017.
 */

public class Section extends SImage {

    private int topLeftX;
    private int topLeftY;
    private int centreX;
    private int centreY;

    public Section(File file, int x, int y, int width, int height) {
        super(file);
        topLeftX = x;
        topLeftY = y;


    }
}
