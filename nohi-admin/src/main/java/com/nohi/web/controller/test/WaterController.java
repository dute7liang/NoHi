package com.nohi.web.controller.test;

import com.nohi.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RequestMapping("/test/watermark")
@RestController
public class WaterController {

    @GetMapping("/test")
    public R<String> watermark(String filePath){
        File sourceFile = new File(filePath);
        byte[] bytes = FileUtils.fileToByte(sourceFile);
        File fasdaaaa = WatermarkUtil.watermarkVideo(bytes, "FASDAAAA");
        return R.ok(fasdaaaa.getAbsolutePath());
    }
}
