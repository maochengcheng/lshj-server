package com.longpeng.jail.service;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaMsgServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.github.wenhao.jpa.Specifications;
import com.longpeng.jail.config.WechatMiniappConfig;
import com.longpeng.jail.datasource.repository.WechatMiniappConfigRepository;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import javax.annotation.PostConstruct;

@Service
public class WechatMiniappService {

    private WxMaService wxService;

    private WechatMiniappConfigRepository wechatMiniappConfigRepository;

    private WxMaMsgService wxMaMsgService;


    @Autowired
    public WechatMiniappService(WechatMiniappConfigRepository wechatMiniappConfigRepository) {
        this.wechatMiniappConfigRepository = wechatMiniappConfigRepository;
    }

    @PostConstruct
    public void initialization(){
      //  WechatMiniappConfig wechatMiniappConfig = wechatMiniappConfigRepository.findOne(Specifications.<WechatMiniappConfig>and().eq("id", 1).build()).orElse(null);
        wxService=new WxMaServiceImpl();

//        if(!StringUtils.isEmpty(wechatMiniappConfig)){
//            WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
//            config.setAppid(wechatMiniappConfig.getAppId());
//            config.setSecret(wechatMiniappConfig.getSecret());
//            wxService.setWxMaConfig(config);
//        }
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        // 传成软件
        config.setAppid("wx59e00bd9bbace06b");
        config.setSecret("370715f07c78a04f4b881d51e01cbcd1");
        // 安徽合肥
//        config.setAppid("wx9414a67deec3667b");
//        config.setSecret("49c23ebe6175e11a79bc531c2225c9b0");
        //安徽宿州
//       config.setAppid("wx64fa29453b800907");
//        config.setSecret("241e08b0f23c76edc89b4bdf3a6b6510");


        wxService.setWxMaConfig(config);
        wxMaMsgService=new WxMaMsgServiceImpl(wxService);

    }

    /**
     * 登陆
     * @param code
     * @return
     */
    public WxMaJscode2SessionResult login(String code){
        try {
            return wxService.getUserService().getSessionInfo(code);
        } catch (WxErrorException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送订阅消息
     * @param subscribeMessage
     * @throws WxErrorException
     */
    public void sendSubscribeMsg(WxMaSubscribeMessage subscribeMessage) throws WxErrorException {
        wxMaMsgService.sendSubscribeMsg(subscribeMessage);
    }









}
