package com.longpeng.jail.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;

import javax.annotation.PostConstruct;
import java.io.*;


@Service
public class SmsService {
    private String baseUrl="http://112.35.10.201:5992/sms/tmpsubmit";
    private String ecName="北京市海淀区看守所";
    private String appId="adminc";
    private String secretKey="cq1080";
    private String sign="pKoSOqmiS";
    private RestTemplate restTemplate;

    @PostConstruct
    public void init(){
        restTemplate=new RestTemplate();

    }

    public void send(String phone,String code) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        Submit submit=new Submit();
        String[] params={code};
        submit.setApId(appId);
        submit.setEcName(ecName);
        submit.setSecretKey(secretKey);
        submit.setParams(mapper.writeValueAsString(params));
        submit.setMobiles(phone);
        submit.setAddSerial("");
        submit.setSign(sign);
        submit.setTemplateId("9ec4b9678108449896a5250d53b59325");

        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append(submit.getEcName());
        stringBuffer.append(submit.getApId());
        stringBuffer.append(submit.getSecretKey());
        stringBuffer.append(submit.getTemplateId());
        stringBuffer.append(submit.getMobiles());
        stringBuffer.append(submit.getParams());
        stringBuffer.append(submit.getSign());
        stringBuffer.append(submit.getAddSerial());
        String mac = DigestUtils.md5DigestAsHex(stringBuffer.toString().getBytes());
        submit.setMac(mac);

        String param =  mapper.writeValueAsString(submit);
        String encode = Base64Utils.encodeToString(param.getBytes());
        String body=restTemplate.postForEntity(baseUrl, encode , String.class).getBody();
    }


}
