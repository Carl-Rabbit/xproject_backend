package com.ooad.xproject.controller;

import com.ooad.xproject.config.FileConfig;
import com.ooad.xproject.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


// todo: role + database -> directory name -> upload/download
@RestController
public class FileController {

    private final FileConfig fileConfig;

    private final FileService fileService;

    public FileController(FileConfig fileConfig, FileService fileService) {
        this.fileConfig = fileConfig;
        this.fileService = fileService;
    }

    @PostMapping("api/upload")
    public String fileUpload(@RequestParam("file") MultipartFile[] files) {
        return fileService.upload(files, fileConfig.getUploadRoot());
    }


    @GetMapping("api/download")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, @RequestParam("path") String path
            , @RequestHeader("user-agent") String userAgent, @RequestParam("filename") String filename
            , @RequestParam(required = false, defaultValue = "false") boolean inline) {

        String realPath = fileConfig.getDownloadRoot() + path;
        return fileService.download(request, realPath, userAgent, filename, inline);
    }
}
