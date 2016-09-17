package com.example.jack.myapplication.Util.Palette;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 取色器
 */

public class Palette {
    public static class Result {
        private int mBackgroundColor;
        private int mContentColor;

        public Result(int backgroundColor, int contentColor) {
            mBackgroundColor = backgroundColor;
            mContentColor = contentColor;
        }

        public int getBackgroundColor() {
            return mBackgroundColor;
        }

        public int getContentColor() {
            return mContentColor;
        }
    }

    public static Result extract(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        List<Integer> topBorderColors
                = getColorsInRange(bitmap, 0, 0, width - 1, 1);
        List<Integer> rightBorderColors
                = getColorsInRange(bitmap, width - 1, 0, 1, height - 1);
        List<Integer> leftBorderColors
                = getColorsInRange(bitmap, 0, 1, 1, height - 1);
        List<Integer> bottomBorderColors
                = getColorsInRange(bitmap, 1, height - 1, width - 1, 1);
        List<Integer> borderColors = new ArrayList<>();
        borderColors.addAll(topBorderColors);
        borderColors.addAll(rightBorderColors);
        borderColors.addAll(leftBorderColors);
        borderColors.addAll(bottomBorderColors);
        int backgroundColor = getDominantColors(borderColors).get(0);

        int contentColor = 0;
        List<Integer> contentColors
                = getColorsInRange(bitmap, 0, 0, width, height);
        contentColors = getDominantColors(contentColors);
        for (int color : contentColors) {
            if (isDifferentColors(backgroundColor, color)) {
                contentColor = color;
                break;
            }
        }

        if (contentColor == 0) {
            if (isDifferentColors(backgroundColor, Color.WHITE)) {
                return new Result(backgroundColor, Color.WHITE);
            } else {
                return new Result(backgroundColor, Color.BLACK);
            }
        }

        return new Result(backgroundColor, contentColor);
    }

    private static List<Integer> getColorsInRange(Bitmap bitmap,
                                                  int x, int y, int width, int height) {
        List<Integer> list = new ArrayList<>();

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                list.add(bitmap.getPixel(i, j));
            }
        }

        return list;
    }

    private static List<Integer> getDominantColors(List<Integer> list) {
        List<List<Integer>> buckets = getSimilarColors(list, 25.5F);
        Collections.sort(buckets, new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> bucket1,
                               List<Integer> bucket2) {
                return bucket2.size() - bucket1.size();
            }
        });

        List<Integer> dominant = new ArrayList<>();
        for (List<Integer> bucket : buckets) {
            dominant.add(getMeanColor(bucket));
        }

        return dominant;
    }

    private static List<List<Integer>> getSimilarColors(
            List<Integer> list, float compareDistance) {
        List<List<Integer>> subsets = new ArrayList<>();

        for (int color : list) {
            List<Integer> closest;
            int index;

            for (index = 0; index < subsets.size(); index++) {
                if (getColorsDistance(subsets.get(index).get(0), color)
                        < compareDistance) {
                    break;
                }
            }

            if (index >= subsets.size()) {
                closest = new ArrayList<>();
                subsets.add(closest);
            } else {
                closest = subsets.get(index);
            }

            closest.add(color);
        }

        return subsets;
    }

    private static float getColorsDistance(int color1, int color2) {
        float[] yue1 = rgb2Yuv(getColorRgb(color1));
        float[] yue2 = rgb2Yuv(getColorRgb(color2));
        return (float) Math.sqrt(Math.pow(yue1[0] - yue2[0], 2.0F)
                + Math.pow(yue1[1] - yue2[1], 2.0F)
                + Math.pow(yue1[2] - yue2[2], 2.0F));
    }

    private static float[] getColorRgb(int color) {
        float[] rgb = new float[3];
        rgb[0] = Color.red(color);
        rgb[1] = Color.green(color);
        rgb[2] = Color.blue(color);
        return rgb;
    }

    private static float[] rgb2Yuv(float[] rgb) {
        float[] yuv = new float[3];
        yuv[0] = rgb[0] * 0.299F + rgb[1] * 0.587F + rgb[2] * 0.114F;
        yuv[1] = rgb[0] * -0.169F + rgb[1] * -0.331F + rgb[2] * 0.500F
                + 128.0F;
        yuv[2] = rgb[0] * 0.500F + rgb[1] * -0.419F + rgb[2] * -0.081F
                + 128.0F;
        return yuv;
    }

    private static int getMeanColor(List<Integer> list) {
        float[] mean = new float[3];

        for (int color : list) {
            float[] rgb = getColorRgb(color);
            mean[0] += rgb[0];
            mean[1] += rgb[1];
            mean[2] += rgb[2];
        }

        mean[0] /= list.size();
        mean[1] /= list.size();
        mean[2] /= list.size();

        return Color.rgb((int) mean[0], (int) mean[1], (int) mean[2]);
    }

    private static boolean isDifferentColors(int color1, int color2) {
        float[] rgb1 = getColorRgb(color1);
        float[] rgb2 = getColorRgb(color2);
        float rgbDiff = Math.abs(rgb1[0] - rgb2[0])
                + Math.abs(rgb1[1] - rgb2[1])
                + Math.abs(rgb1[2] - rgb2[2]);
        float brightnessDiff = Math.abs(getColorBrightness(color1)
                - getColorBrightness(color2));
        return rgbDiff >= 500.0F && brightnessDiff >= 125.0F;
    }

    private static float getColorBrightness(int color) {
        float[] rgb = getColorRgb(color);
        return rgb[0] * 0.299F + rgb[1] * 0.587F + rgb[2] * 0.114F;
    }
}
