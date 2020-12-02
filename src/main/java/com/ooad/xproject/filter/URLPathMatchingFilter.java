package com.ooad.xproject.filter;

import com.ooad.xproject.service.PermissionService;
import com.ooad.xproject.utils.SpringContextUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public class URLPathMatchingFilter extends PathMatchingFilter {

    private PermissionService adminPermissionService;

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if (HttpMethod.OPTIONS.toString().equals((httpServletRequest).getMethod())) {
            httpServletResponse.setStatus(HttpStatus.NO_CONTENT.value());
            return true;
        }

        if (null==adminPermissionService) {
            adminPermissionService = SpringContextUtils.getContext().getBean(PermissionService.class);
        }

        String requestAPI = getPathWithinApplication(request);
        System.out.println("Visit interface" + requestAPI);

        Subject subject = SecurityUtils.getSubject();

        if (!subject.isAuthenticated()) {
            System.out.println("Need login");
            return false;
        }

        boolean needFilter = adminPermissionService.needFilter(requestAPI);
        if (!needFilter) {
            System.out.println("Interface: " + requestAPI + " not need auth");
            return true;
        } else {
            System.out.println("Verify access rights: " + requestAPI);
            // 判断当前用户是否有相应权限
            boolean hasPermission = false;
            String username = subject.getPrincipal().toString();
            Set<String> permissionAPIs = adminPermissionService.listPmsURLsByUsername(username);
            if (permissionAPIs.contains(requestAPI)) {
                hasPermission = true;
            }

            if (hasPermission) {
                System.out.println("Access right: " + requestAPI + "verify successfully");
                return true;
            } else {
                System.out.println("The current user has no right to access interface" + requestAPI);
                return false;
            }
        }
    }
}