package sample;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

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

    public static void generate(SImage SImageTemplate, ArrayList<SImage> imagePool, BufferedImage templateImage, int analysisLvl) {


        System.out.println("generate");
        analysisLevel = analysisLvl;
        unalteredTemplate = SImageTemplate;
        templateFile = templateImage;
        Section[][] sectionList = formatAllImages(imagePool);
        matchController(imagePool, sectionList);

    }


    public static Section[][] formatAllImages(ArrayList<SImage> imagePool) {
        System.out.println("CreateScetion");
        double mostCommonRatio;
        mostCommonRatio = getMostCommonRatio(imagePool);
        defineSections(mostCommonRatio);
        resizeTemplate(unalteredTemplate);

        System.out.println("new height " + processedTemplateFile.getWidth());
        System.out.println("new Width " + processedTemplateFile.getHeight());
        File outputfile = new File("2ndimage.jpg");
        try {
            ImageIO.write(processedTemplateFile, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int width = processedTemplateFile.getWidth();
        int height = processedTemplateFile.getHeight();

        Section[][] sectionList = new Section[width / sectionWidth][height / sectionHeight];
        for (int ix = 0; ix < width; ix += sectionWidth) {
            for (int iy = 0; iy < height; iy += sectionHeight) {
                //System.out.println("hey!");
                BufferedImage sectionImage = createSectionImage(ix, iy);
                //section image is buffered image, but section does not take that type.
                File sectionfile = new File("section");
                try {
                 ImageIO.write(sectionImage, "jpg", sectionfile);
                } catch (IOException e) {
                     e.printStackTrace();
                }

                Section currentSection = new Section(sectionfile, ix, iy, sectionWidth, sectionHeight, analysisLevel);

                sectionList[ix / sectionWidth][iy / sectionHeight] = currentSection;
                currentSection.setRatioMultiple(1);
            }
        }
        System.out.println("section 1 top left X (should be 0): "+sectionList[0][0].getTopLeftX());
        System.out.println("2D array length: " + sectionList.length * sectionList[0].length);
        cropRatio(mostCommonRatio, imagePool);
        System.out.println("Done.");
        return sectionList;

    }

    private static BufferedImage createSectionImage(int x, int y) {
        return processedTemplateFile.getSubimage(x, y, sectionWidth, sectionHeight);
    }


    private static void defineSections(double mostCommonRatio) {
        for (int potentialHeight = 1; potentialHeight < 1000; potentialHeight++) {
            double potentialWidth = potentialHeight * mostCommonRatio;
            //potentialWidth = (Math.round(potentialWidth*100)/100);
            //System.out.println("Before Width: " + potentialWidth);
            //System.out.println("mod " + potentialWidth % 1);
            double remainder = potentialWidth % 1;
            //System.out.println(remainder);

            if ((remainder == 0.0) && (potentialWidth >= 30) && (potentialHeight >= 30)) {
                System.out.println("After Width: " + potentialWidth);
                sectionWidth = (int) potentialWidth;
                sectionHeight = potentialHeight;
                return;
            }
        }
        System.out.println("NO RESOLUTION FOUND");
    }


    private static double getMostCommonRatio(ArrayList<SImage> imagePool) {
        HashMap<Double, Integer> ratioFrequencyMap = new HashMap();
        double selectedImageRatio = 0;
        double targetImageRatio = 0;
        System.out.println("Yo");
        for (SImage selectedImage : imagePool) {
            double width = selectedImage.getWidth();
            double height = selectedImage.getHeight();

            selectedImageRatio = (width / height);

            System.out.println("selected: " + selectedImageRatio);

            try {
                int count = ratioFrequencyMap.get(selectedImageRatio);
                //System.out.println(count);
                ratioFrequencyMap.put(selectedImageRatio, count + 1);
            } catch (Exception e) {
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
            System.out.println("ratio " + ratio);
            System.out.println("highest " + highest);
        }
        System.out.println("Highest Ratio " + highestRatio);
        return highestRatio;
    }


    private static void resizeTemplate(SImage template) {
        int templateWidth = 0;
        int templateHeight = 0;
        int enlargementFactor = 0;

        int enlargedTemplateWidth = 0;
        int enlargedTemplateHeight = 0;

        for (int multiplier = 1; multiplier < 25000; multiplier++) {
            int potentialWidth = multiplier * sectionWidth;
            int potentialHeight = multiplier * sectionHeight;

            //not enough memory space for large images yet.
            if ((potentialWidth >= 3000) && (potentialHeight >= 3000)) {
                System.out.println("crop to After Width: " + potentialWidth);
                System.out.println("Crop to after height " + potentialHeight);
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
        for (int x = 0; x < enlargedTemplateWidth; x++) {
            for (int y = 0; y < enlargedTemplateHeight; y++) {
                enlargedTemplate.setRGB(x, y, templateFile.getRGB(x / enlargementFactor, y / enlargementFactor));
            }
        }
        System.out.println("enlargedTemplate width " + enlargedTemplate.getWidth());
        System.out.println("enlargedTemplate height " + enlargedTemplate.getHeight());


        int cropTopLeftYCoord = ((enlargedTemplateHeight - templateHeight) / 2);
        int cropTopLeftXCoord = ((enlargedTemplateWidth - templateWidth) / 2);
        System.out.println("new template");
        System.out.println(2 * cropTopLeftXCoord + templateWidth);
        System.out.println(2 * cropTopLeftYCoord + templateHeight);

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


    private static void cropRatio(double mostCommonRatio, ArrayList<SImage> imagePool) {
        double widthRatio = mostCommonRatio;
        double heightRatio = 1;
        for (SImage image : imagePool) {

            //store image ratio in SImage?
            double imageWidthRatio = image.getWidth() / (float) image.getHeight();
            System.out.println("This image ratio: " + imageWidthRatio);
            final double imageHeightRatio = 1;
            if ((widthRatio * 0.75) <= imageWidthRatio && imageWidthRatio < (widthRatio * 1.5)) {
                System.out.println("standard");
                image.setRatioMultiple(1);
                crop(widthRatio, heightRatio, imageWidthRatio, image);
            } else if ((widthRatio * 1.5) <= imageWidthRatio && imageWidthRatio < (widthRatio * 2.5)) {
                System.out.println("2width");
                image.setRatioMultiple(2);
                crop(2 * widthRatio, heightRatio, imageWidthRatio, image);

            } else if ((widthRatio * 2.5) <= imageWidthRatio) {
                System.out.println("3width");
                image.setRatioMultiple(3);
                crop(3 * widthRatio, heightRatio, imageWidthRatio, image);

            } else if ((widthRatio * 5 / 12) <= imageWidthRatio && imageWidthRatio < (widthRatio * 0.75)) {
                System.out.println("2height");
                image.setRatioMultiple(0.5);
                crop(widthRatio, 2 * heightRatio, imageWidthRatio, image);

            } else if (imageWidthRatio < (widthRatio * 5 / 12)) {
                System.out.println("3height");
                image.setRatioMultiple(1 / 3);
                crop(widthRatio, 3 * heightRatio, imageWidthRatio, image);
            }

        }
        //return null;
    }

    private static SImage crop(double mostCommonWidthRatio, double mostCommonHeightRatio, double imageRatio, SImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        System.out.println("mostCommonWidthRatio " + mostCommonWidthRatio);
        System.out.println("mostCommonheightRatio " + mostCommonHeightRatio);

        //checking division
        System.out.println("To crop Ratios:");
        System.out.println(mostCommonWidthRatio / (float) mostCommonHeightRatio);
        System.out.println(mostCommonWidthRatio / mostCommonHeightRatio);

        double toCropRatio = mostCommonWidthRatio / (float) mostCommonHeightRatio;

        System.out.println("imageRatio " + imageRatio);

        if (imageRatio > (mostCommonWidthRatio / (float) mostCommonHeightRatio)) {
            double widthModifier = ((height / (float) width) * toCropRatio);
            System.out.println("Width decrease");
            System.out.println("Width Modifier: " + widthModifier);
            int widthCropValue = (int) (width * widthModifier) + 1;
            System.out.println("width crop value " + widthCropValue);
            try {
                image.crop(widthCropValue, height);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("crop, 1st if. widthcropvalue: " + widthCropValue);
            return null;
        } else if (imageRatio < (mostCommonWidthRatio / (float) mostCommonHeightRatio)) {
            double heightModifier = (width / (float) (height * toCropRatio));
            System.out.println("height decrease");
            int heightCropValue = (int) (height * heightModifier);
            System.out.println("height crop value " + heightCropValue);
            try {
                image.crop(width, heightCropValue);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("crop, 2nd if. heightcropvalue: " + heightCropValue);
            return null;
        } else {
            System.out.println("do not crop");
            return null;
        }

        //note: due to the necessity to have integers for dimentions, images will allways be resized so that the width is slightly greater than it should be, if an integer cannot be found.
    }

    //pass SImage pool
    private static void matchController(ArrayList<SImage> imagePool, Section[][] sectionList) {

        //start with largest ratios, then move to standard ratio
        //since compund sections aren't in yet, just standard
        ArrayList<SImage> ratio1Pool = populateList(1, imagePool);
        ArrayList<Section> ratio1Sections = reformatAndPopulateSectionArray(sectionList, 1);
        System.out.println("ratio refined pool length " + ratio1Pool.size());
        System.out.println("ratio refined section list length " + ratio1Sections.size());

        matchSections(ratio1Sections, ratio1Pool);

        //great, so ratio is sorted, now for the splitting into colour arrays.
        //will have to do in new function.
        //create 3 colour lists, sort them
        //start checking sections
        //filter 3 lists
        //recombine 3 lists
        //use recombined list to find closest section.
        generateOutput(sectionList);

    }

    private static void matchSections(ArrayList<Section> sectionList, ArrayList<SImage> imageList) {
        System.out.println("Match sections");
        ArrayList<SImage> redSortedImages = MergeSort.mergeSort(imageList, 0);
        ArrayList<SImage> greenSortedImages = MergeSort.mergeSort(imageList, 1);
        ArrayList<SImage> blueSortedImages = MergeSort.mergeSort(imageList, 2);
        System.out.println("Merge complete");
        System.out.println("SectionList size: " + sectionList.size());

        for (Section section : sectionList) {
            System.out.println("Entered for?");
            double sectionRed = section.getMeanRGB(0);
            double sectionGreen = section.getMeanRGB(1);
            double sectionBlue = section.getMeanRGB(2);

            if (redSortedImages.size() > 25) {
                redSortedImages = binarySearch(redSortedImages, 0, sectionRed);
            }
            if (greenSortedImages.size() > 25) {
                greenSortedImages = binarySearch(greenSortedImages, 1, sectionGreen);
            }
            if (blueSortedImages.size() > 25) {
                blueSortedImages = binarySearch(blueSortedImages, 2, sectionBlue);
            }
            System.out.println("Binary Search complete or skipped.");
            Set<SImage> recombinedList = new HashSet<SImage>();
            recombinedList.addAll(redSortedImages);
            recombinedList.addAll(greenSortedImages);
            recombinedList.addAll(blueSortedImages);
            System.out.println("recombined.");

            int difference = 0;
            do {
            for (SImage image : recombinedList) {
                    if ((image.getMeanRGB(0) - difference) <= sectionRed && (image.getMeanRGB(0) + difference) >= sectionRed &&
                            (image.getMeanRGB(1) - difference) <= sectionGreen && (image.getMeanRGB(1) + difference) >= sectionGreen &&
                            (image.getMeanRGB(2) - difference) <= sectionBlue && (image.getMeanRGB(2) + difference) >= sectionBlue
                            ) {
                        section.setLinkedImage(image);
                        System.out.println(section.file);
                        System.out.println(image.file);
                        difference = 100;
                        break;
                    } else {
                        //System.out.println("Incrementing difference");
                        difference++;
                    }
                }
                //now match them
                //follow design algorithm.
            }
            while (difference < 100);
            System.out.println("Match section complete");
            System.out.println("For skipped.");
        }
        System.out.println("every section covered.");
    }

    private static ArrayList<SImage> binarySearch(ArrayList<SImage> imageList, int colour, double targetColour) {

        ArrayList<SImage> reducedImageArray = new ArrayList<SImage>();
        int size = imageList.size();
        int low = 0;
        int high = size - 1;
        int middle = 0;

        while (high > low) {
            middle = (low + high) / 2;
            if (imageList.get(middle).getMeanRGB(colour) == targetColour) {
                for (int index = (middle - 5 < 0 ? 0 : middle-5); index <= (middle + 5 > size -1  ? size -1  : middle+ 5); index++) {
                    //Possible array boundary error
                    reducedImageArray.add(imageList.get(index));
                    return reducedImageArray;
                }
            }
            if (imageList.get(middle).getMeanRGB(colour) < targetColour) {
                low = middle + 1;
            }
            if (imageList.get(middle).getMeanRGB(colour) > targetColour) {
                high = middle - 1;
            }
        }
        for (int index = (middle - 5 < 0 ? 0 : middle-5); index <= (middle + 5 > size -1  ? size -1  : middle+ 5); index++) {
            reducedImageArray.add(imageList.get(index));
        }
        return reducedImageArray;
    }



    private static ArrayList<SImage> populateList(double ratio, ArrayList<SImage> imagePool) {
        ArrayList<SImage> newList = new ArrayList<SImage>();
        ;
        for (SImage image : imagePool) {
            if (image.getRatio() == ratio) {
                newList.add(image);
            }
            //for each image in image pool
            //if image.getratio = ratio
            //add to this new array.
        }
        return newList;
    }


    private static ArrayList<Section> reformatAndPopulateSectionArray(Section[][] sectionList, double ratio){
        ArrayList<Section> formattedSectionList = new ArrayList<Section>();

        for ( int y=0; y< sectionList[0].length; y++){
            for ( int x=0; x< sectionList.length; x++){
                formattedSectionList.add(sectionList[x][y]);
            }
        }
        ArrayList<Section> newSectionList = new ArrayList<Section>();

        for (Section image : formattedSectionList) {
            System.out.println(image.getRatio());
            if (image.getRatio() == ratio) {
                newSectionList.add(image);
            }
            //for each image in image pool
            //if image.getratio = ratio
            //add to this new array.
        }

        return newSectionList;


}

    private static void generateOutput(Section[][] sectionArray) {

        BufferedImage outputBufferedImage = processedTemplateFile;


        for (Section[] sectionColumn : sectionArray) {
            for (Section section : sectionColumn) {
                //insert method 1 and 2 branch here
                int startX = section.getTopLeftX();
                int startY = section.getTopLeftY();

                for (int x = 0; x < sectionWidth ; x++) {
                    for (int y = 0; y < sectionHeight; y++) {
                        SImage linkedSImage = section.getLinkedImage();

                        File imagefile = linkedSImage.file;

                        BufferedImage linkedImage = null;

                        try {
                            linkedImage = ImageIO.read(imagefile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        double enlargementFactor = sectionHeight / (float) linkedSImage.getHeight();
                        //System.out.println("X: "+x);
                        //System.out.println("Y: "+y);
                        //System.out.println("enlargement factor: "+enlargementFactor);
                        outputBufferedImage.setRGB(startX +x, startY+y, linkedImage.getRGB((int) Math.round(x / enlargementFactor), (int) Math.round(y / enlargementFactor)));


                    }

                }
                //System.out.println("+1 section drawn.");
            }

        }
        File outputfile = new File("outputImage.jpg");
        try {
            System.out.print("Writing...");
            ImageIO.write(outputBufferedImage, "jpg", outputfile);
        } catch (IOException e) {
            System.out.print("eh?!");
        }

    }
}



