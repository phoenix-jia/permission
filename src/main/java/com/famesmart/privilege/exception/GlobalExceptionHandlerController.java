package com.famesmart.privilege.exception;

import com.famesmart.privilege.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerController {

  @ExceptionHandler(Exception.class)
  public Result handleException(Exception ex) {
    log.info(ex.getMessage());
    return Result.error(ex.getMessage());
  }
}
