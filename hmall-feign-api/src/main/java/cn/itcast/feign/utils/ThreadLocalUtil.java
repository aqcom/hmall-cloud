package cn.itcast.feign.utils;//package com.hmall.common.utils;
//
//import org.springframework.stereotype.Component;
//
///**
// * 用于存储请求头中的userId信息
// */
//@Component
//public class ThreadLocalUtil {
//    private static final ThreadLocal<Long> local = new ThreadLocal<>();
//    /**
//     * 从当前线程中存储userId
//     * @param userId
//     */
//    public static void  setUserId(Long userId){
//        local.set(userId);
//    }
//
//    /**
//     * 从当前线程中获取userId
//     * @return
//     */
//    public static Long getUserId(){
//        return local.get();
//    }
//
//    /**
//     * 从当前线程中清除userId
//     */
//    public static void clear(){
//        local.remove();
//    }
//}