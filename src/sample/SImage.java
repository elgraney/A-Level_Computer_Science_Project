package sample;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.*;
import static java.lang.Math.pow;

/**
 * Created by Matthew on 08/01/2017.
 */
public class SImage {

    protected int height;
    protected int width;
    protected int[] modeRGB = new int[3];
    protected double[] meanRGB = new double[3];
    protected double[] meanOfModesRGB = new double[3];
    protected double ratioMultiple;

    private int analysisLevel;

    File file;

    public SImage(File file, int analysisLevel) {
        this.analysisLevel = analysisLevel;
        this.file = file;
        try {
            analyse();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void analyse() throws IOException {

        BufferedImage image = ImageIO.read(file);
        width = image.getWidth();
        height = image.getHeight();
        int pixels = width * height;
        double redAverage = 0;
        double blueAverage = 0;
        double greenAverage = 0;

        double modifierCount = 0;

        //HashMap<Color, Double> RGBFrequencyMap = new HashMap<>(255 * 255);
        ArrayList<colourRange> RGBFrequencyArray = new ArrayList<colourRange>();

        //RGBFrequencyMap.
        int step = calcStep();
        for (int ix = 0; ix < width; ix = ix + step) {
            for (int iy = 0; iy < height; iy++) {
                int clr = image.getRGB(ix, iy);
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;
                double modifier = modifier(red, green, blue, height, width, iy, ix);
                modifierCount += modifier;

                red = (int) round(red);
                green = (int) round(green);
                blue = (int) round(blue);

                redAverage += pow(red, 2.2);
                greenAverage += pow(green, 2.2);
                blueAverage += pow(blue, 2.2);


                int[] RGB = {red, green, blue};
                if (analysisLevel > 1) {
                    RGBFrequencyArray = modeRGB(RGBFrequencyArray, RGB, modifier);
                    //System.out.println("RGBFrequencyArray size "+RGBFrequencyArray.size());
                }
            }
        }

        meanRGB[0] = round(pow((redAverage / (pixels/step)), (1 / 2.2)));
        meanRGB[1] = round(pow((greenAverage / (pixels/step)), (1 / 2.2)));
        meanRGB[2] = round(pow((blueAverage /(pixels/step)), (1 / 2.2)));


        //System.out.println("Red Average: " + meanRGB[0]);
        //System.out.println("Green Average: " + meanRGB[1]);
        //System.out.println("Blue Average: " + meanRGB[2]);

        if (analysisLevel > 1) {
            calcModes(RGBFrequencyArray);
        }
    }


    private ArrayList<colourRange> modeRGB( ArrayList<colourRange> RGBFrequencyArray, int[] RGB, double modifier) {
        for (colourRange item : RGBFrequencyArray) {
            if ((RGB[0] < item.getRGB(0) + 5) && (RGB[0] > item.getRGB(0) - 5)
                    && (RGB[1] < item.getRGB(1) + 5) && (RGB[1] > item.getRGB(1) - 5)
                    && (RGB[2] < item.getRGB(2) + 5) && (RGB[2] > item.getRGB(2) - 5)
                    ) {
                    //System.out.println("modeRGB past if");
                    item.incrementFrequency(modifier);
                    return RGBFrequencyArray;
                }
        }
        colourRange newColourRange = new colourRange(RGB);
        RGBFrequencyArray.add(newColourRange);
        return RGBFrequencyArray;
    }

    public int calcStep() {
        int resolution = width * height;
        int step;
        //System.out.println("Resolution"+resolution);
        if (resolution < 250000) {
            step = 2;
        } else if (resolution >= 250000 && resolution < 500000) {
            step = 3;
        } else if (resolution >= 500000 && resolution < 750000) {
            step = 5;
        } else if (resolution >= 750000 && resolution < 1000000) {
            step = 7;
        } else if (resolution >= 1000000 && resolution < 1500000) {
            step = 9;
        } else if (resolution >= 1500000 && resolution < 2000000) {
            step = 11;
        } else if (resolution >= 2000000 && resolution < 2500000) {
            step = 14;
        } else {
            step = 20;
        }
        //System.out.println("Step: "+step);
        return step;
    }


    private double greatestDifferenceModifier(int red, int green, int blue) {
        int[] RGB = {red, green, blue};
        double maxDifference = 0;
        double difference = 0;
        for (int iterations = 0; iterations < 2; iterations++) {
            difference = sqrt(pow((RGB[iterations] - RGB[0]), 2));
            if (difference > maxDifference) {
                maxDifference = difference;
            }
            difference = sqrt(pow((RGB[iterations] - RGB[1]), 2));
            if (difference > maxDifference) {
                maxDifference = difference;
            }
            difference = sqrt(pow((RGB[iterations] - RGB[1]), 2));
            if (difference > maxDifference) {
                maxDifference = difference;
            }
        }
        double modifier = 0.5 + (maxDifference) / 255;
        //System.out.println("difference Modifer: " + (modifier));
        return (modifier);
    }

    private double significanceWeighting(int red, int green, int blue) {
        double averageRGB = (red + green + blue) / 3;

        double brightnessModifier = (2.89313 / sqrt(2 * PI)) * (pow(E, -(1 / 3975f) * pow((averageRGB - (255 / 2f)), 2) / 2f)) + 0.35;

        double differenceModifier = greatestDifferenceModifier(red, green, blue);
        double overallModifier = brightnessModifier * differenceModifier;

        return overallModifier;
    }

    private double positionWeighting(int totalHeight, int totalWidth, int currentHeight, int currentWidth) {
        double widthModifier;
        double heightModifier;
        double currentWidth2 = (double) currentWidth;
        double currentHeight2 = (double) currentHeight;
        double totalWidth2 = (double) totalWidth;
        double totalHeight2 = (double) totalHeight;

        widthModifier = (2.89313 / sqrt(2 * PI)) * pow(E, -15 * pow(currentWidth2 / totalWidth2 - 0.5, 2) / 2f) + 0.35;
        heightModifier = (2.89313 / sqrt(2 * PI)) * pow(E, -15 * pow(currentHeight2 / totalHeight2 - 0.5, 2) / 2f) + 0.35;

        double overallModifier = (widthModifier + heightModifier) / 2;

        return (overallModifier);

    }

    private double modifier(int red, int green, int blue, int totalHeight, int totalWidth, int currentHeight, int currentWidth) {
        double finalModifier;
        double PositionWeighting = positionWeighting(totalHeight, totalWidth, currentHeight, currentWidth);
        double SignificanceWeighting = significanceWeighting(red, green, blue);
        //System.out.println("Overall Modifer: " + (PositionWeighting * SignificanceWeighting));

        finalModifier = PositionWeighting * SignificanceWeighting;
        return (finalModifier);
    }

    private void calcModes(ArrayList<colourRange> RGBFrequencyArray) {
        double highest = 0;
        int[] singleColour = new int[3];

        for (colourRange colourRange : RGBFrequencyArray) {
            if (colourRange.getFrequency() > highest) {
                singleColour[0] = colourRange.getRGB(0);
                singleColour[1] = colourRange.getRGB(1);
                singleColour[2] = colourRange.getRGB(2);

                highest = colourRange.getFrequency();
            }
        }
        modeRGB = singleColour;
        //finished pure mode.


        //System.out.println("Single Red Mode: " + singleColour[0]);
        //System.out.println("Single Green Mode: " + singleColour[1]);
        //System.out.println("Single Blue Mode: " + singleColour[2]);

        double[] SigmaXF = new double[3];
        double SigmaF = 0;


        for (colourRange colourRange : RGBFrequencyArray) {
            if (colourRange.getFrequency() > highest * 0.75) {
                SigmaXF[0] += colourRange.getFrequency() * colourRange.getRGB(0);
                SigmaXF[1] += colourRange.getFrequency() * colourRange.getRGB(1);
                SigmaXF[2] += colourRange.getFrequency() * colourRange.getRGB(2);

                SigmaF += colourRange.getFrequency();

            }
        }
        //i'm not sure this is how weighted mean works
        meanOfModesRGB[0] = (SigmaXF[0] / SigmaF);
        meanOfModesRGB[1] = (SigmaXF[1] / SigmaF);
        meanOfModesRGB[2] = (SigmaXF[2] / SigmaF);

        //System.out.println("averaged Red Mode: " + (SigmaXF[0] / SigmaF));
        //System.out.println("averaged Green Mode: " + (SigmaXF[1] / SigmaF));
        //System.out.println("averaged Blue Mode: " + (SigmaXF[2] / SigmaF));
    }


    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }


    public void crop(int newWidth, int newHeight) throws IOException {
        width = newWidth;
        height = newHeight;


        BufferedImage croppedImage = new BufferedImage(newWidth, newHeight,ImageIO.read(file).getType());

        //This section can return false colours
        //also really slow
        //look into function to do it for you.
        for (int x=((width - newWidth)/2); x< newWidth; x++) {
            for (int y = ((height- newHeight)/2); y < newHeight; y++) {
                croppedImage.setRGB(x, y, ImageIO.read(file).getRGB(x, y));
            }
        }
        System.out.println("croppedImage width " + croppedImage.getWidth());
        System.out.println("croppedImage height " + croppedImage.getHeight());

        File outputfile = new File("croppedImage.jpg");
        try {
            ImageIO.write(croppedImage, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = outputfile;

    }

    public void setRatioMultiple(double multiple){
        ratioMultiple = multiple;
    }
    public double getRatio(){
        return ratioMultiple;
    }
    public Double getMeanRGB(int index){
        return meanRGB[index];
    }
    public Double getMeanOfModesRGB(int index){
        return meanOfModesRGB[index];
    }
}