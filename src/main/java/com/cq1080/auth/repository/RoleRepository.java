package com.cq1080.auth.repository;


import com.cq1080.auth.bean.Role;
import com.cq1080.jpa.repository.BaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseRepository<Role> {

}
