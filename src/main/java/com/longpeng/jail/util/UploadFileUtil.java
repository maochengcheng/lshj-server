package com.longpeng.jail.util;

import com.longpeng.jail.config.ProjectConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class UploadFileUtil {

    private ProjectConfig projectConfig;

    @Autowired
    public void setProjectConfig(ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    public  String saveFile(MultipartFile file) throws IOException {
        //项目根目录
        File upload = new File(projectConfig.getUploadPath(),"images/");
        if(!upload.exists()){
            upload.mkdirs();
        }
        String path= upload.toString();
        //文件名
        String name=file.getOriginalFilename();
        long size=file.getSize();
        if (size>52428800L){
            //设置文件大小
            return "文件太大";
        }
        //文件后缀，识别文件类型
        String subffix = name.substring(name.lastIndexOf(".") + 1, name.length());
        //文件重命名 防止文件重名
        String fileName=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"."+subffix;
        //文件存储路径，将保存路径以 年 / 月 / 日 / 的格式命名
        StringBuilder realPath = new StringBuilder(path);
        Calendar now = Calendar.getInstance();
        String str=File.separator + now.get(Calendar.YEAR) + File.separator + (now.get(Calendar.MONTH)+1) + File.separator + now.get(Calendar.DAY_OF_MONTH) + File.separator;
        realPath.append(str);
        realPath.append(fileName);
        File fileMkd = new File(realPath.toString());
        //不存在文件夹就创建文件夹
        if(!fileMkd.getParentFile().exists()) {
            fileMkd.getParentFile().mkdirs();
        }
        //上传文件
        file.transferTo(fileMkd);

        //修改后成项目地址下可以访问的路径（images下）
        StringBuilder readPath2=new StringBuilder("images");
        readPath2.append(str);
        readPath2.append(fileName);
        return readPath2.toString().replace("\\","/");
    }
}
