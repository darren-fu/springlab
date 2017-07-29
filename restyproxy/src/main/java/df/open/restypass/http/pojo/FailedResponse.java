package df.open.restypass.http.pojo;

import df.open.restypass.exception.RestyException;
import io.netty.handler.codec.http.HttpHeaders;
import org.asynchttpclient.Response;
import org.asynchttpclient.cookie.Cookie;
import org.asynchttpclient.uri.Uri;

import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by darrenfu on 17-7-25.
 */
public class FailedResponse implements Response {


    private RestyException exception;

    private FailedResponse(RestyException exception) {
        this.exception = exception;
    }

    public static FailedResponse create(RestyException ex) {
        return new FailedResponse(ex);
    }

    public static boolean isFailedResponse(Response response) {
        return response instanceof FailedResponse;
    }


    public RestyException getException() {
        return this.exception;
    }


    @Override
    public int getStatusCode() {
        return 500;
    }


    @Override
    public String getStatusText() {
        return null;
    }

    @Override
    public byte[] getResponseBodyAsBytes() {
        return new byte[0];
    }

    @Override
    public ByteBuffer getResponseBodyAsByteBuffer() {
        return null;
    }

    @Override
    public InputStream getResponseBodyAsStream() {
        return null;
    }

    @Override
    public String getResponseBody(Charset charset) {
        return null;
    }

    @Override
    public String getResponseBody() {
        return null;
    }

    @Override
    public Uri getUri() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public List<String> getHeaders(String name) {
        return null;
    }

    @Override
    public HttpHeaders getHeaders() {
        return null;
    }

    @Override
    public boolean isRedirected() {
        return false;
    }

    @Override
    public List<Cookie> getCookies() {
        return null;
    }

    @Override
    public boolean hasResponseStatus() {
        return false;
    }

    @Override
    public boolean hasResponseHeaders() {
        return false;
    }

    @Override
    public boolean hasResponseBody() {
        return false;
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public SocketAddress getLocalAddress() {
        return null;
    }
}
