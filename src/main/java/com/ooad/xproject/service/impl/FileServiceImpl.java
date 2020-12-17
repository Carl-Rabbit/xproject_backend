package com.ooad.xproject.service.impl;

import com.ooad.xproject.config.FileConfig;
import com.ooad.xproject.entity.*;
import com.ooad.xproject.mapper.ProjectInstMapper;
import com.ooad.xproject.mapper.ProjectMapper;
import com.ooad.xproject.mapper.SchoolMapper;
import com.ooad.xproject.mapper.SubmissionMapper;
import com.ooad.xproject.service.FileService;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
    private final ProjectMapper projectMapper;

    private final ProjectInstMapper projectInstMapper;

    private final SubmissionMapper submissionMapper;

    private final SchoolMapper schoolMapper;

    private final FileConfig fileConfig;

    public FileServiceImpl(ProjectMapper projectMapper, ProjectInstMapper projectInstMapper, SubmissionMapper submissionMapper, SchoolMapper schoolMapper, FileConfig fileConfig) {
        this.projectMapper = projectMapper;
        this.projectInstMapper = projectInstMapper;
        this.submissionMapper = submissionMapper;
        this.schoolMapper = schoolMapper;
        this.fileConfig = fileConfig;
    }

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
            filePath = uploadPath + "\\" + originalFilename;
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

    private String getStorePath(School school, Project project, Submission submission, ProjectInst projectInst) {
        String dir = "\\school-" + school.getSchId() + "-" + school.getSchName() +
                "\\project-" + project.getProjId() + "-" + project.getProjName() +
                "\\submission-" + submission.getSbmId() + "-" + submission.getTitle() +
                "\\team-" + projectInst.getProjInstId() + "-" + projectInst.getTeamName();
        return dir;
    }

    @Override
    public void deleteFolder(File folder) {
        if (!folder.exists()) {
            return;
        }
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
    }

    public File getOrCreateStudentDir(SubmissionInst submissionInst) {
        ProjectInst projectInst = projectInstMapper.selectByPrimaryKey(submissionInst.getProjInstId());
        Submission submission = submissionMapper.selectByPrimaryKey(submissionInst.getSbmId());
        Project project = projectMapper.selectByPrimaryKey(submission.getProjId());
        School school = schoolMapper.selectByPrimaryKey(project.getSchId());
        String studentDir = getStorePath(school, project, submission, projectInst);
        String filePath = fileConfig.getUploadRoot() + "\\" + studentDir;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        System.out.println(file.getPath() + "; " +  file.exists());
        return file;
    }

}
