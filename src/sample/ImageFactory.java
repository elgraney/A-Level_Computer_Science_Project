package sample;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Created by Matthew on 08/01/2017.
 */
public class ImageFactory {
    private static int sectionWidth;
    private static int sectionHeight;
    private static SImage unalteredTemplate;
    private static BufferedImage templateFile;
    private static int analysisLevel;

    private static BufferedImage processedTemplateFile;

    public static void generate(SImage SImageTemplate,List<SImage> imagePool, BufferedImage templateImage, int analysisLvl){


        System.out.println("generate");
        analysisLevel = analysisLvl;
        unalteredTemplate = SImageTemplate;
        templateFile = templateImage;
        createSections(imagePool);
    }




    public static void createSections(List<SImage> imagePool ) {
        System.out.println("CreateScetion");
        double mostCommonRatio;
        mostCommonRatio = getMostCommonRatio(imagePool);
        defineSections(mostCommonRatio);
        resizeTemplate(unalteredTemplate);

        System.out.println("new height "+processedTemplateFile.getWidth());
        System.out.println("new Width "+processedTemplateFile.getHeight());
        File outputfile = new File("2ndimage.jpg");
        try {
            ImageIO.write(processedTemplateFile, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int width = processedTemplateFile.getWidth();
        int height = processedTemplateFile.getHeight();

        Section[][] sectionList = new Section[width/sectionWidth][height/sectionHeight];
        for (int ix = 0; ix < width; ix += sectionWidth) {
            for (int iy = 0; iy < height; iy +=sectionHeight) {
                //System.out.println("hey!");
                BufferedImage sectionImage = createSectionImage(ix, iy);
                //section image is buffered image, but section does not take that type.
               File sectionfile = new File("section.jpg");
                //try {
                  //  ImageIO.write(sectionImage, "jpg", sectionfile);
               // } catch (IOException e) {
               //     e.printStackTrace();
               // }
                Section currentSection = new Section(sectionfile, ix, iy, sectionWidth, sectionHeight, analysisLevel);
                sectionList[ix/sectionWidth][iy/sectionHeight] = currentSection;
            }}
        System.out.println("2D array length: "+  sectionList.length * sectionList[0].length);

    }
    private static BufferedImage createSectionImage(int x ,int y){
        return processedTemplateFile.getSubimage(x, y, sectionWidth, sectionHeight);
    }



    private static void defineSections(double mostCommonRatio) {
        for (int potentialHeight = 1; potentialHeight < 1000; potentialHeight++) {
            double potentialWidth = potentialHeight * mostCommonRatio;
            //potentialWidth = (Math.round(potentialWidth*100)/100);
            //System.out.println("Before Width: " + potentialWidth);
            //System.out.println("mod " + potentialWidth % 1);
            double remainder = potentialWidth % 1;
            System.out.println(remainder);

            if ((remainder == 0.0) && (potentialWidth >= 30)&& (potentialHeight >= 30)) {
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
            System.out.println("ratio " +ratio);
            System.out.println("highest "+ highest);
        }
        System.out.println("Highest Ratio "+highestRatio);
        return highestRatio;
    }



    private static void resizeTemplate(SImage template){
        int templateWidth=0;
        int templateHeight=0;
        int enlargementFactor = 0;

        int enlargedTemplateWidth =0;
        int enlargedTemplateHeight=0;

        for (int multiplier = 1; multiplier < 25000; multiplier++) {
            int potentialWidth = multiplier * sectionWidth;
            int potentialHeight = multiplier * sectionHeight;

            //not enough memory space for large images yet.
            if ((potentialWidth >= 1000) && (potentialHeight >= 1000)) {
                System.out.println("crop to After Width: " + potentialWidth);
                System.out.println("Crop to after height "+potentialHeight);
                templateWidth = potentialWidth;
                templateHeight = potentialHeight;
                break;
            }
        }

        for (int multiplier = 1; multiplier < 25000; multiplier++) {
            int potentialWidth2 = multiplier * template.getWidth();
            int potentialHeight2 = multiplier * template.getHeight();

            if ((potentialWidth2 >= templateWidth) && (potentialHeight2 >= templateHeight)) {
                System.out.println("enlarged After Width: " + potentialWidth2);
                System.out.println("enlarged After Height: " + potentialHeight2);
                enlargedTemplateWidth = potentialWidth2;
                enlargedTemplateHeight = potentialHeight2;
                enlargementFactor = multiplier;
                break;
            }
        }

        BufferedImage enlargedTemplate = new BufferedImage(enlargedTemplateWidth, enlargedTemplateHeight, templateFile.getType());
        for (int x=0; x< enlargedTemplateWidth; x++) {
            for (int y = 0; y < enlargedTemplateHeight; y++) {
                enlargedTemplate.setRGB(x, y, templateFile.getRGB(x / enlargementFactor, y / enlargementFactor));
            }
        }
        System.out.println("enlargedTemplate width " + enlargedTemplate.getWidth());
        System.out.println("enlargedTemplate height " + enlargedTemplate.getHeight());


        int cropTopLeftYCoord = ((enlargedTemplateHeight - templateHeight)/2);
        int cropTopLeftXCoord = ((enlargedTemplateWidth - templateWidth)/2);
        System.out.println("new template");
        System.out.println(2*cropTopLeftXCoord+templateWidth);
        System.out.println(2*cropTopLeftYCoord+templateHeight);

        File outputfile = new File("image.jpg");
        try {
            ImageIO.write(enlargedTemplate, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //PROBLEM WITH CENTERING
        processedTemplateFile = enlargedTemplate.getSubimage(cropTopLeftXCoord, cropTopLeftYCoord, templateWidth, templateHeight);
        System.out.println("new template image generated");
    }
}