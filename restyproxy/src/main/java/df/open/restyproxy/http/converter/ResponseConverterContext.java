package df.open.restyproxy.http.converter;

import df.open.restyproxy.command.RestyCommand;
import df.open.restyproxy.exception.RequestException;
import df.open.restyproxy.exception.RestyException;
import df.open.restyproxy.exception.ServerException;
import df.open.restyproxy.http.pojo.FailedResponse;
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
        // response 为null
        if (response == null) {
            restyCommand.failed(new ServerException("Failed to get response, it's null"));
            return result;

        }
        // response为FailedResponse， [connectException InterruptedException]  status 500
        if (FailedResponse.isFailedResponse(response)) {
            restyCommand.failed(FailedResponse.class.cast(response).getException());
            return result;
        }
        // 服务响应了请求，但是不是200
        int statusCode = response.getStatusCode();
        if (200 != statusCode) {
            if (statusCode >= 400 && statusCode < 500) {
                restyCommand.failed(new RequestException(response.getResponseBody()));
            } else if (statusCode >= 500) {
                restyCommand.failed(new ServerException(response.getResponseBody()));
            }
            return result;
        }


        byte[] body = response.getResponseBodyAsBytes();

        // 使用转换器 转换响应结果   json->object
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
            restyCommand.failed(new RestyException(response.getResponseBody()));
        }
        restyCommand.success();
        return result;
    }

}
