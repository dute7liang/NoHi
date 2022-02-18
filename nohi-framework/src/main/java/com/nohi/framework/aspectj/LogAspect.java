package com.nohi.framework.aspectj;

import com.alibaba.fastjson.JSON;
import com.nohi.common.annotation.Log;
import com.nohi.common.core.domain.model.LoginUser;
import com.nohi.common.enums.BusinessStatus;
import com.nohi.common.enums.HttpMethod;
import com.nohi.common.utils.SecurityUtils;
import com.nohi.common.utils.ServletUtils;
import com.nohi.common.utils.StringUtils;
import com.nohi.common.utils.ip.IpUtils;
import com.nohi.common.utils.spring.SpringUtils;
import com.nohi.framework.manager.AsyncManager;
import com.nohi.framework.manager.factory.AsyncFactory;
import com.nohi.framework.util.MarketUtils;
import com.nohi.framework.web.service.AsyncService;
import com.nohi.system.domain.SysOperLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 操作日志记录处理
 *
 * @author nohi
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult) {
        try {
            long startTime = System.currentTimeMillis();
            StringBuilder logString = new StringBuilder();
            logString.append("record logs:");
            // 获得注解
            if (controllerLog == null || (!controllerLog.isPrint() && !controllerLog.isSaveDb())) {
                return;
            }
            // 获取当前的用户
            HttpServletRequest request = ServletUtils.getRequest();
            LoginUser loginUser = SecurityUtils.getLoginUser();
            // *========数据库日志=========*//
            SysOperLog operLog = new SysOperLog();
            operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
            // 请求的地址
            String ip = IpUtils.getIpAddr(request);
            operLog.setOperIp(ip);
            operLog.setOperUrl(request.getRequestURI());
            // 设置请求方式
            operLog.setRequestMethod(request.getMethod());
            logString.append(String.format("ip=%s;",ip));
            logString.append(String.format("platform=%s;version=%s;", MarketUtils.getMarket(request), MarketUtils.getVersion(request)));
            logString.append(String.format("agent=%s;",MarketUtils.getAgent(request)));
            if (loginUser != null) {
                logString.append(String.format("userName=%s;",loginUser.getUsername()));
                operLog.setOperName(loginUser.getUsername());
            }
            logString.append(String.format("url=%s;method=%s;title=%s;",operLog.getOperUrl(),operLog.getRequestMethod(),controllerLog.title()));
            if (e != null) {
                operLog.setStatus(BusinessStatus.FAIL.ordinal());
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
                logString.append(String.format("exception=%s;",e.getMessage()));
            }
            logString.append(String.format("result=%s;",operLog.getJsonResult()));
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult,logString);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            logString.append("StartTime:").append(dateFormat.format(new Date(startTime)));
            long endTime = System.currentTimeMillis();
            logString.append(";EndTime:").append(dateFormat.format(new Date(endTime)));
            logString.append(";CostTime:").append(endTime - startTime).append("ms");
            AsyncService bean = SpringUtils.getBean(AsyncService.class);
            if(controllerLog.isPrint()){
                bean.recordLog(logString);
            }
            if(controllerLog.isSaveDb()){
                AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
            }
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log     日志
     * @param operLog 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, SysOperLog operLog, Object jsonResult,
                                               StringBuilder logString) throws Exception {
        // 设置action动作
        operLog.setBusinessType(log.businessType().ordinal());
        // 设置标题
        operLog.setTitle(log.title());
        // 设置操作人类别
        operLog.setOperatorType(log.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operLog,logString);
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && StringUtils.isNotNull(jsonResult)) {
            String jsonResultString = JSON.toJSONString(jsonResult);
            operLog.setJsonResult(StringUtils.substring(jsonResultString, 0, 2000));
            logString.append(String.format("result=%s;",jsonResultString));

        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog,StringBuilder logString) throws Exception {
        String requestMethod = operLog.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            operLog.setOperParam(StringUtils.substring(params, 0, 2000));
            logString.append(String.format("params=%s;",params));
        } else {
            Map<String, String[]> paramsMap = ServletUtils.getRequest().getParameterMap();
            String params = toMapString(paramsMap);
            operLog.setOperParam(StringUtils.substring(params, 0, 2000));
            logString.append(String.format("params=%s;",params));
        }
    }

    private String toMapString(Map<String, String[]> paramsMap){
        StringBuilder paramString = new StringBuilder();
        paramString.append("{");
        for(Map.Entry<String, String[]> paramEntry : paramsMap.entrySet()) {
            paramString.append(paramEntry.getKey()).append(":");
            if (Objects.nonNull(paramEntry.getValue())) {
                paramString.append(org.apache.commons.lang3.StringUtils.join(paramEntry.getValue(),","));
            }
            paramString.append(";");
        }
        paramString.append("}");
        return paramString.toString();
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (StringUtils.isNotNull(o) && !isFilterObject(o)) {
                    try {
                        Object jsonObj = JSON.toJSON(o);
                        params += jsonObj.toString() + " ";
                    } catch (Exception e) {
                    }
                }
            }
        }
        return params.trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
