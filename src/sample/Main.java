package sample;

import javafx.application.Application;

import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


import java.util.HashMap;
import java.awt.Color;


import static java.lang.Math.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }{
        main2();
        
    }

    public static void main2(String args[]) throws IOException {
        File file = new File("C:\\Users\\Matthew\\Pictures\\Malice colour.png");
        BufferedImage image = ImageIO.read(file);
        int x = image.getWidth();
        int y = image.getHeight();
        int pixels = x * y;
        double redAverage = 0;
        double blueAverage = 0;
        double greenAverage = 0;
        //int[] redRange = new int[25];
        //int[] blueRange = new int[25];
        //int[] greenRange = new int[25];
        Color RGB = null;
        HashMap<Color, Double> existing = new HashMap<>();
        //existing.put(RGB, 0);

        for (int ix = 0; ix < x; ix++) {
            for (int iy = 0; iy < y; iy++) {
                int clr = image.getRGB(ix, iy);
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;
                double modifier = Modifier(red, green, blue, y, x, iy, ix);

                red = (int) round(red);
                blue = (int) round(blue);
                green = (int) round(green);

                redAverage += pow(red, 2.2);
                blueAverage += pow(blue, 2.2);
                greenAverage += pow(green, 2.2);
                RGB = new Color(red, green, blue);
                //RGB[0] = red;
                //RGB[1] = green;
                //RGB[2] = blue;
                ModeRGB(existing, RGB, modifier);
                //System.out.println("heullo");
                //for (Color name : existing.keySet()) {
                // String key = (Arrays.toString(name));
                // String value = existing.get(name).toString();
                //System.out.println(key + " " + value);

            }

        }

        redAverage = pow((redAverage / pixels), (1 / 2.2));
        blueAverage = pow((blueAverage / pixels), (1 / 2.2));
        greenAverage = pow((greenAverage / pixels), (1 / 2.2));

        redAverage = round(redAverage);
        blueAverage = round(blueAverage);
        greenAverage = round(greenAverage);

        System.out.println("Red Average: " + redAverage);
        System.out.println("Green Average: " + greenAverage);
        System.out.println("Blue Average: " + blueAverage);

        double highest = 0;
        Color item = new Color(0, 0, 0);
        for (Color StoredRGB : existing.keySet()) {
            if (existing.get(StoredRGB) > highest) {
                item = StoredRGB;
                highest = existing.get(StoredRGB);
            }
        }

        double[] SigmaXF = new double[3];
        double SigmaF = 0;

        for (Color StoredRGB : existing.keySet()) {
            if (existing.get(StoredRGB) > (highest * 0.75)) {
                SigmaXF[0] += existing.get(StoredRGB) * StoredRGB.getRed();
                SigmaXF[1] += existing.get(StoredRGB) * StoredRGB.getGreen();
                SigmaXF[2] += existing.get(StoredRGB) * StoredRGB.getBlue();
                SigmaF += existing.get(StoredRGB);
            }

        }
        System.out.println("Single Red Mode: " + item.getRed());
        System.out.println("Single Green Mode: " + item.getGreen());
        System.out.println("Single Blue Mode: " + item.getBlue());

        System.out.println("averaged Red Mode: " + (SigmaXF[0] / SigmaF));
        System.out.println("averaged Green Mode: " + (SigmaXF[1] / SigmaF));
        System.out.println("averaged Blue Mode: " + (SigmaXF[2] / SigmaF));
    }


    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }


    public static HashMap ModeRGB(HashMap<Color, Double> existing, Color RGB, double modifier) {
        for (Color StoredRGB : existing.keySet()) {
            if ((RGB.getRed() < StoredRGB.getRed() + 5) && (RGB.getRed() > StoredRGB.getRed() - 5) && (RGB.getGreen() < StoredRGB.getGreen() + 5) && (RGB.getGreen() > StoredRGB.getGreen() - 5) && (RGB.getBlue() < StoredRGB.getBlue() + 5) && (RGB.getBlue() > StoredRGB.getBlue() - 5)) {
                double count = existing.get(StoredRGB);
                existing.put(StoredRGB, count + 1 * modifier);
                return existing;
            }

        }
        existing.put(RGB, 1 * modifier);
        return existing;
    }

    public static double GreatestDifferenceModifier(int red, int green, int blue) {
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
        System.out.println("difference Modifer: " + (modifier));
        return (modifier);
    }

    public static double SignificanceWeighting(int red, int green, int blue) {
        double averageRGB = (red + green + blue) / 3;
        double brighnessModifier = (5 / sqrt(2 * PI)) * pow(E, (-(pow((((1 / (46.9574))) * averageRGB) - 2.71523, 2) / 2)) - 0.05);
        double differenceModifier = GreatestDifferenceModifier(red, green, blue);
        double overallModifier = brighnessModifier * differenceModifier;
        System.out.println("Significance Modifer: " + (overallModifier));
        return overallModifier;
    }

    public static double PositionWeighting(int totalHeight, int totalWidth, int currentHeight, int currentWidth) {
        System.out.println("total Height: " + totalHeight + " Current Height: " + currentHeight);
        System.out.println("total Width: " + totalWidth + " Current Width: " + currentWidth);
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

        System.out.println("positionWeightingWidth Modifer: " + (widthModifier));
        double overallModifier = (widthModifier + heightModifier) / 2;
        System.out.println("positionWeighting Modifer: " + (overallModifier));
        return (overallModifier);

    }

    public static double Modifier(int red, int green, int blue, int totalHeight, int totalWidth, int currentHeight, int currentWidth) {
        double PositionWeighting = PositionWeighting(totalHeight, totalWidth, currentHeight, currentWidth);
        double SignificanceWeighting = SignificanceWeighting(red, green, blue);
        System.out.println("Overall Modifer: " + (PositionWeighting * SignificanceWeighting));
        return (PositionWeighting * SignificanceWeighting);
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Application.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1000 , 700));
        primaryStage.show();
    }

}