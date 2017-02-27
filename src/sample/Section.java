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
    private boolean inCompoundSection = false;
    private boolean compoundSectionMarker = false;
    private int sectionArrayX;
    private int sectionArrayY;

    public Section(File file, int x, int y, int width, int height) {
        super(file);
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

    public void setCompound(int width, int height, int X, int Y, double ratioMultiple) {
        setRatioMultiple(ratioMultiple);
        compoundSectionMarker = true;
        this.width = width;
        this.height = height;
        sectionArrayX = X;
        sectionArrayY = Y;
    }

    public void setInCompound() {
        inCompoundSection = true;
    }

    public boolean isCompoundSectionMarker(){
        return  compoundSectionMarker;
    }

    public void breakdown(int width, int height){
        this.width = width;
        this.height = height;
        compoundSectionMarker = false;
        inCompoundSection = false;
        setRatioMultiple(1);
    }
    public int getX(){
        return sectionArrayX;
    }
    public int getY(){
        return sectionArrayY;
    }
    public int getCentreX(){return  centreX;}
    public int getCentreY(){return  centreY;}
    public void updateCentre(){
        centreX = topLeftX+ width/2;
        centreY = topLeftY + height/2;
    }
}
