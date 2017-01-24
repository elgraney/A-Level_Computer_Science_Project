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
        float mostCommonRatio;
        mostCommonRatio = getMostCommonRatio(imagePool);
        defineSections(mostCommonRatio);
        int[][] SectionList = new int[template.getWidth()][template.getHeight()];


    }
    private static void defineSections(float mostCommonRatio){
        int sectionWidth = 30/

    }

    private static float getMostCommonRatio(List<SImage> imagePool) {
        HashMap<Float, Integer> ratioFrequencyMap = new HashMap();
        float selectedImageRatio = 0;
        float targetImageRatio = 0;
        System.out.println("Yo");

        //doesn't work, logic error
        for (SImage selectedImage : imagePool) {
            float width = selectedImage.getWidth();
            float height = selectedImage.getHeight();

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


        float highestRatio = 0;
        int highest = 0;
        for (float ratio : ratioFrequencyMap.keySet()) {
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