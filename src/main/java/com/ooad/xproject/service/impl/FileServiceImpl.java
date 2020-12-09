package com.ooad.xproject.service.impl;

import com.ooad.xproject.service.FileService;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public ResponseEntity<byte[]> download(HttpServletRequest request, String realPath, String userAgent, String filename, boolean inline) {
        File file = new File(realPath);
        // build response
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.ok();
        bodyBuilder.contentLength(file.length());
        // bitstream
        bodyBuilder.contentType(MediaType.APPLICATION_OCTET_STREAM);
        // encode filename
        try {
            String encodeFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            // IE
            if (userAgent.indexOf("MSIE") > 0) {
                bodyBuilder.header("Content-Disposition", "attachment;filename=" + encodeFileName);
            } else if (inline) {
                // open in explorer
                URL url = new URL("file:///" + file);
                bodyBuilder.header("Content-Type", url.openConnection().getContentType());
                bodyBuilder.header("Content-Disposition", "inline;filename*=UTF-8''" + encodeFileName);
            } else {
                // download directly
                bodyBuilder.header("Content-Disposition", "attachment;filename*=UTF-8''" + encodeFileName);
            }
            // download successful
            return bodyBuilder.body(FileUtils.readFileToByteArray(file));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return Error request
        return (ResponseEntity<byte[]>) ResponseEntity.badRequest();
    }

    @Override
    public String upload(MultipartFile[] files, String uploadPath) {
        String filePath = "";
        for (MultipartFile file : files) {
            // get filename
            String originalFilename = file.getOriginalFilename();
            // rename to time + filename
            filePath = uploadPath +
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

    @Override
    public String upload(MultipartFile file, String uploadPath, String fileName) {
        String filePath = uploadPath + fileName;
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }
}
