package com.longpeng.jail.controller;

import com.cq1080.rest.API;
import com.cq1080.rest.APIError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.longpeng.jail.service.SmsService;
import com.longpeng.jail.util.UploadFileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@Api(tags = "项目其他接口")
public class TestController {

    private UploadFileUtil uploadFileUtil;
    private SmsService smsService;

    @Autowired
    public TestController(UploadFileUtil uploadFileUtil, SmsService smsService) {
        this.uploadFileUtil = uploadFileUtil;
        this.smsService = smsService;
    }

    @PostMapping("other/upload")
    @ApiOperation("上传")
    public API<String> uploadFile(@RequestPart("file")MultipartFile file) throws IOException {
        long size = file.getSize();
        if(size>1024*1024){
            APIError.e(400,"图片不能大于1M");
        }
        String s = uploadFileUtil.saveFile(file);
        return API.ok(s);
    }

    @GetMapping("test1")
    public API<String> test1() throws JsonProcessingException, UnsupportedEncodingException {
        smsService.send("15526743170","测试");
        return API.ok("测试短信");
    }

}
