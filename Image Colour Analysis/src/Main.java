import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.*;
import static java.lang.Math.E;
import static java.lang.Math.pow;

/**
 * Created by ma.crane on 07/03/2017.
 */
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.*;
import static java.lang.Math.pow;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
            try {
                analyse();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


        //the following variables all store data about an image.
        protected static int height;
        protected static int width;
        //Because colour is effectively 3 integers, red, green and blue, i always store colour in an int array of size 3.
        protected static int[] modeRGB = new int[3];
        protected static double[] meanRGB = new double[3];
        protected static double[] meanOfModesRGB = new double[3];

        //this is the file path of this image.
        //it's public because it is regularly used in other classes.
        public static File file;


        private static void analyse() throws IOException {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
            file = fileChooser.showOpenDialog(null);
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

            //this is a list of the class colourRange. colourRange stores a range of RGB values (eg: red: 24-29, green: 131-136, blue: 249-254)
            //so this array stores a set of ranges of RGB
            ArrayList<colourRange> RGBFrequencyArray = new ArrayList<colourRange>();

            //step is the incrementation value for the following loop.
            int step = calcStep();

            //this nested for loop loops through every pixel in the image (or every other, or every 3rd, depending on step)
            for (int ix = 0; ix < width; ix = ix + step) {
                for (int iy = 0; iy < height; iy++) {
                    //the following gets the bit
                    int clr = image.getRGB(ix, iy);
                    int red = (clr & 0x00ff0000) >> 16;
                    int green = (clr & 0x0000ff00) >> 8;
                    int blue = clr & 0x000000ff;
                    double modifier = modifier(red, green, blue, height, width, iy, ix);

                    red = (int) round(red);
                    green = (int) round(green);
                    blue = (int) round(blue);

                    redAverage += pow(red, 2.2);
                    greenAverage += pow(green, 2.2);
                    blueAverage += pow(blue, 2.2);


                    int[] RGB = {red, green, blue};

                    RGBFrequencyArray = modeRGB(RGBFrequencyArray, RGB, modifier);

                }
            }

            meanRGB[0] = round(pow((redAverage / (pixels/step)), (1 / 2.2)));
            meanRGB[1] = round(pow((greenAverage / (pixels/step)), (1 / 2.2)));
            meanRGB[2] = round(pow((blueAverage /(pixels/step)), (1 / 2.2)));

            calcModes(RGBFrequencyArray);

        }


        private static ArrayList<colourRange> modeRGB( ArrayList<colourRange> RGBFrequencyArray, int[] RGB, double modifier) {
            for (colourRange item : RGBFrequencyArray) {
                if ((RGB[0] < item.getRGB(0) + 5) && (RGB[0] > item.getRGB(0) - 5)
                        && (RGB[1] < item.getRGB(1) + 5) && (RGB[1] > item.getRGB(1) - 5)
                        && (RGB[2] < item.getRGB(2) + 5) && (RGB[2] > item.getRGB(2) - 5)
                        ) {
                    item.incrementFrequency(modifier);
                    return RGBFrequencyArray;
                }
            }
            colourRange newColourRange = new colourRange(RGB);
            RGBFrequencyArray.add(newColourRange);
            return RGBFrequencyArray;
        }

        public static int calcStep() {
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


        private static double greatestDifferenceModifier(int red, int green, int blue) {
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
            return (modifier);
        }

        private static double significanceWeighting(int red, int green, int blue) {
            double averageRGB = (red + green + blue) / 3;

            double brightnessModifier = (2.89313 / sqrt(2 * PI)) * (pow(E, -(1 / 3975f) * pow((averageRGB - (255 / 2f)), 2) / 2f)) + 0.35;

            double differenceModifier = greatestDifferenceModifier(red, green, blue);
            double overallModifier = brightnessModifier * differenceModifier;

            return overallModifier;
        }

        private static double positionWeighting(int totalHeight, int totalWidth, int currentHeight, int currentWidth) {
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

        private static double modifier(int red, int green, int blue, int totalHeight, int totalWidth, int currentHeight, int currentWidth) {
            double finalModifier;
            double PositionWeighting = positionWeighting(totalHeight, totalWidth, currentHeight, currentWidth);
            double SignificanceWeighting = significanceWeighting(red, green, blue);

            finalModifier = PositionWeighting * SignificanceWeighting;
            return (finalModifier);
        }

        private static void calcModes(ArrayList<colourRange> RGBFrequencyArray) {
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

            System.out.println("Single Red Mean: " + meanRGB[0]);
            System.out.println("Single Green Mean: " + meanRGB[1]);
            System.out.println("Single Blue Mean: " + meanRGB[2]);


            System.out.println("Single Red Mode: " + singleColour[0]);
            System.out.println("Single Green Mode: " + singleColour[1]);
            System.out.println("Single Blue Mode: " + singleColour[2]);

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

            meanOfModesRGB[0] = (SigmaXF[0] / SigmaF);
            meanOfModesRGB[1] = (SigmaXF[1] / SigmaF);
            meanOfModesRGB[2] = (SigmaXF[2] / SigmaF);

            System.out.println("averaged Red Mode: " + (SigmaXF[0] / SigmaF));
            System.out.println("averaged Green Mode: " + (SigmaXF[1] / SigmaF));
            System.out.println("averaged Blue Mode: " + (SigmaXF[2] / SigmaF));
        }
}
