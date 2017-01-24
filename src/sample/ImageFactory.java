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
        System.out.println("generate");

        createSections(template, imagePool);
    }

    public static void createSections(SImage template,List<SImage> imagePool ) {
        System.out.println("CreateScetion");
        double mostCommonRatio;
        mostCommonRatio = getMostCommonRatio(imagePool);
        int[][] SectionList = new int[template.getWidth()][template.getHeight()];

    }

    private static double getMostCommonRatio(List<SImage> imagePool) {
        HashMap<Double, Integer> ratioFrequencyMap = new HashMap();
        List<SImage> tempImagePool = imagePool;
        double selectedImageRatio = 0;
        double targetImageRatio = 0;
        System.out.println("Yo");

        //doesn't work, logic error
        for (SImage selectedImage : tempImagePool) {
            Boolean notChecked = true;
            selectedImageRatio = selectedImage.getWidth() / selectedImage.getHeight();
            for (double ratio : ratioFrequencyMap.keySet()) {
                if (ratio == selectedImageRatio) {
                    notChecked = false;
                    System.out.println("False");
                }
            }
            //probelm with notChecked variable not behaving as expected.
            if (notChecked = true); {
                System.out.println("Not checked");
                for (SImage targetImage : tempImagePool)
                    targetImageRatio = targetImage.getWidth() / targetImage.getHeight();

                    if (targetImageRatio == selectedImageRatio) {
                        try{
                            int count = ratioFrequencyMap.get(targetImageRatio);
                            ratioFrequencyMap.put(targetImageRatio, count + 1);}
                        catch(Exception e){
                            ratioFrequencyMap.put(targetImageRatio, 1);
                     }
            }}

        }
        double highestRatio = 0;
        int highest = 0;
        System.out.println("Problem?");
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