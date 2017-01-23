package sample;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Matthew on 08/01/2017.
 */
public class ImageFactory {
    public static void generate(SImage template,List<SImage> imagePool){
        createSections(template, imagePool);
    }
    public static void createSections(SImage template,List<SImage> imagePool ) {
        double mostCommonRatio;
        mostCommonRatio = getMostCommonRatio(imagePool);
        int[][] SectionList = new int[template.getWidth()][template.getHeight()];

    }

    private static double getMostCommonRatio(List<SImage> imagePool) {
        HashMap<double, int> ratioFrequencyMap = new HashMap();
        double selectedImageRatio = 0;
        double targetImageRatio = 0;
        for (SImage selectedImage : imagePool) {
            for (SImage targetImage : imagePool)
                targetImageRatio = targetImage.getWidth() / targetImage.getHeight();
            selectedImageRatio = selectedImage.getWidth() / selectedImage.getHeight();

            if (targetImageRatio == selectedImageRatio) {
                int count = ratioFrequencyMap.get(targetImageRatio);
                ratioFrequencyMap.put(targetImageRatio, count + 1);
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
            System.out.println(ratioFrequencyMap.get(ratio));
        }
        System.out.println(highestRatio);

        return highestRatio;
    }
}