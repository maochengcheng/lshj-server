package com.cq1080.auth.service;


import com.cq1080.auth.bean.Manager;
import com.cq1080.auth.bean.Menu;
import com.cq1080.auth.bean.Permission;
import com.cq1080.auth.bean.Role;
import com.cq1080.auth.config.AuthResourceConfig;
import com.cq1080.auth.repository.ManagerRepository;
import com.cq1080.auth.repository.RoleRepository;
import com.cq1080.bean.form.PageForm;
import com.cq1080.jpa.specification.SpecificationUtil;

import com.cq1080.rest.APIError;
import com.cq1080.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PermissionService{

    private RoleRepository roleRepository;



    private ManagerRepository  managerRepository;

    private AuthManagerService  authManagerService;


    @Autowired
    public void setAuthManagerService(AuthManagerService authManagerService) {
        this.authManagerService = authManagerService;
    }

    @Autowired
    public void setManagerRepository(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Page<Role> getRoleList(Role role, PageForm pageForm){
        return roleRepository.findAll(SpecificationUtil.<Role>filter(role,pageForm).build(),pageForm.pageRequest());
    }

    /**
     * 修改编辑角色
     * @param role 角色实体
     * @return Role
     */
    public Role editRole(Role role) {

        return roleRepository.save(role);
    }

    /**
     * 获取当前登录管理员的权限
     * @return 管理员的权限列表
     */
    public List<Permission> getSelfPermission(){
        return getPermissionByUser(authManagerService.getCurrentManager().getId());
    }

    /**
     * 根据管理员id获取管理员权限
     * @param managerId 管理员id
     * @return 管理员的权限列表
     */
    public List<Permission> getPermissionByUser(Integer managerId){
        Manager manager = managerRepository.findItemById(managerId);
        if (manager == null){
            APIError.NOT_FOUND();
        }
        return manager.getRoles().stream().map(new Function<Role, List<Permission>>() {
            @Override
            public List<Permission> apply(Role role) {
                return role.getPermissions();
            }
        }).reduce(new BinaryOperator<List<Permission>>() {
            @Override
            public List<Permission> apply(List<Permission> permissions, List<Permission> permissions2) {
                permissions.addAll(permissions2);
                return permissions;
            }
        }).orElse(new ArrayList<>());
    }

    /**
     * 检查当前登录管理员是否有该功能的权限
     * @param method method
     * @param url url
     * @return 是否有权限
     */
    public boolean hasPermission(String method, String url) {

        return authManagerService.getCurrentManager().getType().equals(Manager.Type.SUPER_MANAGER) || getSelfPermission().indexOf(new Permission(method,url)) > -1;
    }

    /**
     * 查看所有权限
     *
     * @param manger 管理员id，可选
     * @param role 角色id，可选
     * @return 权限列表
     */
    public List<Menu> getAllPermission(Integer manger, Integer role,String tag){
        List<Menu> menuList = AuthResourceConfig.menuList;
        checkMyPermission(menuList,null,true);
        if (manger != null){
            List<Permission> permissions = getPermissionByUser(manger);
            checkMyPermission(menuList,permissions,false);
            return menuList;
        }
        if(role != null){
            List<Permission> permissions = roleRepository.findItemById(role).getPermissions();
            checkMyPermission(menuList,permissions,false);
            return menuList;
        }

        if (!StringUtils.isEmpty(tag)){
            return menuList.stream().filter(it -> tag.equals(it.getTag())).collect(Collectors.toList());
        }
        return menuList;
    }
    public List<Menu> getAllPermission(Integer manger, Integer role) {

        return getAllPermission(manger,role,null);
    }

    private void checkMyPermission(Collection<Menu> menus, List<Permission> permissions, boolean clear){
        if (menus == null || menus.size() ==0){
            return;
        }
        menus.forEach(it ->{
            it.getPermissions().forEach(pe ->{
                if (clear){
                    pe.setCheck(false);
                }else {
                    pe.setCheck(permissions.indexOf(pe)>-1);
                }

            });
            checkMyPermission(it.getSub(),permissions,clear);
        });
    }

    /**
     * 删除角色
     * @param ids 校色ids
     */
    public void deleteRole(String ids) {
        roleRepository.deleteInBatch(StringUtil.toDeleteBatch(ids,Role.class));
    }
}
