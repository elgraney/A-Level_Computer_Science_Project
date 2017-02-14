package sample;

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
    private SImage linkedImage;

    //compound section only
    private int compoundWidth;
    private int compoundHeight;
    private boolean inCompoundSection = false;
    private boolean compoundSectionMarker = false;

    public Section(File file, int x, int y, int width, int height, int analysisLevel) {
        super(file, analysisLevel);
        topLeftX = x;
        topLeftY = y;

        findCentreXY(width, height);

    }

    private void findCentreXY(int width, int height) {
        centreX = topLeftX + Math.round(width / 2);
        centreY = topLeftY + Math.round(height / 2);
    }

    public void setLinkedImage(SImage image) {
        linkedImage = image;
    }

    public SImage getLinkedImage() {
        return linkedImage;
    }

    public int getTopLeftX() {
        return topLeftX;
    }

    public int getTopLeftY() {
        return topLeftY;
    }

    public boolean isInCompoundSection() {
        return inCompoundSection;
    }

    public void setCompound(int width, int height) {
        compoundSectionMarker = true;
        compoundWidth = width;
        compoundHeight = height;
    }

    public void setInCompound() {
        inCompoundSection = true;
    }

    public boolean isCompoundSectionMarker(){
        return  compoundSectionMarker;
    }
}
