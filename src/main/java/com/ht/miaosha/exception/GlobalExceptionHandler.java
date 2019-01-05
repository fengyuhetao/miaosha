package com.ht.miaosha.exception;

import com.ht.miaosha.result.CodeMsg;
import com.ht.miaosha.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by hetao on 2018/12/28.
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        System.out.println("---------------exception begin------------------");
        e.printStackTrace();
        System.out.println("---------------exception end------------------");
        if(e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return Result.error(ex.getCm());
        } else if(e instanceof LoginException) {
            try {
                response.sendRedirect("/to_login");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }
        else if(e instanceof BindException) {
            BindException ex = (BindException) e;
            List<ObjectError> errorList = ex.getAllErrors();
            ObjectError error = errorList.get(0);

            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        } else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
