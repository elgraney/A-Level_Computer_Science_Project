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

//The SImage class is used analyse images passed into the program, then store data about them.
public class SImage {
    //the following variables all store data about an image.
    protected int height;
    protected int width;
    //Because colour is effectively 3 integers, red, green and blue, i always store colour in an int array of size 3.
    protected int[] modeRGB = new int[3];
    protected double[] meanRGB = new double[3];
    protected double[] meanOfModesRGB = new double[3];

    //ratio multiple is the value by which the most common ratio must be multiplied by equal the ratio of this image.
    protected double ratioMultiple;

    //selected is a variable which indicates if this image is to be included in the next generation of not.
    //it's public because it is regularly used in other classes.
    public boolean selected = true;

    //this is the file path of this image.
    //it's public because it is regularly used in other classes.
    public File file;

    //SImage constructor
    public SImage(File file) {
        this.file = file;
        this.ratioMultiple = 1;
        try {
            //as soon as an SImage is created, the image's colour is analysed with this method.
            analyse();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void analyse() throws IOException {
        //a buffered image of the image must be created
        BufferedImage image = ImageIO.read(file);
        //the resolution is found and stored
        width = image.getWidth();
        height = image.getHeight();
        int pixels = width * height;

        //colour variables are initialised
        double redAverage = 0;
        double blueAverage = 0;
        double greenAverage = 0;

        //this is a list of the class ColourRange. ColourRange stores a range of RGB values (eg: red: 24-29, green: 131-136, blue: 249-254)
        //so this array stores a set of ranges of RGB
        ArrayList<ColourRange> RGBFrequencyArray = new ArrayList<ColourRange>();

        //step is the incrementation value for the following loop.
        int step = calcStep();

        //this nested for loop loops through every pixel in the image (or every other, or every 3rd, depending on step)
        for (int ix = 0; ix < width; ix = ix + step) {
            for (int iy = 0; iy < height; iy++) {
                //the following gets the bits that refer to each colour from the RGB string
                int clr = image.getRGB(ix, iy);
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;
                double modifier = modifier(red, green, blue, height, width, iy, ix);

                //the colour value is raised to the power of 2.2 as part of the gamma correction algorithm. The inverse operation is performed on the mean.
                redAverage += pow(red, 2.2);
                greenAverage += pow(green, 2.2);
                blueAverage += pow(blue, 2.2);

                int[] RGB = {red, green, blue};

                //calls the methods that contribute to the Mode calculation
                RGBFrequencyArray = modeRGB(RGBFrequencyArray, RGB, modifier);
            }
        }
        //storing the mean RGBs
        meanRGB[0] = round(pow((redAverage / (pixels/step)), (1 / 2.2)));
        meanRGB[1] = round(pow((greenAverage / (pixels/step)), (1 / 2.2)));
        meanRGB[2] = round(pow((blueAverage /(pixels/step)), (1 / 2.2)));

        //call the method for finding the mode RGB
        calcModes(RGBFrequencyArray);
    }

    //Uses the ColourRange class to record the frequency of RGB values within a certain range
    private ArrayList<ColourRange> modeRGB( ArrayList<ColourRange> RGBFrequencyArray, int[] RGB, double modifier) {
        for (ColourRange item : RGBFrequencyArray) {
            //an RGB value is considered similar if red, green and blue are all within +-8 of an RGB value already seen
            if ((RGB[0] < item.getRGB(0) + 8) && (RGB[0] > item.getRGB(0) - 8)
                    && (RGB[1] < item.getRGB(1) + 8) && (RGB[1] > item.getRGB(1) - 8)
                    && (RGB[2] < item.getRGB(2) + 8) && (RGB[2] > item.getRGB(2) - 8)
                    ) {
                    //the frequency of the existing RGB value is incremented based on modifiers
                    item.incrementFrequency(modifier);
                    return RGBFrequencyArray;
                }
        }
        //this runs if there is no existing similar RGB value. A new ColourRange is created to store the RGB and the initial frequency is set to 1
        ColourRange newColourRange = new ColourRange(RGB);
        RGBFrequencyArray.add(newColourRange);
        return RGBFrequencyArray;
    }

    //Step refers to the number of pixels skipped over when analysing an image. This is done to reduce analysis time.
    //more pixels can be skipped for larger images
    public int calcStep() {
        int resolution = width * height;
        int step;
        if (resolution < 250000) {
            step = 3;
        } else if (resolution >= 250000 && resolution < 500000) {
            step = 6;
        } else if (resolution >= 500000 && resolution < 750000) {
            step = 10;
        } else if (resolution >= 750000 && resolution < 1000000) {
            step = 14;
        } else if (resolution >= 1000000 && resolution < 1500000) {
            step = 18;
        } else if (resolution >= 1500000 && resolution < 2000000) {
            step = 22;
        } else if (resolution >= 2000000 && resolution < 2500000) {
            step = 28;
        } else {
            step = 40;
        }
        return step;
    }

    //the GD modifier finds the largest difference between two colour values. The larger the difference, the larger the returned modifier
    private double greatestDifferenceModifier(int red, int green, int blue) {
        int[] RGB = {red, green, blue};
        double maxDifference = 0;
        double difference = 0;
        //there are only 2 iteration despite there being 3 colours because all possible differences have been found after 2 iterations if the modulus of the difference is taken
        //this is because half of the values would be negative if all 9 differences were calculated
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
        //the modifier is a value between 0.5 and 1.5
        double modifier = 0.5 + (maxDifference) / 255;
        return (modifier);
    }

    //significance weighting is a combination of difference and brightness modifiers.
    private double significanceWeighting(int red, int green, int blue) {
        double averageRGB = (red + green + blue) / 3;

        //brightness modifier is a value between roughly 0.5 and 1.5
        //average RGB values furthest from black (0) and white (255) are given the largest weighting
        //the following function describes a transformed normal distribution curve
        double brightnessModifier = (2.89313 / sqrt(2 * PI)) * (pow(E, -(1 / 3975f) * pow((averageRGB - (255 / 2f)), 2) / 2f)) + 0.35;

        double differenceModifier = greatestDifferenceModifier(red, green, blue);
        double overallModifier = brightnessModifier * differenceModifier;

        return overallModifier;
    }

    //position weighting returns a modifier based on the pixels location in the image
    private double positionWeighting(int totalHeight, int totalWidth, int currentHeight, int currentWidth) {
        double widthModifier;
        double heightModifier;
        double currentWidth2 = (double) currentWidth;
        double currentHeight2 = (double) currentHeight;
        double totalWidth2 = (double) totalWidth;
        double totalHeight2 = (double) totalHeight;

        //the two following functions describe transformed normal distribution curves that give weightings between 0.5 and 1.5
        //central pixels are given weights close to 1.5 and outlying pixels are given weights close to 0.5
        widthModifier = (2.89313 / sqrt(2 * PI)) * pow(E, -15 * pow(currentWidth2 / totalWidth2 - 0.5, 2) / 2f) + 0.35;
        heightModifier = (2.89313 / sqrt(2 * PI)) * pow(E, -15 * pow(currentHeight2 / totalHeight2 - 0.5, 2) / 2f) + 0.35;

        //the height and width modifier is averaged to give an overall position modifier
        double overallModifier = (widthModifier + heightModifier) / 2;

        return (overallModifier);

    }
    //this collects all the different modifiers, combines them, then returns the final modifier
    private double modifier(int red, int green, int blue, int totalHeight, int totalWidth, int currentHeight, int currentWidth) {
        double finalModifier;

        double PositionWeighting = positionWeighting(totalHeight, totalWidth, currentHeight, currentWidth);
        double SignificanceWeighting = significanceWeighting(red, green, blue);

        finalModifier = PositionWeighting * SignificanceWeighting;
        return (finalModifier);
    }

    //This selects the most frequent instance of ColourRange and calls it the modal RGB
    private void calcModes(ArrayList<ColourRange> RGBFrequencyArray) {
        double highest = 0;
        int[] singleColour = new int[3];

        for (ColourRange ColourRange : RGBFrequencyArray) {
            if (ColourRange.getFrequency() > highest) {
                singleColour[0] = ColourRange.getRGB(0);
                singleColour[1] = ColourRange.getRGB(1);
                singleColour[2] = ColourRange.getRGB(2);

                highest = ColourRange.getFrequency();
            }
        }
        modeRGB = singleColour;


        //the most frequent ColourRanges are passed through a weighted average to obtain a mean of modes
        double[] SigmaXF = new double[3];
        double SigmaF = 0;
        for (ColourRange ColourRange : RGBFrequencyArray) {
            //colours are only considered if their frequency is greater than 60% the frequency of the modal colour
            if (ColourRange.getFrequency() > highest * 0.6) {
                SigmaXF[0] += ColourRange.getFrequency() * ColourRange.getRGB(0);
                SigmaXF[1] += ColourRange.getFrequency() * ColourRange.getRGB(1);
                SigmaXF[2] += ColourRange.getFrequency() * ColourRange.getRGB(2);

                SigmaF += ColourRange.getFrequency();
            }
        }
        //the mean of modes is calculated and stored
        meanOfModesRGB[0] = (SigmaXF[0] / SigmaF);
        meanOfModesRGB[1] = (SigmaXF[1] / SigmaF);
        meanOfModesRGB[2] = (SigmaXF[2] / SigmaF);
    }


    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    //This reduces an images size without stretching it by creating an entirely new image and replacing the old image
    public void crop(int newWidth, int newHeight) throws IOException {
        width = newWidth;
        height = newHeight;

        //a new image of the correct type and dimensions is created
        BufferedImage croppedImage = null;
        try{
            croppedImage = new BufferedImage(newWidth, newHeight,ImageIO.read(file).getType());
            //the existing image is loaded
            BufferedImage bufferedImage = ImageIO.read(file);
            //relevant pixels are coppied from the existing image into the new image
            for (int x=((width - newWidth)/2); x< newWidth; x++) {
                for (int y = ((height- newHeight)/2); y < newHeight; y++) {
                    croppedImage.setRGB(x, y, bufferedImage.getRGB(x, y));
                }
            }
            //the new image is written so that it overwrites the old image
            try {
                ImageIO.write(croppedImage, "jpg", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //in rare instances the file type of an image is not recognised. This catch removes this problem image from the generation if it arrises
        catch (IllegalArgumentException e) {
            System.out.println("Bad file caught");
            selected=false;
        }
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