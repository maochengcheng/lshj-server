package com.longpeng.jail.service;

import com.cq1080.bean.form.PageForm;
import com.cq1080.jpa.specification.SpecificationUtil;
import com.cq1080.rest.APIError;
import com.cq1080.utils.StringUtil;
import com.longpeng.jail.bean.entity.DetentionCenter;
import com.longpeng.jail.bean.entity.SysDictionary;
import com.longpeng.jail.bean.request.InsertDetentionCenterFreezeReq;
import com.longpeng.jail.datasource.mapper.SysDictionaryMapper;
import com.longpeng.jail.datasource.repository.DetentionCenterRepository;
import com.longpeng.jail.datasource.repository.SysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典
 */
@Service
public class SysDictionaryService {
    private SysDictionaryRepository sysDictionaryRepository;
    private SysDictionaryMapper sysDictionaryMapper;

    @Autowired
    public SysDictionaryService(SysDictionaryRepository sysDictionaryRepository, SysDictionaryMapper sysDictionaryMapper) {
        this.sysDictionaryRepository = sysDictionaryRepository;
        this.sysDictionaryMapper = sysDictionaryMapper;
    }

    /**
     * 获取字典信息列表
     * @param sysDictionary
     * @param pageForm
     * @return
     */
    public Page<SysDictionary> getSysDictionary(SysDictionary sysDictionary, PageForm pageForm){
        sysDictionary.setStatus("0");
        Page<SysDictionary> sysDictionarys = sysDictionaryRepository.findAll((SpecificationUtil.<SysDictionary>filter(sysDictionary, pageForm).build()), pageForm.pageRequest());
        return sysDictionarys;
    }
    /**
     * 获取字典信息列表
     * @param sysDictionary
     * @param pageForm
     * @return
     */
    public Page<SysDictionary> getSysDictionaryItem(SysDictionary sysDictionary, PageForm pageForm){
        if(StringUtils.isEmpty(sysDictionary.getDicCode())){
            APIError.e(400,"dicCode不存在");
        }
        sysDictionary.setStatus("1");
        Page<SysDictionary> sysDictionarys = sysDictionaryRepository.findAll((SpecificationUtil.<SysDictionary>filter(sysDictionary, pageForm).build()), pageForm.pageRequest());
        return sysDictionarys;
    }
    /**
     * 编辑或添加字典
     * @param sysDictionary
     * @return
     */
    public SysDictionary insertSysDictionary(SysDictionary sysDictionary){
        sysDictionary.setStatus("0");
        sysDictionaryRepository.save(sysDictionary);
        return sysDictionary;
    }
    /**
     * 编辑或添加字典项目
     * @param sysDictionary
     * @return
     */
    public SysDictionary insertSysDictionaryItem(SysDictionary sysDictionary){
        sysDictionary.setStatus("1");
        sysDictionaryRepository.save(sysDictionary);
        return sysDictionary;
    }
    /**
     * 删除字典
     * @param ids
     */
    public void deleteSysDictionary(String ids){
        Integer[] integers = StringUtil.toIntArray(ids);
        for(Integer i:integers){
            sysDictionaryMapper.deleteSysDictionaryById(i);
        }
    }
}
