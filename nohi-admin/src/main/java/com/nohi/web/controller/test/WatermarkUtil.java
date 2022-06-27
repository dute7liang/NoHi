package com.nohi.web.controller.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StopWatch;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 水印工具类
 * <p>created on 2022/5/23 18:50</p>
 * @author ZL
 */
@Slf4j
public class WatermarkUtil {

    /**
     * 以下参数为水印默认值参数
     */
    private final static float ALPHA = 0.8f;
    private final static int FONT_SIZE = 18;
    private final static Font FONT = new Font("宋体", Font.PLAIN, FONT_SIZE);
    private final static Color COLOR = Color.gray;
    private final static int X_MOVE = 100;
    private final static int Y_MOVE = 100;

    private final static int PICTURE_WIDTH = 800;
    private final static int PICTURE_HEIGHT = 600;

    private final static double INTERVAL = 1.5;

    private final static String TEMP_PATH = "/temp";



    public static String getTempFile() throws FileNotFoundException {
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if (!path.exists()) {
            path = new File("");
        }
        File tempLocaltion = new File(path.getAbsolutePath() + TEMP_PATH);
        if (!tempLocaltion.exists()) {
            tempLocaltion.mkdirs();
        }
        return tempLocaltion.getAbsolutePath();
    }

    private static File getTempFileMp4() throws IOException {
        File rootFile = new File(getTempFile());
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        File dateFile = new File(rootFile,currentDate);
        if(!dateFile.exists()){
            dateFile.mkdirs();
        }
        String fileName = RandomStringUtils.randomAlphanumeric(10) + ".mp4";
        return new File(dateFile,fileName);
    }

    /**
     * 视频添加水印
     * @param video
     * @param text
     * @return
     */
    public static File watermarkVideo(byte[] video,String text){
        try (InputStream input = new ByteArrayInputStream(video)){
            FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(input);
            File mp4 = getTempFileMp4();
            StopWatch stopWatch = new StopWatch("watermark");
            stopWatch.start("watermark");
            log.info("水印处理: 对视频添加水印 file={}",mp4.getAbsolutePath());
            watermarkVideo(frameGrabber,mp4,text,FONT,COLOR,ALPHA, X_MOVE, Y_MOVE);
            stopWatch.stop();
            log.info("水印处理: 水印结束 耗时={}, file={}",stopWatch.prettyPrint(),mp4.getAbsolutePath());
            return mp4;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 图片添加水印
     * @param image
     * @param text
     * @return
     */
    public static byte[] watermarkImage(byte[] image,String text){
        StopWatch stopWatch = new StopWatch("watermark");
        stopWatch.start("watermark");
        log.info("水印处理: 对图片添加水印");
        try (InputStream input = new ByteArrayInputStream(image)){
            BufferedImage read = ImageIO.read(input);
            byte[] bytes = watermarkImage(read, text, FONT, COLOR, ALPHA, X_MOVE, Y_MOVE);
            stopWatch.stop();
            log.info("水印处理: 水印结束 耗时={}",stopWatch.prettyPrint());
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 视频添加文字水印
     * @param file 源文件
     * @param targetFile 加水印后保存地址
     * @param text 水印内容
     */
    public static void watermarkVideo(File file,File targetFile,String text){
        try {
            FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file);
            watermarkVideo(frameGrabber,targetFile,text,FONT,COLOR,ALPHA, X_MOVE, Y_MOVE);
        } catch (FrameGrabber.Exception | FFmpegFrameRecorder.Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加图片水印
     * 开发中，请勿使用
     * @param sourceFile 源文件
     * @param markImgFile 水印图片
     */
    @Deprecated
    public static void watermarkVideoPicture(File sourceFile,File markImgFile){
        try {
            String targetFileName = sourceFile.getAbsolutePath() + RandomStringUtils.randomNumeric(5)+".mp4";
            FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(sourceFile);
            Frame frame;
            frameGrabber.start();
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(targetFileName, frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), frameGrabber.getAudioChannels());
            recorder.setSampleRate(frameGrabber.getSampleRate());
            recorder.setFrameRate(frameGrabber.getFrameRate());
            recorder.setTimestamp(frameGrabber.getTimestamp());
            recorder.setVideoBitrate(frameGrabber.getVideoBitrate());
            recorder.setVideoCodec(frameGrabber.getVideoCodec());
            recorder.start();
            BufferedImage markImg = ImageIO.read(markImgFile);
            try {
                while (true){
                    frame=frameGrabber.grabFrame();
                    if(frame==null){
                        break;
                    }
                    //判断图片帧
                    if(frame.image !=null){
                        IplImage iplImage = Java2DFrameUtils.toIplImage(frame);
                        BufferedImage buffImg=Java2DFrameUtils.toBufferedImage(iplImage);
                        Graphics2D graphics = buffImg.createGraphics();
                        graphics.drawImage(markImg, 0, 0, buffImg.getWidth(), buffImg.getHeight(), null);
                        graphics.dispose();
                        Frame newFrame = Java2DFrameUtils.toFrame(buffImg);
                        recorder.record(newFrame);
                        newFrame.close();
                    }
                    frame.close();
                }
            }finally {
                recorder.stop();
                recorder.release();
                frameGrabber.stop();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    /**
     * 生成水印图片 可以用于直接覆盖到视频上面 生成水印
     * @param text 水印文字
     * @param targetFile 目标文件
     */
    public static void genWatermarkPicture(String text,File targetFile) throws IOException {
        BufferedImage image = new BufferedImage(PICTURE_WIDTH, PICTURE_HEIGHT, BufferedImage.TYPE_INT_BGR);
        Graphics2D g = image.createGraphics();
        g.getDeviceConfiguration().createCompatibleImage(PICTURE_WIDTH, PICTURE_HEIGHT, Transparency.TRANSLUCENT);
        g.dispose();
        g = image.createGraphics();
        // 设置对线段的锯齿状边缘处理
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setClip(0, 0, PICTURE_WIDTH, PICTURE_HEIGHT);
        g.fillRect(0, 0, PICTURE_WIDTH, PICTURE_HEIGHT);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, ALPHA));
        g.setColor(Color.black);
        g.setFont(FONT);
        g.rotate(Math.toRadians(-40),(double)(image.getWidth()/2)-100, (double)image.getHeight()/2);
        int x = -PICTURE_WIDTH / 2;
        int y;
        int markWidth = FONT_SIZE * getTextLength(text);
        while (x < PICTURE_WIDTH * INTERVAL) {
            y = -PICTURE_HEIGHT / 2;
            while (y < PICTURE_HEIGHT * INTERVAL) {
                g.drawString(text, x, y);
                y += FONT_SIZE + Y_MOVE;
            }
            x += markWidth + X_MOVE;
        }
        g.dispose();
        ImageIO.write(image, "jpg",targetFile);
    }

    /**
     * 加文字 水印
     * @param frameGrabber 源文件
     * @param targetFileName 加水印后文件保存地址
     * @param waterText 文字水印
     * @param font 字体
     * @param fontColor 字体颜色
     * @param alpha 水印透明度  null 表示不加透明度取值 0-1
     * @param xMove 水印x间隔
     * @param yMove 水印y间隔
     * @throws FrameGrabber.Exception
     * @throws FFmpegFrameRecorder.Exception
     */
    private static void watermarkVideo(FFmpegFrameGrabber frameGrabber,File targetFileName,
                                       String waterText, Font font,Color fontColor,
                                       Float alpha, int xMove,int yMove) throws FrameGrabber.Exception, FFmpegFrameRecorder.Exception {
        int fontSize = font.getSize();
        Frame frame;
        FFmpegFrameRecorder recorder;
        Graphics2D graphics;
        BufferedImage buffImg;
        IplImage iplImage;
        frameGrabber.start();
        recorder = new FFmpegFrameRecorder(targetFileName, frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), frameGrabber.getAudioChannels());
        recorder.setSampleRate(frameGrabber.getSampleRate());
        recorder.setFrameRate(frameGrabber.getFrameRate());
        recorder.setTimestamp(frameGrabber.getTimestamp());
        recorder.setVideoBitrate(frameGrabber.getVideoBitrate());
        recorder.setVideoCodec(frameGrabber.getVideoCodec());
        recorder.start();
        try {
            while (true){
                frame=frameGrabber.grabFrame();
                if(frame==null){
                    break;
                }
                //判断图片帧
                if(frame.image !=null){
                    iplImage = Java2DFrameUtils.toIplImage(frame);
                    int width = iplImage.width();
                    int height = iplImage.height();
                    buffImg = Java2DFrameUtils.toBufferedImage(iplImage);
                    iplImage.close();
                    graphics = buffImg.createGraphics();
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    graphics.setColor(fontColor);
                    graphics.setFont(font);
                    graphics.rotate(Math.toRadians(-40),(double)(buffImg.getWidth()/2)-100, (double)buffImg.getHeight()/2);
                    if(alpha != null){
                        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
                    }
                    int x = -width / 2;
                    int y;
                    int markWidth = fontSize * getTextLength(waterText);
                    while (x < width * INTERVAL) {
                        y = -height / 2;
                        while (y < height * INTERVAL) {
                            graphics.drawString(waterText, x, y);
                            y += fontSize + yMove;
                        }
                        x += markWidth + xMove;
                    }
                    graphics.dispose();
                    Frame newFrame = Java2DFrameUtils.toFrame(buffImg);
                    recorder.record(newFrame);
                    newFrame.close();
                    buffImg.flush();
                }
                frame.close();
            }
        }finally {
            recorder.stop();
            recorder.release();
            frameGrabber.stop();
        }
    }

    /**
     * 图片加水印
     * @param srcImg 源文件
     * @param waterText 文字水印
     * @param font 字体
     * @param fontColor 字体颜色
     * @param alpha 水印透明度  null 表示不加透明度取值 0-1
     * @param xMove 水印x间隔
     * @param yMove 水印y间隔
     * @throws FrameGrabber.Exception
     * @throws FFmpegFrameRecorder.Exception
     */
    private static byte[] watermarkImage(Image srcImg,
                                       String waterText, Font font,Color fontColor,
                                       Float alpha, int xMove,int yMove) throws IOException {
        int fontSize = font.getSize();
        int width = srcImg.getWidth(null);
        int height = srcImg.getHeight(null);
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = buffImg.createGraphics();
//        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        graphics.drawImage(srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        graphics.drawImage(srcImg, 0, 0, width, height, null);
        graphics.setColor(fontColor);
        graphics.setFont(font);
        graphics.rotate(Math.toRadians(-40),(double)(buffImg.getWidth()/2)-100, (double)buffImg.getHeight()/2);
        if(alpha != null){
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        }
        int x = -width / 2;
        int y;
        int markWidth = fontSize * getTextLength(waterText);
        while (x < width * INTERVAL) {
            y = -height / 2;
            while (y < height * INTERVAL) {
                graphics.drawString(waterText, x, y);
                y += fontSize + yMove;
            }
            x += markWidth + xMove;
        }
        graphics.dispose();
        ByteArrayOutputStream outImgStream = new ByteArrayOutputStream();
        ImageIO.write(buffImg, "png", outImgStream);
        byte[] bytes = outImgStream.toByteArray();
        outImgStream.close();
        return bytes;
    }


    /**
     * 获取文本长度。汉字为1:1，英文和数字为2:1
     */
    private static int getTextLength(String text) {
        int length = text.length();
        for (int i = 0; i < text.length(); i++) {
            String s = String.valueOf(text.charAt(i));
            if (s.getBytes().length > 1) {
                length++;
            }
        }
        length = length % 2 == 0 ? length / 2 : length / 2 + 1;
        return length;
    }

}
