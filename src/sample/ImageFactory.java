package sample;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Created by Matthew on 08/01/2017.
 */
public class ImageFactory {
    private static int sectionWidth;
    private static int sectionHeight;
    private static SImage workingTemplate;
    private static BufferedImage templateFile;


    public static void generate(SImage template,List<SImage> imagePool, BufferedImage templateImage){
        System.out.println("generate");
        workingTemplate = template;
        templateFile = templateImage;
        createSections(imagePool);
    }

    public static void createSections(List<SImage> imagePool ) {
        System.out.println("CreateScetion");
        double mostCommonRatio;
        mostCommonRatio = getMostCommonRatio(imagePool);
        defineSections(mostCommonRatio);

        int width = workingTemplate.getWidth();
        int height = workingTemplate.getHeight();

        Section[][] sectionList = new Section[width/sectionWidth][height/sectionHeight];
        for (int ix = 0; ix < width; ix += sectionWidth) {
            for (int iy = 0; iy < height; iy +=sectionHeight) {
                System.out.println("hey!");
                Image sectionImage = createSectionImage(ix, iy);
                //section image is buffered image, but section does not take that type.

                File sectionfile = new File("section.jpg");
                ImageIO.write(sectionImage, "jpg", sectionfile);

                Section currentSection = new Section(sectionImage, ix, iy, sectionWidth, sectionHeight);
                sectionList[ix/sectionWidth][iy/sectionHeight] = currentSection;
            }}

    }
    private static Image createSectionImage(int x ,int y){
        return templateFile.getSubimage(x, y, sectionWidth, sectionHeight);



    }
    private static void defineSections(double mostCommonRatio) {
        for (int potentialHeight = 1; potentialHeight < 1000; potentialHeight++) {
            double potentialWidth = potentialHeight * mostCommonRatio;
            //potentialWidth = (Math.round(potentialWidth*100)/100);
            //System.out.println("Before Width: " + potentialWidth);
            //System.out.println("mod " + potentialWidth % 1);
            double remainder = potentialWidth % 1;
            System.out.println(remainder);
            if ((remainder == 0.0) && (potentialWidth >= 30)) {
                System.out.println("After Width: " + potentialWidth);
                sectionWidth = (int) potentialWidth;
                sectionHeight = potentialHeight;
                return;
            }

        }
        System.out.println("NO RESOLUTION FOUND");
    }






    private static double getMostCommonRatio(List<SImage> imagePool) {
        HashMap<Double, Integer> ratioFrequencyMap = new HashMap();
        double selectedImageRatio = 0;
        double targetImageRatio = 0;
        System.out.println("Yo");


        for (SImage selectedImage : imagePool) {
            double width = selectedImage.getWidth();
            double height = selectedImage.getHeight();

            selectedImageRatio = (width/height);


            System.out.println("selected: "+ selectedImageRatio);

            try {
                int count = ratioFrequencyMap.get(selectedImageRatio);
                System.out.println(count);
                ratioFrequencyMap.put(selectedImageRatio, count + 1);
            }
            catch (Exception e) {
                System.out.println("Error detected");
                ratioFrequencyMap.put(selectedImageRatio, 1);
            }
        }


        double highestRatio = 0;
        int highest = 0;
        for (double ratio : ratioFrequencyMap.keySet()) {
            if (ratioFrequencyMap.get(ratio) > highest) {
                highestRatio = ratio;
                highest = ratioFrequencyMap.get(ratio);
            }
        }
        //testing
        for (double ratio : ratioFrequencyMap.keySet()) {
            System.out.println(ratio);
            System.out.println(highest);
        }
        System.out.println(highestRatio);

        return highestRatio;
    }
}