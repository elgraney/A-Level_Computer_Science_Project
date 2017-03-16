package sample;

import javafx.scene.image.*;

import java.awt.*;
import java.awt.Image;
import java.awt.image.PackedColorModel;
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
    //these are values used frequently throughout the class
    private static int sectionWidth;
    private static int sectionHeight;
    private static SImage unalteredTemplate;
    private static BufferedImage templateFile;

    //these two variables only exist in order to pass data about the generation back to the output window
    private static Section[][] finalSectionList;
    //hash set is used because i want a count of all unique images. HashSets don't allow duplicates, so every item in it must be unique.
    private static Set<SImage> usedImages = new HashSet<SImage>();

    private static BufferedImage processedTemplateFile;

    public static Boolean generating = false;


    //SET CONSTANTS FOR OUTPUT RESOLUTION AND SECTION SIZE

    public static File generate(SImage SImageTemplate, ArrayList<SImage> imagePool, BufferedImage templateImage, int outputRes, int generationStyle, String outputFormat) throws GenerationException {
        //generating is set to true and will be kept as true untill the generation is finished.
        generating = true;
        System.out.println("generate");
        unalteredTemplate = SImageTemplate;
        templateFile = templateImage;
        //all images are formatted and ordered in preparation for matching and generating
        Section[][] sectionList = formatAllImages(imagePool, outputRes, generationStyle);
        //following this the matching process begins
        File outputImage = matchController(imagePool, sectionList, outputFormat, generationStyle);
        //the final output image is returned to the Controller class
        return outputImage;

    }

//this class takes the images pool and the generation preferences and uses them to eventually return a list of sections of the larger template image
    private static Section[][] formatAllImages(ArrayList<SImage> imagePool, int outputRes, int generationStyle) throws GenerationException {
        System.out.println("CreateSection");
        double mostCommonRatio;
        //MCR is calculated here. It is fundamental to the rest of the formatting
        mostCommonRatio = getMostCommonRatio(imagePool);
        //Define sections uses the MCR to decide the dimensions of a section
        defineSections(mostCommonRatio);
        //resizeTemplate enlarges and crops the original template so that the sections fit into it without remainder
        resizeTemplate(unalteredTemplate, outputRes);

        System.out.println("new height " + processedTemplateFile.getWidth());
        System.out.println("new Width " + processedTemplateFile.getHeight());
        File outputFile = new File("2ndimage.jpg");
        try {
            ImageIO.write(processedTemplateFile, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int width = processedTemplateFile.getWidth();
        int height = processedTemplateFile.getHeight();


        //the following section separates the enlarged template into many sections. Each section is created as a new independent image.
        Section[][] sectionList = new Section[width / sectionWidth][height / sectionHeight];
        //the loop iterates though every top left coordinate for every section.
        for (int ix = 0; ix < width; ix += sectionWidth) {
            for (int iy = 0; iy < height; iy += sectionHeight) {
                //The section is created and given the appropriate data
                BufferedImage sectionImage = createSectionImage(ix, iy);
                //the new section is created as a new file
                File sectionfile = new File("section");
                try {
                    ImageIO.write(sectionImage, "jpg", sectionfile);
                } catch (IOException e) {
                    throw new GenerationException();
                }
                //the section image is used to make a new instance of the class Section
                Section currentSection = new Section(sectionfile, ix, iy, sectionWidth, sectionHeight);
                //the section is placed in the appropriate place in a 2D array
                //the array is set up so that the conceptual table it creates is the same as the actual layout of the section on the template
                sectionList[ix / sectionWidth][iy / sectionHeight] = currentSection;
                currentSection.setRatioMultiple(1);
            }
        }
        //Compound sections can be set up only after sections have been
        defineCompoundSections(sectionList);
        System.out.println("section 1 top left X (should be 0): " + sectionList[0][0].getTopLeftX());
        System.out.println("2D array length: " + sectionList.length * sectionList[0].length);
        //every image in the image pool is cropped so that it will fit perfectly into the region of a section
        cropRatio(mostCommonRatio, imagePool, generationStyle);
        System.out.println("Done.");
        //the 2D sectionList array is returned
        return sectionList;

    }
    //createSectionImage takes the top left coordinates for a section and creates a new sub-image the size of a section from the larger template from these coordinates
    private static BufferedImage createSectionImage(int x, int y) {
        return processedTemplateFile.getSubimage(x, y, sectionWidth, sectionHeight);
    }

    //defineSections finds the dimensions of a standard section based on the MCR
    private static void defineSections(double mostCommonRatio) throws GenerationException {
        //the following loop finds every width value under 400 that is a multiple of another integer times MCR
        for (int potentialHeight = 1; potentialHeight < 400; potentialHeight++) {
            double potentialWidth = potentialHeight * mostCommonRatio;
            double remainder = potentialWidth % 1;
            //if that width value is greater than 40 and also an integer it become the sectionWidth. The associated height becomes the sectionHeight
            if ((remainder == 0.0) && (potentialWidth >= 40) && (potentialHeight >= 40)) {
                System.out.println("After Width: " + potentialWidth);
                sectionWidth = (int) potentialWidth;
                sectionHeight = potentialHeight;
                return;
            }
        }
        //if there is no possible section smaller than 400 then the generation cancels because a high quality output is impossible
        System.out.println("no possible section under 400x400");
        throw new GenerationException();
    }
    //defineCompoundSections groups sections of similar colour into a larger compound section
    private static void defineCompoundSections(Section[][] sectionList) {
        //sectionSimilarity is a string that will later be analysed to deduce the layout of similar sections
        String sectionSimilarity = "";
        //for each section in sectionList
        for (int column = 0; column <= sectionList.length - 1; column++) {
            for (int row = 0; row <= sectionList[0].length - 1; row++) {
                //proceed if the section is not already in a compound section
                if (sectionList[column][row].isInCompoundSection() == false) {
                    sectionSimilarity = "";
                    //for the 8 other nearby sections to the bottom right
                    for (int x = column; x <= (column + 2 > sectionList.length - 1 ? sectionList.length - 1 : column + 2); x++) {
                        for (int y = row; y <= (row + 2 > sectionList[0].length - 1 ? sectionList[0].length - 1 : row + 2); y++) {
                            //if the section red, green and blue are each within +-5 of the top left section and the section is not already in a compoundSection
                            if ((sectionList[column][row].getMeanOfModesRGB(0) - 5 <= sectionList[x][y].getMeanOfModesRGB(0)) &&
                                    (sectionList[x][y].getMeanOfModesRGB(0) <= (sectionList[column][row].getMeanOfModesRGB(0) + 5)) &&
                                    (sectionList[column][row].getMeanOfModesRGB(1) - 5 <= sectionList[x][y].getMeanOfModesRGB(1)) &&
                                    (sectionList[x][y].getMeanOfModesRGB(1) <= (sectionList[column][row].getMeanOfModesRGB(1) + 5)) &&
                                    (sectionList[column][row].getMeanOfModesRGB(2) - 5 <= sectionList[x][y].getMeanOfModesRGB(2)) &&
                                    (sectionList[x][y].getMeanOfModesRGB(2) <= (sectionList[column][row].getMeanOfModesRGB(2) + 5)) &&
                                    sectionList[x][y].isInCompoundSection() == false) {
                                //if the section is similar '1' is added to the sectionSimilarity string
                                sectionSimilarity = sectionSimilarity + "1";
                            } else {
                                //if the section isn't similar '0' is added to the sectionSimilarity string
                                sectionSimilarity = sectionSimilarity + "0";
                            }
                        }
                    }
                    //the following switch statement acts on significant sectionSimilarity strings, making the appropriate compound sections
                    switch (sectionSimilarity) {
                        case "110000000":
                            //make 2x1 compound section
                            sectionList[column][row].setCompound(sectionWidth * 2, sectionHeight, column, row, 2);

                            for (int x = column; x < column + 2; x++) {
                                int y = row;
                                sectionList[x][y].setInCompound();
                            }
                            break;
                        case "111000000":
                            //make 3x1 compound section
                            sectionList[column][row].setCompound(sectionWidth * 3, sectionHeight, column, row, 3);

                            for (int x = column; x < column + 3; x++) {
                                int y = row;
                                sectionList[x][y].setInCompound();
                            }
                            break;
                        case "100100000":
                            //make 1x2 compound section
                            sectionList[column][row].setCompound(sectionWidth, sectionHeight * 2, column, row, 4);

                            for (int y = row; y < row + 2; y++) {
                                int x = column;
                                sectionList[x][y].setInCompound();
                            }
                            break;
                        case "100100100":
                            //make 1x3 compound section
                            sectionList[column][row].setCompound(sectionWidth, sectionHeight * 3, column, row, 5);

                            for (int y = row; y < row + 3; y++) {
                                int x = column;
                                sectionList[x][y].setInCompound();
                            }
                            break;
                        case "110110000":
                            //make 2x2 compound section
                            sectionList[column][row].setCompound(sectionWidth * 2, sectionHeight * 2, column, row, 1);

                            for (int y = row; y < row + 2; y++) {
                                for (int x = column; x < column + 2; x++) {
                                    sectionList[x][y].setInCompound();
                                }
                            }
                            break;
                        case "111111111":
                            //make 3x3 compound section
                            sectionList[column][row].setCompound(sectionWidth * 3, sectionHeight * 3, column, row, 1);

                            for (int y = row; y < row + 3; y++) {
                                for (int x = column; x < column + 3; x++) {
                                    sectionList[x][y].setInCompound();

                                }
                            }
                            System.out.println("break");
                            break;
                    }
                    //corrects the central coordinate of of the compound section so that is directly in the middle
                    sectionList[column][row].updateCentre();
                }
            }
        }

    }

    //This finds the most common ratio for all the images in the pool in decimal form, then returns it
    public static double getMostCommonRatio(ArrayList<SImage> imagePool) {
        HashMap<Double, Integer> ratioFrequencyMap = new HashMap();
        double selectedImageRatio = 0;
        for (SImage selectedImage : imagePool) {
            double width = selectedImage.getWidth();
            double height = selectedImage.getHeight();
            selectedImageRatio = (width / height);
            //tries to increment the frequency for a previously seen ratio
            try {
                int count = ratioFrequencyMap.get(selectedImageRatio);
                ratioFrequencyMap.put(selectedImageRatio, count + 1);
            //if the ratio has not yet been seen, and error will be thrown, at which point that ratio is saved with frequency 1
            } catch (Exception e) {
                ratioFrequencyMap.put(selectedImageRatio, 1);
            }
        }
        //Selects the ratio with the highest associated frequency
        double highestRatio = 0;
        int highest = 0;
        for (double ratio : ratioFrequencyMap.keySet()) {
            if (ratioFrequencyMap.get(ratio) > highest) {
                highestRatio = ratio;
                highest = ratioFrequencyMap.get(ratio);
            }
        }
        return highestRatio;
    }

    //resizeTemplate calculates how far it must be enlarged, enlarges it, then crops it so that it is perfectly divisible by sections
    private static void resizeTemplate(SImage template, int outputRes) {
        int templateWidth = 0;
        int templateHeight = 0;
        int enlargementFactor = 0;

        int enlargedTemplateWidth = 0;
        int enlargedTemplateHeight = 0;

        //finds the lowest integer multiple of sectionWidth and sectionHeight that are greater than the user-specified minimum resolution
        //the calculated values will be the final resolution of the template image
        for (int multiplier = 1; multiplier < 25000; multiplier++) {
            int potentialWidth = multiplier * sectionWidth;
            int potentialHeight = multiplier * sectionHeight;
            //if current test values are both greater than the minimum values, they are made permanent
            if ((potentialWidth >= outputRes) && (potentialHeight >= outputRes)) {
                templateWidth = potentialWidth;
                templateHeight = potentialHeight;
                break;
            }
        }
        //the template image can not be immediately enlarged to the right size without stretching it, so it must be enlarged and cropped first
        //this finds the lowest integer multiple of template height and width greater than the previously calculated final height and width
        for (int multiplier = 1; multiplier < 25000; multiplier++) {
            int potentialWidth2 = multiplier * template.getWidth();
            int potentialHeight2 = multiplier * template.getHeight();
            //if current test values are both greater than the calculated final values, they are made permanent
            if ((potentialWidth2 >= templateWidth) && (potentialHeight2 >= templateHeight)) {
                enlargedTemplateWidth = potentialWidth2;
                enlargedTemplateHeight = potentialHeight2;
                enlargementFactor = multiplier;
                break;
            }
        }
        //the template is now enlarged to the previously calculated proportions
        BufferedImage enlargedTemplate = new BufferedImage(enlargedTemplateWidth, enlargedTemplateHeight, templateFile.getType());
        //the pixels of the old original image are enlarged and written into place in the new larger image
        for (int x = 0; x < enlargedTemplateWidth; x++) {
            for (int y = 0; y < enlargedTemplateHeight; y++) {
                enlargedTemplate.setRGB(x, y, templateFile.getRGB(x / enlargementFactor, y / enlargementFactor));
            }
        }
        System.out.println("enlargedTemplate width " + enlargedTemplate.getWidth());
        System.out.println("enlargedTemplate height " + enlargedTemplate.getHeight());

        //this enlarged image is then cropped down to the final height and width
        //an even amount is cropped from each side of the image
        int cropTopLeftYCoord = ((enlargedTemplateHeight - templateHeight) / 2);
        int cropTopLeftXCoord = ((enlargedTemplateWidth - templateWidth) / 2);

        //the final image is extracted from the enalrged image and stored in a private variable for use throughout ImageFactory
        File outputFile = new File("image.jpg");
        try {
            ImageIO.write(enlargedTemplate, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        processedTemplateFile = enlargedTemplate.getSubimage(cropTopLeftXCoord, cropTopLeftYCoord, templateWidth, templateHeight);
    }

    //cropRatio is the first step towards resizing all of the images in the image pool to fit into sections
    private static void cropRatio(double mostCommonRatio, ArrayList<SImage> imagePool, int generationStyle) throws GenerationException {
        //ratio is width/height, which can also be seen as the relative size of width if height was 1
        //this is why heightRatio is 1 and widthRatio is the MCR (likely a decimal somewhere between 1.0 & 2.0)
        double widthRatio = mostCommonRatio;
        final double heightRatio = 1;

        //every image is cropped
        for (SImage image : imagePool) {
            //the individual ratio of each image is found
            double imageWidthRatio = image.getWidth() / (float) image.getHeight();
            final double imageHeightRatio = 1;
            //The images are cropped to the most appropriate multiple of sections. This means a portrait image might be mapped over two vertically adjacent sections rather than forced into a single section.
            //catches images best suited to the proportions of a normal single section
            if ((widthRatio * 0.75) <= imageWidthRatio && imageWidthRatio < (widthRatio * 1.5)) {
                //the image ratio multiple is used later so that images are only matched to sections of the same ratio multiple
                image.setRatioMultiple(1);
                //generationStyle refers to "ordered" or "messy" from the user generation options
                //images are only cropped if the style is "ordered"
                if (generationStyle == 1) {
                    //calls crop with the correct proportions to crop to
                    crop(widthRatio, heightRatio, imageWidthRatio, image);
                }

            //catches images best suited to the proportions of 2 horizontally adjacent sections
            } else if ((widthRatio * 1.5) <= imageWidthRatio && imageWidthRatio < (widthRatio * 2.5)) {
                image.setRatioMultiple(2);
                if (generationStyle == 1){
                    crop(2 * widthRatio, heightRatio, imageWidthRatio, image);}

            //catches images best suited to the proportions of 3 horizontally adjacent sections
            } else if ((widthRatio * 2.5) <= imageWidthRatio) {
                image.setRatioMultiple(3);
                if (generationStyle == 1){
                    crop(3 * widthRatio, heightRatio, imageWidthRatio, image);}

            //catches images best suited to the proportions of 2 vertically adjacent sections
            } else if ((widthRatio * 5 / 12) <= imageWidthRatio && imageWidthRatio < (widthRatio * 0.75)) {
                //the multiple is set to 4 rather than 0.5 (which it should be) because it must be an integer to work
                image.setRatioMultiple(4);
                if (generationStyle == 1){
                    crop(widthRatio, 2 * heightRatio, imageWidthRatio, image);}


            //catches images best suited to the proportions of 3 vertically adjacent sections
            } else if (imageWidthRatio < (widthRatio * 5 / 12)) {
                //the multiple is set to 5 rather than 1/3 (which it should be) because it must be an integer to work
                image.setRatioMultiple(5);
                if (generationStyle == 1){
                    crop(widthRatio, 3 * heightRatio, imageWidthRatio, image);
            }
            }

        }
    }
    //Crop first figures out whether it needs to reduce height or width then carries out the cropping process
    private static void crop(double mostCommonWidthRatio, double mostCommonHeightRatio, double imageRatio, SImage image) throws GenerationException {
        int width = image.getWidth();
        int height = image.getHeight();

        //the output of this calculation can be used to infer the relative proportions of the image height and width and the ratio it needs to be cropped to
        double toCropRatio = mostCommonWidthRatio / (float) mostCommonHeightRatio;

        //if the output is less than the image ratio it can be inferred that the width of this image is too great and must be reduced
        if (imageRatio > toCropRatio) {
            //this calculates a modifier by which to times the width to bring it to the required size
            double widthModifier = ((height / (float) width) * toCropRatio);
            //this modifier must be less than 1

            //this ensures the width value is always rounded up. It would be unacceptable for an image to be 1 pixel to small, therefore leaving a region of empty space.
            int widthCropValue = (int) (width * widthModifier) + 1;
            //the image is then finally actually cropped
            try {
                image.crop(widthCropValue, height);
            } catch (IOException e) {
                throw new GenerationException();
            }
            return;

        //if the output is greater than the image ratio it can be inferred that the height of this image is too great and must be reduced
        //the process is otherwise the same as for width
        } else if (imageRatio < toCropRatio) {
            double heightModifier = (width / (float) (height * toCropRatio));
            int heightCropValue = (int) (height * heightModifier);
            try {
                image.crop(width, heightCropValue);
            } catch (IOException e) {
                throw new GenerationException();
            }
            return;

        }
        return;
    }

    //match controller takes the imagePool and sectionList and matches each section to the most appropriate image
    private static File matchController(ArrayList<SImage> imagePool, Section[][] sectionList, String outputFormat, int generationStyle) throws GenerationException {

        //Large compound sections are matched first, partially because they could be broken back down into standard section (meaning standard sections must be matched last) and partially because in the messy generation format it looks better if larger images are placed under smaller images
        //the imagePool is reduced to only relevant images (ratio multiple 3) to reduce the duration of the linear search that appears later
        ArrayList<SImage> ratio3Pool = populateList(3, imagePool);
        //the section list is reformatted from a 2D array into an ArrayList of relevant sections only (ratio multiple 3)
        ArrayList<Section> ratio3Sections = reformatAndPopulateSectionArray(sectionList, 3);

        //sections of this ratio are matched
        matchSections(ratio3Sections, ratio3Pool, sectionList);

        //the same process is repeated for all the other possible ratios

        //ratio 5 (1/3)
        ArrayList<SImage> ratioThirdPool = populateList(5, imagePool);
        ArrayList<Section> ratioThirdSections = reformatAndPopulateSectionArray(sectionList, 5);
        matchSections(ratioThirdSections, ratioThirdPool, sectionList);

        //ratio 2
        ArrayList<SImage> ratio2Pool = populateList(2, imagePool);
        ArrayList<Section> ratio2Sections = reformatAndPopulateSectionArray(sectionList, 2);
        matchSections(ratio2Sections, ratio2Pool, sectionList);

        //ratio 4 (0.5)
        ArrayList<SImage> ratioHalfPool = populateList(4, imagePool);
        ArrayList<Section> ratioHalfSections = reformatAndPopulateSectionArray(sectionList, 4);
        matchSections(ratioHalfSections, ratioHalfPool, sectionList);


        //ratio 1
        //this must be done last so that all broken down compound sections are included in this matching process
        //to clarify, if this was done first then a 2x1 compound section was broken down into two standard sections, those two standard sections would never be matched.
        ArrayList<SImage> ratio1Pool = populateList(1, imagePool);
        ArrayList<Section> ratio1Sections = reformatAndPopulateSectionArray(sectionList, 1);
        matchSections(ratio1Sections, ratio1Pool, sectionList);

        //the final section list includes every section with its associated image. The sections will not be edited futher during the running of the program
        finalSectionList = sectionList;

        //the final output is generated then returned
        File outputImage = generateOutput(sectionList, outputFormat, generationStyle);
        return outputImage;
    }

    //sections are linked to the most appropriate image
    private static void matchSections(ArrayList<Section> sectionList, ArrayList<SImage> imageList, Section[][] sectionArray) throws GenerationException {

        //the image pool is reduced before matching begins. This decreases the time complexity of the matching algorithm by making it largely logarithmic rather than linear
        ArrayList<SImage> redSortedImages;
        ArrayList<SImage> greenSortedImages;
        ArrayList<SImage> blueSortedImages;
        //if there are images of this ratio in the imagePool, proceed
        if (imageList.size() != 0) {
            //sorts the images into 3 different lists, ordered by ascending red, green and blue values respectively
            //this is required for the binary search later
            redSortedImages = MergeSort.mergeSort(imageList, 0);
            greenSortedImages = MergeSort.mergeSort(imageList, 1);
            blueSortedImages = MergeSort.mergeSort(imageList, 2);
        //if there are no possible matches then all of the compound sections can be immediately broken down
        } else {
            for (Section section : sectionList) {
                compoundSectionBreakdown(section, sectionArray);
            }
            return;
        }
        //the beginning of the actual matching process
        for (Section section : sectionList) {
            //must not already be in a compound section
            if ((section.isInCompoundSection() == false) || (section.isCompoundSectionMarker() == true)) {
                //uses mean of modes algorithm because it is most frequently the best choice of analysis
                double sectionRed = section.getMeanOfModesRGB(0);
                double sectionGreen = section.getMeanOfModesRGB(1);
                double sectionBlue = section.getMeanOfModesRGB(2);

                int sortedListMaxRange = 5;
                int difference = 0;
                //this loop increments from 0 to 999, exiting when the closest match is found
                while ((difference != 999)) {
                    //each RGB list is passed through a binary search to return only the images with R, G and B values close to the RGB of the target section
                    //sortedListMaxRange directly affects the number of items returned from this. The greater it is, the more values are returned
                    ArrayList<SImage> newRedSortedImages = binarySearch(redSortedImages, 0, sectionRed, sortedListMaxRange);
                    ArrayList<SImage> newGreenSortedImages = binarySearch(greenSortedImages, 1, sectionGreen, sortedListMaxRange);
                    ArrayList<SImage> newBlueSortedImages = binarySearch(blueSortedImages, 2, sectionBlue, sortedListMaxRange);

                    //HashSet is used to remove dupicates. It is a data type that does not allow the entry of an item that already exists.
                    //this also servers to minimise the size of the linear search
                    Set<SImage> recombinedList = new HashSet<SImage>();
                    recombinedList.addAll(newRedSortedImages);
                    recombinedList.addAll(newGreenSortedImages);
                    recombinedList.addAll(newBlueSortedImages);

                    difference = 0;
                    do {
                        for (SImage image : recombinedList) {
                            //if the image has adequately similar colour and is included in the pool it is linked
                            if (((image.getMeanRGB(0) - difference) <= sectionRed) && ((image.getMeanRGB(0) + difference) >= sectionRed) &&
                                    ((image.getMeanRGB(1) - difference) <= sectionGreen) && ((image.getMeanRGB(1) + difference)) >= sectionGreen &&
                                    ((image.getMeanRGB(2) - difference) <= sectionBlue) && ((image.getMeanRGB(2) + difference) >= sectionBlue) &&
                                    image.selected == true
                                    ) {
                                section.setLinkedImage(image);
                                //usedImages is a metadata variable used in the output window to display total and unique images
                                usedImages.add(image);
                                //difference is set to 998 so that when it increments by 1 after this it equals 999 and the loop ends
                                difference = 998;
                                break;
                            }
                        }
                        //if the best match has RGB greater than +-20 difference from the section and the section is a compound section of unusual ratio (not ratio 1), it is broken down
                        //it can't be ratio 1 because 3x3 and 2x2 compound sections are matched in the same pass as regular sections, so if they are broken down, the sections making up those compound sections are never matched
                        //there would also be no benefit in treating these compound sections differently to regular sections because they can only be matched to the same images as can be matched to regular sections
                        if (difference >=20 && section.isCompoundSectionMarker()==true && section.getRatio()!=1){
                            difference = 150;
                        }
                        //increments difference by 1
                        difference++;
                    }
                    //the loop exits if the difference reaches 150, at which point the only possible match is so inappropriate that it isn't worth doing.
                    while (difference < 150);
                    //this catches instances when a match was not found: when a match is found difference will be set to 999
                    if (difference != 999) {
                        //increases the potential number of images returned from the binary search
                        sortedListMaxRange += 5;
                        //if sortedListMax has been increased to 100 and there are still no matches, it is highly unlikely that there is a suitable image to match to the section
                        if (sortedListMaxRange > 100) {
                            //it is dealt with appropriately in compoundSectionBreakdown
                            compoundSectionBreakdown(section, sectionArray);
                            break;
                        }
                    }
                }
            }
        }
        return;
    }

    //This deals with failed sections that have not found any images to match with
    private static void compoundSectionBreakdown(Section section, Section[][] sectionArray) throws GenerationException {
        //the type of compound section directly affects which sections must be made independent
        //if the section is 3x3, break down into 9 independent sections
        if ((section.getWidth() == sectionWidth * 3) && (section.getHeight() == sectionHeight * 3)) {
            for (int y = section.getY(); y < section.getY() + 3; y++) {
                for (int x = section.getX(); x < section.getX() + 3; x++) {
                    sectionArray[x][y].breakdown(sectionWidth, sectionHeight);
                }
            }
        //if the section is 3x1, break down into 3 independent sections
        } else if ((section.getWidth() == sectionWidth * 3) && (section.getHeight() == sectionHeight * 1)) {
            System.out.println("Caught 3x1 to breakdown.");
            for (int y = section.getY(); y < section.getY() + 1; y++) {
                for (int x = section.getX(); x < section.getX() + 3; x++) {
                    sectionArray[x][y].breakdown(sectionWidth, sectionHeight);
                }
            }
        //if the section is 2x2, break down into 4 independent sections
        } else if ((section.getWidth() == sectionWidth * 2) && (section.getHeight() == sectionHeight * 2)) {
            System.out.println("Caught 2x2 to breakdown.");
            for (int y = section.getY(); y < section.getY() + 2; y++) {
                for (int x = section.getX(); x < section.getX() + 2; x++) {
                    sectionArray[x][y].breakdown(sectionWidth, sectionHeight);
                }
            }
        //if the section is 1x3, break down into 3 independent sections
        } else if ((section.getWidth() == sectionWidth * 1) && (section.getHeight() == sectionHeight * 3)) {
            System.out.println("Caught 1x3 to breakdown.");
            for (int y = section.getY(); y < section.getY() + 3; y++) {
                for (int x = section.getX(); x < section.getX() + 1; x++) {
                    sectionArray[x][y].breakdown(sectionWidth, sectionHeight);
                }
            }
        //if the section is 1x2, break down into 2 independent sections
        } else if ((section.getWidth() == sectionWidth * 1) && (section.getHeight() == sectionHeight * 2)) {
            System.out.println("Caught 1x2 to breakdown.");
            for (int y = section.getY(); y < section.getY() + 2; y++) {
                for (int x = section.getX(); x < section.getX() + 1; x++) {
                    sectionArray[x][y].breakdown(sectionWidth, sectionHeight);
                }
            }
        //if the section is 1x2, break down into 2 independent sections
        } else if ((section.getWidth() == sectionWidth * 2) && (section.getHeight() == sectionHeight * 1)) {
            System.out.println("Caught 2x1 to breakdown.");
            for (int y = section.getY(); y < section.getY() + 1; y++) {
                for (int x = section.getX(); x < section.getX() + 2; x++) {
                    sectionArray[x][y].breakdown(sectionWidth, sectionHeight);
                }
            }
        //otherwise the unmatched image must be a standard section. If this has failed to match then the generation process must be canceled so an error is thrown.
        } else if ((section.getWidth() == sectionWidth * 1) && (section.getHeight() == sectionHeight * 1)) {
            System.out.println("TOTAL MATCHING FAILURE");
            throw new GenerationException();
        }
    }

    //the binary search narrows down the list of images that must be searched through to find a match
    private static ArrayList<SImage> binarySearch(ArrayList<SImage> imageList, int colour, double targetColour, int sortedListMaxRange) {
        ArrayList<SImage> reducedImageArray = new ArrayList<SImage>();
        int size = imageList.size();
        int low = 0;
        int high = size - 1;
        int middle = 0;
        //the high and low markers move closer together until the index where the perfect match would be
        while (high > low) {
            middle = (low + high) / 2;
            //if the image is a perfect match then add that image and the nearest set of images (size dependant on sortedListMaxRange (5 by default)) to the reduced list
            if (imageList.get(middle).getMeanRGB(colour) == targetColour) {
                for (int index = (middle - sortedListMaxRange < 0 ? 0 : middle - sortedListMaxRange); index <= (middle + sortedListMaxRange > size - 1 ? size - 1 : middle + sortedListMaxRange); index++) {
                    reducedImageArray.add(imageList.get(index));
                }
                //return the finished reduced list
                return reducedImageArray;
            }
            //the following narrow the gap between high and low if a match isn't found by the normal binary search algorithm
            if (imageList.get(middle).getMeanRGB(colour) < targetColour) {
                low = middle + 1;
            }
            if (imageList.get(middle).getMeanRGB(colour) > targetColour) {
                high = middle - 1;
            }
        }
        //if high = low then no perfect match has been found. This does not matter because we need a range of images anyway
        //the image at most recently checked index and nearest set of images (size dependant on sortedListMaxRange) is added to the reduced list
        for (int index = (middle - sortedListMaxRange < 0 ? 0 : middle - sortedListMaxRange); index <= (middle + sortedListMaxRange > size - 1 ? size - 1 : middle + sortedListMaxRange); index++) {
            reducedImageArray.add(imageList.get(index));
        }
        return reducedImageArray;
    }


    //This method takes a ratio and every image in the image Pool, then return only the images of the specified ratio.
    private static ArrayList<SImage> populateList(double ratio, ArrayList<SImage> imagePool) {
        ArrayList<SImage> newList = new ArrayList<SImage>();
        for (SImage image : imagePool) {
            if (image.getRatio() == ratio) {
                newList.add(image);
            }
        }
        return newList;
    }

    //This method takes an input ratio and the 2D Section Array, reformats the Section array into an Arraylist, then finds all sections in the section pool of that ratio.
    //It then returns these images as a new Section ArrayList.
    private static ArrayList<Section> reformatAndPopulateSectionArray(Section[][] sectionList, double ratio) {
        ArrayList<Section> formattedSectionList = new ArrayList<Section>();
        //this adds each section in the 2D array to a list in order of rows then columns
        for (int y = 0; y < sectionList[0].length; y++) {
            for (int x = 0; x < sectionList.length; x++) {
                formattedSectionList.add(sectionList[x][y]);
            }
        }
        ArrayList<Section> newSectionList = new ArrayList<Section>();

        //if the section is not in a compound section or the section marks a compound section it is added to the new list
        for (Section image : formattedSectionList) {
            if ((image.getRatio() == ratio)) {
                if (image.isCompoundSectionMarker() == true || image.isInCompoundSection() == false) {
                    newSectionList.add(image);
                }
            }
        }
        //the reformatted and reduced list is returned
        return newSectionList;
    }

    //the output image is drawn and returned using the images linked to each section
    private static File generateOutput(Section[][] sectionArray, String outputFormat, int generationStyle) {
        BufferedImage outputBufferedImage = processedTemplateFile;
        //every section that is not overwritten by a compound section is considered
        for (Section[] sectionColumn : sectionArray) {
            for (Section section : sectionColumn) {
                if (section.isInCompoundSection() == false || section.isCompoundSectionMarker() == true) {
                    int startX;
                    int startY;
                    SImage linkedSImage = section.getLinkedImage();
                    File imagefile = linkedSImage.file;
                    BufferedImage linkedImage = null;
                    //the linked image is buffered
                    try {
                        linkedImage = ImageIO.read(imagefile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //for the "ordered" style
                    if (generationStyle == 1) {
                        startX = section.getTopLeftX();
                        startY = section.getTopLeftY();
                        //every pixel in a section is given a new RGB value based on the linked image
                        for (int x = 0; x < section.getWidth(); x++) {
                            for (int y = 0; y < section.getHeight(); y++) {
                                //enlargementFactor is used to find the equivalent pixel on the linked image for the pixel being considered in the section
                                double enlargementFactor = section.getHeight() / (float) linkedSImage.getHeight();

                                int imageX = (int) (Math.round(x / enlargementFactor));
                                int imageY = (int) (Math.round(y / enlargementFactor));

                                //the following prevents potential out of bounds errors caused by rounding pixel coordinates
                                if (imageX >= linkedImage.getWidth()) {
                                    imageX = linkedImage.getWidth() - 1;
                                } else if (imageX < 0) {
                                    imageX = 0;
                                }
                                if (imageY >= linkedImage.getHeight()) {
                                    imageY = linkedImage.getHeight() - 1;
                                } else if (imageY < 0) {
                                    imageY = 0;
                                }
                                //the current template file is overwritten with the new pixel
                                outputBufferedImage.setRGB(startX + x, startY + y, linkedImage.getRGB(imageX, imageY));
                                //this process is repeated for every single pixel in the template image
                                //in the most extreme scenario this could mean over 80 billion pixels... hence why it takes a while.
                            }
                        }
                    }
                    // if the generation style is not "ordered" (meaning it is "messy")
                    // this is more difficult because images do not fit neatly into sections
                    else{
                        //this is the ratio of a standard section
                        double standardImageRatio = sectionWidth/(float) sectionHeight;

                        double thisImageMultiple = linkedSImage.getRatio();
                        //this is the ratio of the image linked to the section that is currently being considered
                        double thisImageRatio = linkedSImage.getWidth()/(float) linkedSImage.getHeight();

                        int drawWidth= 0;
                        int drawHeight=0;

                        //if the ratio of the image is smaller than the ratio of this section then the width must fit the section because the height is bigger than it should be
                        //to clarify, this would result in an image the overflows the section boundaries on the y, but fits perfectly on the x
                        if (thisImageRatio < standardImageRatio*thisImageMultiple){
                            drawWidth = section.getWidth();
                            drawHeight = (int) ((drawWidth/(float)linkedSImage.getWidth())*linkedSImage.getHeight())+1;
                        }
                        //if the ratio of the image is greater than the ratio of this section then the height must fit the section because the width is bigger than it should be
                        //this would result in an image the overflows the section boundaries on the x, but fits perfectly on the y
                        else if (thisImageRatio >= standardImageRatio*thisImageMultiple){
                            drawHeight = section.getHeight();
                            drawWidth = (int) ((drawHeight/(float)linkedSImage.getHeight())*linkedSImage.getWidth())+1;
                        }

                        //finding the point from which to draw the image (top left coordinate) based on where its centre is so that it overflows the boundaries of the section as little as possible and evenly on all sides
                        int centreX = section.getCentreX();
                        int centreY = section.getCentreY();
                        startX = centreX- (drawWidth/2);
                        startY = centreY - (drawHeight/2);

                        //similarly to the method for "ordered" every pixel is rewritten based on pixels from the linked image
                        for (int x = 0; x < drawWidth; x++) {
                            for (int y = 0; y < drawHeight; y++) {

                                double enlargementFactor = section.getHeight() / (float) linkedSImage.getHeight();

                                int imageX = (int) (Math.round(x / enlargementFactor));
                                int imageY = (int) (Math.round(y / enlargementFactor));

                                if (imageX >= linkedImage.getWidth()) {
                                    imageX = linkedImage.getWidth() - 1;
                                } else if (imageX < 0) {
                                    imageX = 0;
                                }

                                if (imageY >= linkedImage.getHeight()) {
                                    imageY = linkedImage.getHeight() - 1;
                                } else if (imageY < 0) {
                                    imageY = 0;
                                }

                                if (((startX +x) >=0 && (startY)+y >=0)&&((startX +x) < outputBufferedImage.getWidth() && (startY)+y < outputBufferedImage .getHeight())) {
                                    outputBufferedImage.setRGB(startX + x, startY + y, linkedImage.getRGB(imageX, imageY));
                                }
                            }
                        }
                        }

                }
            }
            System.out.println("+1 section lot drawn.");

        }
        //the final output image is written to a file
        File outputFile = new File("outputImage." + outputFormat);
        try {
            System.out.print("Writing...");
            ImageIO.write(outputBufferedImage, outputFormat, outputFile);
        } catch (IOException e) {
            System.out.print("eh?!");
        }
        //generating is set to false so that another generation can be started
        generating=false;
        return outputFile;
    }

    //This exception is thrown if there is an issue at any point during the generation phase which would force the termination of the generation process
    //it returns to the default listening state in Controller
    public static class GenerationException extends Exception {
    }

    //These two functions exist to gather data to output to the user in the output window
    public static int getNoSections(){
        //the total number of images used can be calculated from the size of the final section list
        int count = 0;
        for (Section[] sectionArray : finalSectionList ){
            for (Section section : sectionArray){
                if (section.isCompoundSectionMarker() == true || section.isInCompoundSection() == false){
                    count +=1;
                }
            }
        }
        return count;
    }
    public static int getNoUniqueImages(){
        //usedImages is a HashSet so cannot contain duplicates. Therefore it's size shows the number of unique images used
        return usedImages.size();
    }
}




