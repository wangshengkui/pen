package com.example.pencon;

public class Constants {

    //(本子铺码) A5纸规格尺寸：148mm × 210mm
    private static final int PAPER_WIDTH = 148;
    private static final int PAPER_HEIGHT = 210;

    //(本子铺码) OID4码点规格：1.524mm × 1.524mm
    public static final double XDIST_PERUNIT = 1.524;
    public static final double YDIST_PERUNIT = 1.524;

    //(本子铺码) 一英寸里大小 1in=2.54cm=25.40mm
    public static final float IN_SIZE = 25.40f;
    //(本子铺码) 一英寸里的像素
    public static final int IN_PIXEL = 600;

    //铺码生成的背景图片原始像素：4300 x 6048
   // public static final int A5_ORIGINAL_IMAGE_WIDTH = 4300;//
  //  public static final int A5_ORIGINAL_IMAGE_HEIGHT = 6048;
    //public static final int A5_ORIGINAL_IMAGE_WIDTH = 6048*2;
   // public static final int A5_ORIGINAL_IMAGE_HEIGHT = 8600*2;//A3的
    
    public static final int A5_ORIGINAL_IMAGE_WIDTH = 4300;//A2的
    public static final int A5_ORIGINAL_IMAGE_HEIGHT = 6048;
    
    public static final int A4_ORIGINAL_IMAGE_WIDTH = 7020;
    public static final int A4_ORIGINAL_IMAGE_HEIGHT = 9936;
    
    public static final int A0_ORIGINAL_IMAGE_WIDTH = 4960*4;//A2的
    public static final int A0_ORIGINAL_IMAGE_HEIGHT = 7015*4;
    //书写本子实际大小
    public static final double A5_WIDTH = (((double) A5_ORIGINAL_IMAGE_WIDTH) / IN_PIXEL) * IN_SIZE;
    public static final double A5_HEIGHT = (((double) A5_ORIGINAL_IMAGE_HEIGHT) / IN_PIXEL) * IN_SIZE;
    public static final double A4_WIDTH=((double)A4_ORIGINAL_IMAGE_WIDTH/ IN_PIXEL)*IN_SIZE;
    public static final double A4_HEIGHT = (((double) A4_ORIGINAL_IMAGE_HEIGHT) / IN_PIXEL) * IN_SIZE; 
    
    public static final double A0_WIDTH=((double)A0_ORIGINAL_IMAGE_WIDTH/ IN_PIXEL)*IN_SIZE;
    public static final double A0_HEIGHT = (((double) A0_ORIGINAL_IMAGE_HEIGHT) / IN_PIXEL) * IN_SIZE;    
    //原始背景图片过大，等比例缩小图片宽高得到合适大小背景图片（减少内存开销），放到app中作为背景图片
    public static int A5_BG_REAL_WIDTH = A5_ORIGINAL_IMAGE_WIDTH / 4;
    public static int A5_BG_REAL_HEIGHT = A5_ORIGINAL_IMAGE_HEIGHT / 4;
    public static int A4_BG_REAL_WIDTH = A4_ORIGINAL_IMAGE_WIDTH / 4;
    public static int A4_BG_REAL_HEIGHT = A4_ORIGINAL_IMAGE_HEIGHT / 4;
    
    public static int A0_BG_REAL_WIDTH = A0_ORIGINAL_IMAGE_WIDTH / 4;
    public static int A0_BG_REAL_HEIGHT = A0_ORIGINAL_IMAGE_HEIGHT / 4;
}
