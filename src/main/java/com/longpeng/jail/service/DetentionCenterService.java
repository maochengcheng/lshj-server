package com.longpeng.jail.service;

import com.cq1080.bean.form.PageForm;
import com.cq1080.jpa.specification.SpecificationUtil;
import com.cq1080.rest.APIError;
import com.cq1080.utils.StringUtil;
import com.longpeng.jail.bean.entity.DetentionCenter;
import com.longpeng.jail.bean.request.InsertDetentionCenterFreezeReq;
import com.longpeng.jail.datasource.mapper.DetentionCenterMapper;
import com.longpeng.jail.datasource.repository.DetentionCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 看守所
 */
@Service
public class DetentionCenterService {
    private DetentionCenterRepository detentionCenterRepository;
    private DetentionCenterMapper detentionCenterMapper;

    @Autowired
    public DetentionCenterService(DetentionCenterRepository detentionCenterRepository, DetentionCenterMapper detentionCenterMapper) {
        this.detentionCenterRepository = detentionCenterRepository;
        this.detentionCenterMapper = detentionCenterMapper;
    }

    /**
     * 获取看守所信息列表
     * @param detentionCenter
     * @param pageForm
     * @return
     */
    public Page<DetentionCenter> getDetentionCenter(DetentionCenter detentionCenter, PageForm pageForm){
        Page<DetentionCenter> detentionCenters = detentionCenterRepository.findAll((SpecificationUtil.<DetentionCenter>filter(detentionCenter, pageForm).build()), pageForm.pageRequest());
        return detentionCenters;
    }

    /**
     * 编辑或添加看守所
     * @param detentionCenter
     * @return
     */
    public DetentionCenter insertDetentionCenter(DetentionCenter detentionCenter){
        if(!StringUtils.isEmpty(detentionCenter.getId()) && detentionCenter.getFreeze().equals(1)){
            APIError.e(40000,"请先冻结单位在编辑单位");
        }
        detentionCenterRepository.save(detentionCenter);
        return detentionCenter;
    }

    /**
     * 冻结解冻单位
     * @param insertDetentionCenterFreezeReq
     */
    public void insertDetentionCenterFreeze(InsertDetentionCenterFreezeReq insertDetentionCenterFreezeReq){
        DetentionCenter detentionCenter = detentionCenterRepository.findById(insertDetentionCenterFreezeReq.getId()).orElse(null);
        if(StringUtils.isEmpty(detentionCenter)){
            APIError.e(400,"编辑id不存在");
        }
        detentionCenter.setFreeze(insertDetentionCenterFreezeReq.getFreeze());
        detentionCenterRepository.save(detentionCenter);
    }

    /**
     * 删除看守所
     * @param ids
     */
    public void deleteDetentionCenter(String ids){
        Integer[] integers = StringUtil.toIntArray(ids);
        for(Integer i:integers){
            detentionCenterMapper.deleteDetentionCenterById(i);
        }
    }
}
