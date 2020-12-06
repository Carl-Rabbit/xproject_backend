package com.ooad.xproject.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class FileConfig {

    @Value("${ooad.file.path.upload-root}")
    private String uploadRoot;

    @Value("${ooad.file.path.download-root}")
    private String downloadRoot;

    @Value("${ooad.file.path.output-root}")
    private String outputRoot;
}
