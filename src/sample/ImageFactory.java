package sample;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Matthew on 08/01/2017.
 */
public class ImageFactory {
    private int sectionWidth;
    private int sectionHeight;

    public static void generate(SImage template,List<SImage> imagePool){
        System.out.println("generate");

        createSections(template, imagePool);
    }

    public static void createSections(SImage template,List<SImage> imagePool ) {
        System.out.println("CreateScetion");
        double mostCommonRatio;
        mostCommonRatio = getMostCommonRatio(imagePool);
        defineSections(mostCommonRatio);
        int[][] SectionList = new int[template.getWidth()][template.getHeight()];


    }
    private static void defineSections(double mostCommonRatio){
        for (int potentialHeight =1; potentialHeight < 100; potentialHeight++){
            double potentialWidth = potentialHeight*mostCommonRatio;
            System.out.println("Before before Width: "+ potentialWidth);
            //potentialWidth = (Math.round(potentialWidth*100)/100);
            System.out.println("Before Width: "+ potentialWidth);
            System.out.println("mod "+ potentialWidth%1) ;
            if ((potentialWidth%1)==0);
                if (potentialWidth>=30);
                    System.out.println("After Width: "+ potentialWidth);

        }


    }


    private static double getMostCommonRatio(List<SImage> imagePool) {
        HashMap<Double, Integer> ratioFrequencyMap = new HashMap();
        double selectedImageRatio = 0;
        double targetImageRatio = 0;
        System.out.println("Yo");

        //doesn't work, logic error
        for (SImage selectedImage : imagePool) {
            double width = selectedImage.getWidth();
            double height = selectedImage.getHeight();

            selectedImageRatio = (width/height);

            //probelm with selectedImage

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