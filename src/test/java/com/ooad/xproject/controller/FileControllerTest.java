package com.ooad.xproject.controller;

import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.config.FileConfig;
import com.ooad.xproject.entity.*;
import com.ooad.xproject.service.FileService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


@RunWith(SpringRunner.class)
@SpringBootTest
class FileControllerTest {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileConfig fileConfig;

    @Test
    void uploadToDir() {
        SubmissionInst submissionInst = new SubmissionInst();
        submissionInst.setSbmId(1);
        submissionInst.setProjInstId(9);
        MultipartFile[] files = new MultipartFile[1];

        File file = fileService.getOrCreateStudentDir(submissionInst);

        fileService.deleteFilesOfFolder(file);
//        fileService.upload(files, file.getPath());
    }

    @Test
    void getAllSbmFiles() {
        int sbmId = 1;
        File file = fileService.getSbmDir(sbmId);
        String realPath = "C:\\BCSpace\\JetProjects\\JavaProject\\xproject_backend\\business\\output" + "\\" + "output.zip";
        SvResult<String> svResult = fileService.compressDir(file, realPath);
//        return fileService.download(request, realPath, userAgent, filename, inline);
    }
}