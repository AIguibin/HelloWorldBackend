package com.aiguibin.action;

import com.aiguibin.domain.DataVO;
import com.aiguibin.domain.UserDTO;
import com.aiguibin.entities.Employee;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 默认可以不写  @ParentPackage("struts-default")
 * 根命名空间, 可以不写  @Namespace("/") 全局配置,
 * 如果方法上不指定result, 则使用该Result
 * ----@Results({
 * --------@Result(name="success",location="success.jsp"),
 * --------@Result(name="error",location="/error.jsp")
 * ---})
 */
@Controller
@Scope("prototype")
@ParentPackage(value = "json-default")
public class EmployeeAction {

    private static final Log logger = LogFactory.getLog(EmployeeAction.class);

    @Getter
    @Setter
    private DataVO dataVO;
    @Getter
    @Setter
    private UserDTO userDTO;
    @Getter
    @Setter
    private String admin;


    /**
     * 返回JSON：
     * Result中的name对应的是方法中的返回值,location表示WEB-INF目录,所以它的值不能加"/"
     * 如果加上"/"就表示要去根目录下去找
     * results = {--@Result(name = "success", location = "success.jsp")})
     * 返回JSON数据的dataVO必须提供GetterSetter方法否则为空
     * 接收JSON：
     * 接收前端JSON数据必须依赖拦截器：interceptorRefs = {@InterceptorRef(value = "json")}
     * 其原理是通过struts2-json-plugin插件，配置 Struts拦截器拦截以后通过原生Servlet解析成JSON字符串
     * 然后与该Action的属性getter|setter方法把值设置进来
     * @return 字符串，根据字符串找到相应的结果解析本次请求的返回值
     */
    @Action(value = "save", results = {
            @Result(name = "success", location = "success.jsp"),
            @Result(name = "failed", type = "json", params = {"root", "dataVO"})},
            interceptorRefs = {@InterceptorRef(value = "json")}
            )
    public String save() {
        if ("admin".equals(admin)) {
            return "success";
        }
        /*try {
            logger.info("请求来到");
            Thread.sleep(60000*5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Employee employee = new Employee();
        employee.setEmployeeName(userDTO.getName());
        employee.setOperator(userDTO.getConfirm());
        employee.setEmployeeAge(Integer.valueOf(userDTO.getPassword()));
        dataVO = new DataVO(employee);
        logger.error(dataVO);
        return "failed";
    }

}
