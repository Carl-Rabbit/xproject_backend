package com.ooad.xproject.controller;

import com.ooad.xproject.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


// todo: role + database -> directory name -> upload/download
@RestController
public class FileController {


    private final FileService fileService;

    @Value("${ooad.file.path.update}")
    private String uploadPath;

    @Value("${ooad.file.path.download}")
    private String downloadPath;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("api/upload")
    public String fileUpload(@RequestParam("file") MultipartFile[] files) {

        return fileService.upload(files, uploadPath);
    }


    @GetMapping("api/download")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, @RequestParam("path") String path
            , @RequestHeader("user-agent") String userAgent, @RequestParam("filename") String filename
            , @RequestParam(required = false, defaultValue = "false") boolean inline) {

        String realPath = downloadPath + path;
        return fileService.download(request, realPath, userAgent, filename, inline);
    }
}
