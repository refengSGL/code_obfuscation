package com.refengSGL.handler;

import cn.dev33.satoken.util.SaResult;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.refengSGL.exception.CodeException;
import com.refengSGL.utils.ExceptionUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @description: 统一异常处理器
 */
@Slf4j
@Controller
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 全局异常拦截
     */
    @ExceptionHandler(Exception.class)
    public SaResult handlerException(Exception e) {
        log.error(ExceptionUtils.getMessage(e));
        return SaResult.error("系统异常，请稍后重试");
    }


    @ExceptionHandler(CodeException.class)
    public SaResult codeException(CodeException e) {
        log.error(ExceptionUtils.getMessage(e));
        return SaResult.error(e.getMessage());
    }

    @ExceptionHandler(MismatchedInputException.class)
    public SaResult mismatchedInputException(MismatchedInputException e) {
        log.error(ExceptionUtils.getMessage(e));
        return SaResult.error("未接收到前端信息，JSON格式转换异常");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public SaResult httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(ExceptionUtils.getMessage(e));
        return SaResult.error("前端传递参数类型与后端接收类型不匹配，JSON反序列化失败");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public SaResult maxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error(ExceptionUtils.getMessage(e));
        return SaResult.error("上传文件大小超过限制");
    }
}
