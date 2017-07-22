package df.open.restyproxy.http.converter;

import df.open.restyproxy.command.RestyCommand;
import org.asynchttpclient.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darrenfu on 17-7-20.
 */
public class ResponseConverterContext {

    public static ResponseConverterContext DEFAULT = new ResponseConverterContext();

    /**
     * 转换器列表
     */
    private List<ResponseConverter> converterList;

    /**
     * Instantiates a new Response converter context.
     */
    public ResponseConverterContext() {
        this.converterList = new ArrayList<>();
        converterList.add(new JsonResponseConverter());
        converterList.add(new StringResponseConverter());
    }

    /**
     * Instantiates a new Response converter context.
     *
     * @param converterList the converter list
     */
    public ResponseConverterContext(List<ResponseConverter> converterList) {
        this.converterList = converterList;
    }


    /**
     * Convert response object.
     *
     * @param restyCommand the resty command
     * @param response     the response
     * @return the object
     */
    public Object convertResponse(RestyCommand restyCommand, Response response) {

        Object result = null;

        if (response != null && 200 != response.getStatusCode()) {
            throw new RuntimeException(response.getResponseBody());
        }

        byte[] body = response.getResponseBodyAsBytes();

        boolean converted = false;
        Type returnType = restyCommand.getReturnType();
        String respContentType = response.getContentType();
        for (ResponseConverter converter : converterList) {
            if (converter.support(returnType, respContentType)) {
                converted = true;
                result = converter.convert(body, returnType, respContentType);
                break;
            }
        }
        if (!converted) {
            throw new RuntimeException("没有合适的响应解析器:" + restyCommand);
        }
        return result;
    }

}
