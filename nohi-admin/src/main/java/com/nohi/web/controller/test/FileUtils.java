package com.nohi.web.controller.test;

import java.io.*;

/**
 * 文件工具类
 * <p>created on 2022/6/16 11:54</p>
 * @author ZL
 */
public class FileUtils {

    public static byte[] fileToByte(File file){
        try (InputStream in = new FileInputStream(file)){
            byte[] data = new byte[in.available()];
            in.read(data);
            return data;
        } catch (IOException e) {
            throw new RuntimeException("文件转换字节失败",e);
        }
    }

    public static void byteToFile(byte[] bytes,File file){
        try (OutputStream outputStream = new FileOutputStream(file)){
            outputStream.write(bytes);
        }catch (IOException e){
            throw new RuntimeException("字节写入文件失败!",e);
        }

    }

}
