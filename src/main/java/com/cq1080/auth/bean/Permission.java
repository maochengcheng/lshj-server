package com.cq1080.auth.bean;

import com.cq1080.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Permission extends BaseEntity {

    private String method;

    private String name;

    private String url;

    private String path;

    private String  tag;

    private transient  boolean check;

    /**
     * 类型，admin 默认被后台占用
     */
    private String  type;

    public Permission() {
    }

    public Permission(String method, String url) {
        this.method = method;
        this.url = url;
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Permission)){
            return false;
        }
        Permission permission = (Permission) obj;
        return (getId()!=null && getId().equals(permission.getId())) || (method.equals(permission.getMethod()) && url.equals(permission.getUrl()));
    }
}
