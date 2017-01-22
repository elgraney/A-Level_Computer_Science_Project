package sample;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.*;
import static java.lang.Math.pow;

/**
 * Created by Matthew on 08/01/2017.
 */
public class SImage {

    int height;
    int width;
    int[] modeRGB = new int[3];
    long[] meanRGB = new long[3];
    double[] meanOfModesRGB = new double[3];
    String path;

    File file;

    public SImage(File file) {
        this.file = file;
        try {
            analyse();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void analyse() throws IOException {

        BufferedImage image = ImageIO.read(file);
        int x = image.getWidth();
        int y = image.getHeight();
        int pixels = x * y;
        double redAverage = 0;
        double blueAverage = 0;
        double greenAverage = 0;
        Color RGB = null;

        HashMap<Color, Double> RGBFrequencyMap = new HashMap<>();

        for (int ix = 0; ix < x; ix++) {
            for (int iy = 0; iy < y; iy++) {
                int clr = image.getRGB(ix, iy);
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;
                double modifier = modifier(red, green, blue, y, x, iy, ix);

                red = (int) round(red);
                blue = (int) round(blue);
                green = (int) round(green);

                redAverage += pow(red, 2.2);
                blueAverage += pow(blue, 2.2);
                greenAverage += pow(green, 2.2);
                RGB = new Color(red, green, blue);

                modeRGB(RGBFrequencyMap, RGB, modifier);
            }
        }

        meanRGB[0] = round(pow((redAverage / pixels), (1 / 2.2)));
        meanRGB[1] = round(pow((blueAverage / pixels), (1 / 2.2)));
        meanRGB[2] = round(pow((greenAverage / pixels), (1 / 2.2)));

        System.out.println("Red Average: " + redAverage);
        System.out.println("Green Average: " + greenAverage);
        System.out.println("Blue Average: " + blueAverage);

        calcModes(RGBFrequencyMap);
    }


    private HashMap modeRGB(HashMap<Color, Double> RGBFrequencyMap, Color RGB, double modifier) {
        for (Color StoredRGB : RGBFrequencyMap.keySet()) {
            if ((RGB.getRed() < StoredRGB.getRed() + 5) && (RGB.getRed() > StoredRGB.getRed() - 5) && (RGB.getGreen() < StoredRGB.getGreen() + 5) && (RGB.getGreen() > StoredRGB.getGreen() - 5) && (RGB.getBlue() < StoredRGB.getBlue() + 5) && (RGB.getBlue() > StoredRGB.getBlue() - 5)) {
                double count = RGBFrequencyMap.get(StoredRGB);
                RGBFrequencyMap.put(StoredRGB, count + 1 * modifier);
                return RGBFrequencyMap;
            }

        }
        RGBFrequencyMap.put(RGB, 1 * modifier);
        return RGBFrequencyMap;
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
        double modifier = 0.1 + (maxDifference * 2) / 255;
        //System.out.println("difference Modifer: " + (modifier));
        return (modifier);
    }

    private double significanceWeighting(int red, int green, int blue) {
        double averageRGB = (red + green + blue) / 3;
        double brighnessModifier = (5 / sqrt(2 * PI)) * pow(E, (-(pow((((1 / (46.9574))) * averageRGB) - 2.71523, 2) / 2)) - 0.05);
        double differenceModifier = greatestDifferenceModifier(red, green, blue);
        double overallModifier = brighnessModifier * differenceModifier;
        //System.out.println("Significance Modifer: " + (overallModifier));
        return overallModifier;
    }

    private double positionWeighting(int totalHeight, int totalWidth, int currentHeight, int currentWidth) {
        //System.out.println("total Height: " + totalHeight + " Current Height: " + currentHeight);
        //System.out.println("total Width: " + totalWidth + " Current Width: " + currentWidth);
        double widthModifier;
        double heightModifier;
        double currentWidth2 = (double) currentWidth;
        double currentHeight2 = (double) currentHeight;
        double totalWidth2 = (double) totalWidth;
        double totalHeight2 = (double) totalHeight;

        //widthModifier = ((5/ sqrt(2*PI))* pow(E, (-(pow(5*((currentWidth2/totalWidth2)+0.0034)-2.5, 2))/2)))-0.08867;
        //heightModifier =((5/ sqrt(2*PI))* pow(E, (-(pow(5*((currentHeight2/totalHeight2)+0.0034)-2.5, 2))/2)))-0.08867;
        //WHYYYYYYYYYYY
        widthModifier = ((5 / sqrt(2 * PI)) * pow(E, (-(((pow((5 * ((currentWidth2 / totalWidth2) + 0.0034) - 2.5), 2)) / 2))))) - 0.08867;
        heightModifier = ((5 / sqrt(2 * PI)) * pow(E, (-(((pow((5 * ((currentHeight2 / totalHeight2) + 0.0034) - 2.5), 2)) / 2))))) - 0.08867;

        //System.out.println("positionWeightingWidth Modifer: " + (widthModifier));
        double overallModifier = (widthModifier + heightModifier) / 2;
        //System.out.println("positionWeighting Modifer: " + (overallModifier));
        return (overallModifier);

    }

    private double modifier(int red, int green, int blue, int totalHeight, int totalWidth, int currentHeight, int currentWidth) {
        double PositionWeighting = positionWeighting(totalHeight, totalWidth, currentHeight, currentWidth);
        double SignificanceWeighting = significanceWeighting(red, green, blue);
        //System.out.println("Overall Modifer: " + (PositionWeighting * SignificanceWeighting));
        return (PositionWeighting * SignificanceWeighting);
    }

    private void calcModes(HashMap<Color, Double> RGBFrequencyMap) {
        double highest = 0;
        Color item = new Color(0, 0, 0);
        for (Color StoredRGB : RGBFrequencyMap.keySet()) {
            if (RGBFrequencyMap.get(StoredRGB) > highest) {
                item = StoredRGB;
                highest = RGBFrequencyMap.get(StoredRGB);
            }
        }

        modeRGB[0] = item.getRed();
        modeRGB[1] = item.getGreen();
        modeRGB[2] = item.getBlue();
        System.out.println("Single Red Mode: " + item.getRed());
        System.out.println("Single Green Mode: " + item.getGreen());
        System.out.println("Single Blue Mode: " + item.getBlue());

        double[] SigmaXF = new double[3];
        double SigmaF = 0;


        for (Color StoredRGB : RGBFrequencyMap.keySet()) {
            if (RGBFrequencyMap.get(StoredRGB) > (highest * 0.75)) {
                SigmaXF[0] += RGBFrequencyMap.get(StoredRGB) * StoredRGB.getRed();
                SigmaXF[1] += RGBFrequencyMap.get(StoredRGB) * StoredRGB.getGreen();
                SigmaXF[2] += RGBFrequencyMap.get(StoredRGB) * StoredRGB.getBlue();
                SigmaF += RGBFrequencyMap.get(StoredRGB);

            }
        }
            meanOfModesRGB[0] = (SigmaXF[0] / SigmaF);
            meanOfModesRGB[1] = (SigmaXF[1] / SigmaF);
            meanOfModesRGB[2] = (SigmaXF[2] / SigmaF);

            System.out.println("averaged Red Mode: " + (SigmaXF[0] / SigmaF));
            System.out.println("averaged Green Mode: " + (SigmaXF[1] / SigmaF));
            System.out.println("averaged Blue Mode: " + (SigmaXF[2] / SigmaF));
        }

    }


