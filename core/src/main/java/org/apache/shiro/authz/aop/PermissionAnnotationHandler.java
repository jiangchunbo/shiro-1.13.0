/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.authz.aop;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

import java.lang.annotation.Annotation;

/**
 * Checks to see if a @{@link org.apache.shiro.authz.annotation.RequiresPermissions RequiresPermissions} annotation is
 * declared, and if so, performs a permission check to see if the calling <code>Subject</code> is allowed continued
 * access.
 *
 * @since 0.9.0
 */
public class PermissionAnnotationHandler extends AuthorizingAnnotationHandler {

    /**
     * Default no-argument constructor that ensures this handler looks for
     * {@link org.apache.shiro.authz.annotation.RequiresPermissions RequiresPermissions} annotations.
     */
    public PermissionAnnotationHandler() {
        super(RequiresPermissions.class);
    }

    /**
     * Returns the annotation {@link RequiresPermissions#value value}, from which the Permission will be constructed.
     *
     * @param a the RequiresPermissions annotation being inspected.
     * @return the annotation's <code>value</code>, from which the Permission will be constructed.
     */
    protected String[] getAnnotationValue(Annotation a) {
        RequiresPermissions rpAnnotation = (RequiresPermissions) a;
        return rpAnnotation.value();
    }

    /**
     * Ensures that the calling <code>Subject</code> has the Annotation's specified permissions, and if not, throws an
     * <code>AuthorizingException</code> indicating access is denied.
     * <p>
     * 该方法是 {@link PermissionAnnotationMethodInterceptor} 的委托，用于在执行方法前进行权限校验
     *
     * @param a the RequiresPermission annotation being inspected to check for one or more permissions
     * @throws org.apache.shiro.authz.AuthorizationException if the calling <code>Subject</code> does not have the permission(s) necessary to
     *                                                       continue access or execution.
     */
    @Override
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        // 只处理 RequiresPermissions 注解，可能这个方法是 public 所以小心任何人都可以调用
        if (!(a instanceof RequiresPermissions)) return;

        // 获取注解对象的值
        RequiresPermissions rpAnnotation = (RequiresPermissions) a;
        String[] perms = getAnnotationValue(a);

        // 获取 Subject
        Subject subject = getSubject();

        // 如果只配置了一个值，那么快速判断返回 (不需要根据 AND 还是 OR 进行逻辑判断)
        if (perms.length == 1) {
            subject.checkPermission(perms[0]);
            return;
        }

        // 多个 permission，处理 AND
        if (Logical.AND.equals(rpAnnotation.logical())) {
            getSubject().checkPermissions(perms);
            return;
        }

        // 多个 permission，处理 OR
        if (Logical.OR.equals(rpAnnotation.logical())) {
            // Avoid processing exceptions unnecessarily - "delay" throwing the exception by calling hasRole first
            // 检查是否有任何一个权限
            boolean hasAtLeastOnePermission = false;
            for (String permission : perms)
                if (getSubject().isPermitted(permission))
                    hasAtLeastOnePermission = true;

            // Cause the exception if none of the role match, note that the exception message will be a bit misleading
            // 如果没有匹配到任何一个权限，抛出异常，而且以 pers[0] 抛出，因为不是那么重要
            if (!hasAtLeastOnePermission) getSubject().checkPermission(perms[0]);

        }
    }

}
