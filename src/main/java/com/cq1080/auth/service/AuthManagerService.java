package com.cq1080.auth.service;

import com.cq1080.auth.bean.*;
import com.cq1080.auth.repository.ManagerLoginLogRepository;
import com.cq1080.auth.repository.ManagerRepository;
import com.cq1080.bean.form.PageForm;
import com.cq1080.cache.service.CacheService;
import com.cq1080.jpa.specification.SpecificationUtil;
import com.cq1080.rest.APIError;
import com.cq1080.utils.StringUtil;
import com.cq1080.utils.TokenUtil;
import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class AuthManagerService {


    private CacheService  cacheService;

    private PermissionService permissionService;

    private ManagerRepository  managerRepository;

    private ManagerLoginLogRepository loginLogRepository;

    protected ApplicationContext mContext;


    @Autowired
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.mContext = applicationContext;
    }

    @Autowired
    public void setLoginLogRepository(ManagerLoginLogRepository loginLogRepository) {
        this.loginLogRepository = loginLogRepository;
    }

    @Autowired
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Autowired
    public void setManagerRepository(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    /**
     * 管理员登录
     * @param loginForm 登录参数对象
     * @return ManagerLoginVO
     */
    public ManagerLoginVO login(LoginForm loginForm) {
        Manager manager = managerRepository.findOne(Specifications.<Manager>and().eq("mobile", loginForm.getMobile()).eq("password", getPassword(loginForm.getPassword())).build()).orElse(null);
        if (manager == null) {
            APIError.e("账号或密码错误");
        }
        loginLogRepository.findAll(Specifications.<ManagerLoginLog>and().eq("managerId", manager.getId()).eq("status",ManagerLoginLog.Status.ACTIVE).build()).forEach(it ->{
            it.setStatus(ManagerLoginLog.Status.EXPIRED);
            loginLogRepository.save(it);
            cacheService.deleteObject(  "manager_" + it.getToken());
        });
        ManagerLoginLog loginLog = new ManagerLoginLog();
        loginLog.setToken(TokenUtil.generateToken());
        loginLog.setManagerId(manager.getId());
        loginLog.setName(manager.getName());
        loginLog.setIp(getIP());

        loginLogRepository.save(loginLog);
        //save login token
        String key = "manager_" + loginLog.getToken();
        cacheService.saveObject(key,loginLog);
        ManagerToken jwtToken = new ManagerToken(loginLog.getToken());
        SecurityUtils.getSubject().login(jwtToken);
        return ManagerLoginVO.builder().token(loginLog.getToken()).permissions(permissionService.getPermissionByUser(manager.getId())).info(manager).build();
    }

    /**
     * 初始化超级管理员
     * @param form 超级管理员基本资料
     * @return Manager
     */
    @Transactional
    public Manager  initManager(CreateManagerForm  form){
        if (managerRepository.count(Specifications.<Manager>and().eq("type",Manager.Type.SUPER_MANAGER).build()) >0){
            APIError.e("超级管理员已存在");
        };
        Manager manager = new Manager();
        manager.setType(Manager.Type.SUPER_MANAGER);
        manager.setName(form.getName());
        manager.setMobile(form.getMobile());
        manager.setPassword(getPassword(form.getPassword()));
        Manager save = managerRepository.save(manager);
        sendEvent(save);
        return save;
    }

    public void sendEvent(Object object) {
        mContext.publishEvent(object);
    }

    /**
     * 创建或者编辑管理员
     * @param form 超级管理员基本资料
     * @param init 是否初始化超级管理员
     * @return Manager
     */
    @Transactional
    public Manager  createManager(CreateManagerForm form,boolean  init){
        if (init){
            return initManager(form);
        }
        if(form.getId() == null && managerRepository.count(Specifications.<Manager>and().eq("mobile",form.getMobile()).build())>0){
            APIError.e("该登录账号已被注册");
        }
        Manager manager = new Manager();
        if (form.getId() != null){
            manager = managerRepository.findItemById(form.getId());
        }
        if (!form.getMobile().equals(manager.getMobile()) && managerRepository.count(Specifications.<Manager>and().eq("presenceStatus",1).eq("mobile",form.getMobile()).build())>0){
            APIError.e("该登录账号已被注册");
        }
        manager.setMobile(form.getMobile());
        if (manager.getPassword() == null){
            manager.setPassword(getPassword(form.getPassword()));
        }
        manager.setRoles(form.getRoles());
        manager.setType(Manager.Type.MANAGER);
        manager.setName(form.getName());
        manager.setDetentionName(form.getDetentionName());
        sendEvent(manager);
        return managerRepository.save(manager);
    }

    private String  getPassword(String password){
        return  DigestUtils.md5DigestAsHex(password.getBytes());
    }

    /**
     * 获取管理员列表
     * @param manager meta信息
     * @param pageForm 筛选条件
     * @return 管理员列表
     */
    public Page<Manager> getManager(Manager manager, PageForm pageForm) {
        PredicateBuilder<Manager> spec = SpecificationUtil.filter(manager,pageForm);
        spec.eq("type",Manager.Type.MANAGER);
        return managerRepository.findAll(spec.build(),pageForm.pageRequest());
    }

    /**
     * 更改登录密码
     * @param loginForm 密码
     */
    public void changePassword(PasswordForm loginForm) {
        Manager manager = getCurrentManager();
        manager.setPassword(getPassword(loginForm.getPassword()));
        managerRepository.save(manager);
        SecurityUtils.getSubject().logout();
    }

    /**
     * 获取当前登录管理员
     * @return Manager
     */
    public Manager  getCurrentManager(){
        String token = getCurrentRequest().getHeader("accessToken");
        if (StringUtils.isEmpty(token)){
            APIError.NEED_LOGIN();
        }
        ManagerLoginLog loginLog = getManagerLoginLog(token);
        if (loginLog == null){
            APIError.NEED_LOGIN();
        }
        return managerRepository.findItemById(loginLog.getManagerId());
    }


    public Optional<ManagerLoginLog> getCurrentUserLogin(boolean throwError){
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()){
            if (throwError){
                APIError.NEED_LOGIN();
            }
            return Optional.empty();
        }
        return Optional.ofNullable((ManagerLoginLog) subject.getPrincipal());
    }

    protected HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        return servletRequest;
    }

    public String  getIP(){
        HttpServletRequest request = getCurrentRequest();
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多IP的情况（只取第一个IP）
        if (ip != null && ip.contains(",")) {
            String[] ipArray = ip.split(",");
            ip = ipArray[0];
        }
        return ip;
    }

    /**
     * 删除管理员
     * @param ids 管理员ids
     */
    public void deleteManager(String ids) {
        managerRepository.deleteInBatch(StringUtil.toDeleteBatch(ids,Manager.class));
    }

    /**
     * 从缓存获取当前登录用户
     * @param token 交互token
     * @return ManagerLoginLog
     */
    public ManagerLoginLog getManagerLoginLog(String token) {
        String key ="manager_"+token;
        return cacheService.getObject(key,ManagerLoginLog.class);
    }
}
