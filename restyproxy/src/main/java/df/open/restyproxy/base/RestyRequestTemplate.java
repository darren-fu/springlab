package df.open.restyproxy.base;

import df.open.restyproxy.util.CommonTools;
import df.open.restyproxy.util.JsonTools;
import df.open.restyproxy.util.StringBuilderFactory;
import df.open.restyproxy.wrapper.spring.pojo.PathVariableData;
import df.open.restyproxy.wrapper.spring.pojo.RequestBodyData;
import df.open.restyproxy.wrapper.spring.pojo.RequestParamData;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Request 模板
 * Created by darrenfu on 17-6-25.
 */
@SuppressWarnings("unchecked")
@Data
public class RestyRequestTemplate {

    private static final Pattern pathVariableReg = Pattern.compile("\\{.+?\\}");

    private Method method;

    // GET POST
    private String httpMethod;

    // pat = baseUrl + methodUrl
    private String path;

    // @RequestMapping on class
    private String baseUrl;

    //@RequestMapping on method
    private String methodUrl;

    // @RequestMapping中定义的 Header
    private Map<String, String> headers;

    // @RequestMapping中定义的 params
    private Map<String, Object> params;

    //@RequestParam
    private List<RequestParamData> requestParams;

    //@PathVariable
    private List<PathVariableData> pathVariables;

    //@RequestBody参数 或者是没有注解的参数
    private List<RequestBodyData> requestBody;


    /**
     * Gets headers.
     *
     * @return the headers
     */
    public Map<String, String> getHeaders() {
        if (CommonTools.isEmpty(headers)) {
            headers = new HashMap<>();
        }
        if (!headers.containsKey(RestyConst.CONTENT_TYPE)) {
            headers.put(RestyConst.CONTENT_TYPE, RestyConst.APPLICATION_JSON);
        }
        return headers;
    }

    /**
     * Add header.
     *
     * @param head  the head
     * @param value the value
     */
    public void addHeader(String head, String value) {
        if (headers == null) {
            headers = new HashMap();
        }
        headers.put(head, value);
    }

    /**
     * Add param.
     *
     * @param param the param
     * @param value the value
     */
    public void addParam(String param, String value) {
        if (params == null) {
            params = new HashMap();
        }
        params.put(param, value);
    }

    /**
     * Add request param.
     *
     * @param requestParamData the request param data
     */
    public void addRequestParam(RequestParamData requestParamData) {
        if (requestParams == null) {
            requestParams = new ArrayList<>();
        }
        requestParams.add(requestParamData);
    }


    /**
     * Add path variable.
     *
     * @param pathVariableData the path variable data
     */
    public void addPathVariable(PathVariableData pathVariableData) {
        if (pathVariables == null) {
            pathVariables = new ArrayList<>();
        }
        pathVariables.add(pathVariableData);
    }

    /**
     * Add request body.
     *
     * @param requestBodyData the request body data
     */
    public void addRequestBody(RequestBodyData requestBodyData) {
        if (requestBody == null) {
            requestBody = new ArrayList<>();
        }
        requestBody.add(requestBodyData);
    }

    /**
     * Gets request path.
     *
     * @param args the args
     * @return the request path
     */
    public String getRequestPath(Object[] args) {
        if (this.pathVariables != null && this.pathVariables.size() > 0) {
            StringBuffer sb = new StringBuffer(64);
            Matcher matcher = pathVariableReg.matcher(this.path);
            while (matcher.find()) {
                String pathVariable = findPathVariable(matcher.group(), args);
                matcher.appendReplacement(sb, ObjectUtils.defaultIfNull(pathVariable, ""));
            }
            return sb.toString();
        } else {
            return this.path;
        }
    }

    /**
     * 获取 path variable
     *
     * @param placeholder {XXX}
     * @param args        object[] args
     * @return path value
     */
    private String findPathVariable(String placeholder, Object[] args) {
        for (PathVariableData pathVariable : this.pathVariables) {
            if (pathVariable.getName().equalsIgnoreCase(placeholder)) {
                if (args.length > pathVariable.getIndex() && args[pathVariable.getIndex()] != null) {
                    return args[pathVariable.getIndex()].toString();
                }
            }
        }
        return null;
    }


    /**
     * Gets query string.
     *
     * @param args the args
     * @return the query string
     */
    public String getQueryString(Object[] args) {
        if (CommonTools.isEmpty(this.params) && CommonTools.isEmpty(this.requestParams)) {
            return null;
        }
        StringBuilder sb = StringBuilderFactory.DEFAULT.stringBuilder();
        int index = 0;

        if (!CommonTools.isEmpty(this.getParams())) {
            // 处理Params的值
            Set<String> paramNames = this.getParams().keySet();
            for (String paramName : paramNames) {
                if (index != 0) {
                    sb.append("&");
                }
                Object paramValue = this.getParams().getOrDefault(paramName, "");
                sb.append(paramName);
                sb.append("=");
                sb.append(paramValue);
                index++;
            }
        }

        if (!CommonTools.isEmpty(this.requestParams)) {
            // 处理RequestParam的值
            for (RequestParamData requestParam : this.requestParams) {
                Object arg = args[requestParam.getIndex()];
                if (arg == null) {
                    arg = requestParam.getDefaultValue();
                }
                String argValue = arg.toString();
                if (index != 0) {
                    sb.append("&");
                }
                sb.append(requestParam.getName());
                sb.append("=");
                sb.append(argValue);
                index++;
            }

        }
        return sb.toString();
    }


    /**
     * Gets body.
     *
     * @param args the args
     * @return the body
     */
    public String getBody(Object[] args) {
        if (RestyConst.HTTP_POST.equalsIgnoreCase(this.httpMethod)) {
            // POST 请求 没有请求参数时，body -> {}
            if (CommonTools.isEmpty(this.requestBody)) {
                return "{}";
            }
            // body的参数数量一个
            if (this.requestBody.size() == 1) {
                return JsonTools.nonNullMapper().toJson(args[this.requestBody.get(0).getIndex()]);
            }

            // body的参数数量多个
            Map<String, Object> bodyMap = new HashMap<>();
            for (RequestBodyData bodyData : this.requestBody) {
                bodyMap.put(bodyData.getName(), args[bodyData.getIndex()]);
            }
            return JsonTools.nonNullMapper().toJson(bodyMap);
        } else {
            return null;
        }
    }

}
