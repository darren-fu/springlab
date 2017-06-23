package df.open.rest.entity;

import lombok.Data;

/**
 * Created by darrenfu on 17-6-19.
 */
@Data
public class Response<T> {

    private String code;

    private String msg;

    private T info;
}
