package com.cq1080.auth.repository;


import com.cq1080.auth.bean.Permission;
import com.cq1080.jpa.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends BaseRepository<Permission> {

}
