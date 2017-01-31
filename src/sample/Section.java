package sample;

import sample.SImage;

import java.io.File;

/**
 * Created by ma.crane on 26/01/2017.
 */

public class Section extends SImage {

    private static int topLeftX;
    private static int topLeftY;
    private static int centreX;
    private static int centreY;

    public Section(File file, int x, int y, int width, int height, int analysisLevel) {
        super(file, analysisLevel);
        topLeftX = x;
        topLeftY = y;

        findCentreXY(width, height);

    }
    private static void findCentreXY(int width, int height){
        centreX = topLeftX + Math.round(width/2);
        centreY = topLeftY + Math.round(height/2);



    }
}
