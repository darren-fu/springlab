package df.open.spring.base;

import df.open.restyproxy.exception.RestyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by darrenfu on 17-7-27.
 */
@RestControllerAdvice
@Slf4j
public class RestControllerAspect {


    @ExceptionHandler(RestyException.class)
    public String restyError(RestyException ex, HttpServletRequest request) {
        log.info("catch resty exception:{}", ex.getMessage());
        return ex.getMessage();
    }

}
