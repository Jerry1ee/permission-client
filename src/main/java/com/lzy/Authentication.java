package com.lzy;

/**
 * 权限管理的切面类
 *
 * @author lzy
 */

import com.lzy.FeignClient.AuthenticService;
import com.lzy.annotation.GetUserId;
import com.lzy.annotation.PermissionName;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


@Aspect

public class Authentication {

    @Autowired
    AuthenticService authenticService;


//    第一个参数: args(userId, ..)
//    第二个参数: args(*, userId, ..)
//    第三个参数: args(*, *, userId, ..)

    @Pointcut("@annotation(com.lzy.annotation.PermissionName)" )
    public void getAnnotation() {
    }


@Around("getAnnotation()&&@annotation(permissionName)")
    public Object check(ProceedingJoinPoint pjp, PermissionName permissionName) {

        Object obj ;
        String name=permissionName.name();
        Object[] args=pjp.getArgs();

        Method method = MethodSignature.class.cast(pjp.getSignature()).getMethod();
        StringBuilder userId = new StringBuilder();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

    for (int argIndex = 0; argIndex < args.length; argIndex++) {
        for (Annotation paramAnnotation : parameterAnnotations[argIndex]) {
            if (!(paramAnnotation instanceof GetUserId)) {
                continue;
            }

            userId.append(args[argIndex]);
        }
    }
        if(userId.toString().isEmpty()) return false;
        System.out.println("切入了 ，下面执行feign调用");
        try {
            if(authenticService.checkUserPermission(name,Long.parseLong(userId.toString())))
            {
                System.out.println("可以执行切点!!!!!!!!!!");
                obj=pjp.proceed();
            }
            else
            {
                obj=false;
            }
        }
        catch (Throwable throwable)
        {
            return throwable;
        }

        return obj;
    }

}
