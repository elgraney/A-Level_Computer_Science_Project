package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Matthew on 04/02/2017.
 */


public final class MergeSort {

    public static ArrayList<SImage> mergeSort(ArrayList<SImage> completeImageList, int colour) {
        ArrayList<SImage> left = new ArrayList<SImage>();
        ArrayList<SImage> right = new ArrayList<SImage>();
        int center;

        if (completeImageList.size() == 1) {
            return completeImageList;
        } else {
            center = completeImageList.size()/2;
            for (int index=0; index<center; index++) {
                left.add(completeImageList.get(index));
            }
            for (int index=center; index<completeImageList.size(); index++) {
                right.add(completeImageList.get(index));
            }
            left  = mergeSort(left, colour);
            right = mergeSort(right, colour);

            merge(left, right, completeImageList, colour);
        }
        return completeImageList;
    }

    private static void merge(ArrayList<SImage> left, ArrayList<SImage> right, ArrayList<SImage> completeImageList, int colour) {
        int leftIndex = 0;
        int rightIndex = 0;
        int completeImageListIndex = 0;

        while (leftIndex < left.size() && rightIndex < right.size()) {
            if ( (left.get(leftIndex).getMeanRGB(colour).compareTo(right.get(rightIndex).getMeanRGB(colour))) < 0) {
                completeImageList.set(completeImageListIndex, left.get(leftIndex));
                leftIndex++;
            } else {
                completeImageList.set(completeImageListIndex, right.get(rightIndex));
                rightIndex++;
            }
            completeImageListIndex++;
        }
        ArrayList<SImage> rest;
        int restIndex;
        if (leftIndex >= left.size()) {
            rest = right;
            restIndex = rightIndex;
        } else {
            rest = left;
            restIndex = leftIndex;
        }

        for (int i=restIndex; i<rest.size(); i++) {
            completeImageList.set(completeImageListIndex, rest.get(i));
            completeImageListIndex++;
        }
    }

}



