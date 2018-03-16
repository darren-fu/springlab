package df.open.restypass.wrapper.spring.pojo;

import lombok.Data;


/**
 * body
 * Created by darrenfu on 17-7-28.
 */
@Data
public class RequestBodyData {

    private Integer index;
    private String name;
    private boolean required;
    private Object defaultValue = null;



}
