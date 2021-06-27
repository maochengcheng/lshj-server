package com.cq1080.auth.bean;


import com.cq1080.jpa.entity.BaseEntity;
import com.cq1080.meta.annotation.DeleteType;
import com.cq1080.meta.annotation.EditType;
import com.cq1080.meta.annotation.Meta;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.List;

@Data
@Entity
@Meta(delete = DeleteType.DELETE,edit = EditType.EDIT)
public class Role extends BaseEntity {


    @ManyToMany(fetch = FetchType.EAGER)
    private List<Permission> permissions;

    @Meta(displayName = "角色名称",displayInList = true,searchable = true,clickable = true)
    private String name;
}
