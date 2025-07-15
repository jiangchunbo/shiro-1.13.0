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
package org.apache.shiro.aop;

import java.lang.reflect.Method;

/**
 * 3rd-party API independent representation of a method invocation.  This is needed so Shiro can support other
 * MethodInvocation instances from other AOP frameworks/APIs.
 * <p>
 * 这是一个独立于第三方 API 的方法调用。
 * 作者说这是必须的，
 * 以便 Shiro 能够支持来自其他 AOP 框架或者 API 的 MethodInvocation
 *
 * @since 0.1
 */
public interface MethodInvocation {

    /**
     * Continues the method invocation chain, or if the last in the chain, the method itself.
     * <p>
     * 继续方法调用链，如果这是链的最后一个，则调用方法本身
     *
     * @return the result of the Method invocation.
     * @throws Throwable if the method or chain throws a Throwable
     */
    Object proceed() throws Throwable;

    /**
     * Returns the actual {@link Method Method} to be invoked.
     * <p>
     * 返回实际调用的方法
     *
     * @return the actual {@link Method Method} to be invoked.
     */
    Method getMethod();

    /**
     * Returns the (possibly null) arguments to be supplied to the method invocation.
     * <p>
     * 返回提供给方法调用的参数(可能是 null)
     *
     * @return the (possibly null) arguments to be supplied to the method invocation.
     */
    Object[] getArguments();

    /**
     * Returns the object that holds the current joinpoint's static part.
     * For instance, the target object for an invocation.
     * <p>
     * 返回 joinpoint 的静态部分，静态部分一般就是指编译器、程序加载时就已经确定的部分，对于方法调用来说，目标对象就是静态部分
     *
     * @return the object that holds the current joinpoint's static part.
     * @since 1.0
     */
    Object getThis();


}

