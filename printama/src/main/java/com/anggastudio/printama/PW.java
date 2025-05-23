package com.anggastudio.printama;

/**
 * Printama Width Constants
 * Use these constants to set the width of image that will be printed
 * The width is based on the printer printing area
 * It will calculate the width whether you choose 2 inches printer or 3 inches printer
 */
public class PW {
    /**
     * Prints the image using the original width of the image/bitmap
     * will not calculated anything, will just get the width using Bitmap.getWidth()
     */
    public static final int ORIGINAL_WIDTH = 0;
    /**
     * Prints the image using the full width of the printer paper
     * For 2-inch printer: approximately 384px
     * For 3-inch printer: approximately 576px
     */
    public static final int FULL_WIDTH = -1;
    
    /**
     * Prints the image using half of the printer paper width
     * For 2-inch printer: approximately 192px
     * For 3-inch printer: approximately 288px
     */
    public static final int HALF_WIDTH = -2;
    
    /**
     * Prints the image using one-third of the printer paper width
     * For 2-inch printer: approximately 128px
     * For 3-inch printer: approximately 192px
     */
    public static final int THIRD_WIDTH = -3;
    
    /**
     * Prints the image using one-fourth of the printer paper width
     * For 2-inch printer: approximately 96px
     * For 3-inch printer: approximately 144px
     */
    public static final int QUARTER_WIDTH = -4;
}
