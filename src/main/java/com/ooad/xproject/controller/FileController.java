package com.ooad.xproject.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


// todo: role + database -> directory name -> upload/download
@RestController
public class FileController {

    @Value("${ooad.file.root.path}")
    private String fileRootPath;

    @Value("${ooad.file.root.path}")
    private String downloadPath;

    @PostMapping("api/upload")
    public String fileUpload(@RequestParam("file") MultipartFile[] files) {
        String filePath = "";
        for (MultipartFile file : files) {
            // get filename
            String originalFilename = file.getOriginalFilename();
            // rename to time + filename
            filePath = fileRootPath +
                    System.currentTimeMillis() +
                    originalFilename;
            try {
                // store the file
                file.transferTo(new File(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }


    @GetMapping("api/download")
    public ResponseEntity<byte[]> downlaodFile(HttpServletRequest request, @RequestParam("path") String path
            , @RequestHeader("user-agent") String userAgent, @RequestParam("filename") String filename
            , @RequestParam(required = false, defaultValue = "false") boolean inline) {
        // 根路径加上传参数的路径构成文件路径地址
        String realPath = downloadPath + path;
        File file = new File(realPath);
        // 构建响应
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.ok();
        bodyBuilder.contentLength(file.length());
        // 二进制数据流
        bodyBuilder.contentType(MediaType.APPLICATION_OCTET_STREAM);
        // 文件名编码
        try {
            String encodeFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            // IE
            if (userAgent.indexOf("MSIE") > 0) {
                bodyBuilder.header("Content-Disposition", "attachment;filename=" + encodeFileName);
            } else {
                // 其他浏览器
                if (inline) {
                    // 在浏览器中打开
                    URL url = new URL("file:///" + file);
                    bodyBuilder.header("Content-Type", url.openConnection().getContentType());
                    bodyBuilder.header("Content-Disposition", "inline;filename*=UTF-8''" + encodeFileName);
                } else {
                    // 直接下载
                    bodyBuilder.header("Content-Disposition", "attachment;filename*=UTF-8''" + encodeFileName);
                }

            }
            // 下载成功返回二进制流
            return bodyBuilder.body(FileUtils.readFileToByteArray(file));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 下载失败直接返回错误的请求
        return (ResponseEntity<byte[]>) ResponseEntity.badRequest();

    }
}
