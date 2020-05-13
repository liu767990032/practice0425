package com.logoxiang.practice.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

/**
 * 网络工具类
 *
 * @author mervinSun
 *
 */
public final class NetUtils {

    /**
     * HTTP链接请求超时设置（毫秒）
     */
    private final static int HTTP_CONNECTION_REQUEST_TIMEOUT = 5000;

    /**
     * HTTP链接超时设置（毫秒）
     */
    private final static int HTTP_CONNECT_TIMEOUT = 5000;

    /**
     * HTTP链接读取超时设置（毫秒）
     */
    private final static int HTTP_SO_TIMEOUT = 30000;

    /**
     * ENCODING
     */
    public final static String ENCODING = "UTF-8";

    /**
     * 创建请求client
     *
     * @param url
     *            请求地址
     * @return client
     */
    private static CloseableHttpClient createClient(String url) {
        // 如果是https地址则进行SSL设置
        if (url.startsWith("https")) {
            try {
                SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                    // 信任所有
                    @Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }
                }).build();
                SSLConnectionSocketFactory csFactory = new SSLConnectionSocketFactory(sslContext);
                return HttpClients.custom().setSSLSocketFactory(csFactory).build();
            } catch (KeyManagementException e) {
            } catch (NoSuchAlgorithmException e) {
            } catch (KeyStoreException e) {
            }
        }
        // 普通请求
        return HttpClients.createDefault();
    }

    /**
     * 创建配置
     *
     * @return 配置
     */
    private static RequestConfig createConfig() {
        return RequestConfig.custom().setConnectionRequestTimeout(HTTP_CONNECTION_REQUEST_TIMEOUT).setConnectTimeout(HTTP_CONNECT_TIMEOUT).setSocketTimeout(HTTP_SO_TIMEOUT).build();
    }

    /**
     * 获取结果
     *
     * @param response
     *            返回对象
     * @param encoding
     *            编码格式
     * @return
     */
    private static Result getResult(HttpResponse response, String encoding) {
        Result result = null;

        if (response != null && response.getStatusLine() != null) {
            result = new Result();
            result.setStatusCode(response.getStatusLine().getStatusCode());
            result.setHeaders(response.getAllHeaders());
            try {
                result.setRespString(EntityUtils.toString(response.getEntity(), encoding));
            } catch (ParseException e) {
            } catch (IOException e) {
            }
        }

        return result;
    }

    /**
     * 发送GET请求
     *
     * @param url
     *            请求地址
     * @param headerMap
     *            请求头键值对
     * @param encoding
     *            编码格式
     * @return 字符串结果
     */
    public static Result get(String url, Map<String, String> headerMap, String encoding) {
        Result result = null;

        // 参数检查
        if (StringUtils.isBlank(url)) {
            return result;
        }
        if (StringUtils.isBlank(encoding)) {
            encoding = ENCODING;
        }

        CloseableHttpClient client = null;
        HttpGet method = null;
        try {
            // 创建client
            client = createClient(url);
            // 创建请求方法
            method = new HttpGet(url);
            method.setConfig(createConfig());
            // 设置请求头
            if (headerMap!=null) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    method.setHeader(entry.getKey(), entry.getValue());
                }
            }
            // 执行请求
            HttpResponse response = client.execute(method);
            // 获取结果
            result = getResult(response, encoding);
        } catch (Exception e) {
            if (method != null) {
                method.abort();
            }
            throw new RuntimeException(e);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                }
            }
        }

        return result;
    }

    /**
     * 发送字符串参数的POST请求
     *
     * @param url
     *            请求地址
     * @param headerMap
     *            请求头键值对
     * @param encoding
     *            编码格式
     * @param param
     *            字符串参数
     * @return 字符串结果
     */
    public static Result post(String url, Map<String, String> headerMap, String encoding, String param) {
        Result result = null;

        // 参数检查
        if (StringUtils.isBlank(url)) {
            return result;
        }
        if (StringUtils.isBlank(encoding)) {
            encoding = ENCODING;
        }

        CloseableHttpClient client = null;
        HttpPost method = null;
        try {
            // 创建client
            client = createClient(url);
            // 创建请求方法
            method = new HttpPost(url);
            method.setConfig(createConfig());
            // 设置请求头
            if (headerMap!=null) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    method.setHeader(entry.getKey(), entry.getValue());
                }
            }
            // 配置参数
            if (StringUtils.isNotBlank(param)) {
                method.setEntity(new StringEntity(param, encoding));
            }
            // 执行请求
            HttpResponse response = client.execute(method);
            // 获取结果
            result = getResult(response, encoding);
        } catch (Exception e) {
            if (method != null) {
                method.abort();
            }
            throw new RuntimeException(e);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                }
            }
        }

        return result;
    }

    /**
     * 发送字符串参数键值对的POST请求
     *
     * @param url
     *            请求地址
     * @param headerMap
     *            请求头键值对
     * @param encoding
     *            编码格式
     * @param paramMap
     *            字符串参数键值对
     * @return 字符串结果
     */
    public static Result post(String url, Map<String, String> headerMap, String encoding, Map<String, String> paramMap) {
        Result result = null;

        // 参数检查
        if (StringUtils.isBlank(url)) {
            return result;
        }
        if (StringUtils.isBlank(encoding)) {
            encoding = ENCODING;
        }

        CloseableHttpClient client = null;
        HttpPost method = null;
        try {
            // 创建client
            client = createClient(url);
            // 创建请求方法
            method = new HttpPost(url);
            method.setConfig(createConfig());
            // 设置请求头
            if (headerMap!=null) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    method.setHeader(entry.getKey(), entry.getValue());
                }
            }
            // 配置参数
            if (paramMap!=null) {
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                method.setEntity(new UrlEncodedFormEntity(params, encoding));
            }
            // 执行请求
            HttpResponse response = client.execute(method);
            // 获取结果
            result = getResult(response, encoding);
        } catch (Exception e) {
            if (method != null) {
                method.abort();
            }
            throw new RuntimeException(e);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                }
            }
        }

        return result;
    }

    /**
     * 发送支持字符串参数键值对和文件上传的POST请求
     *
     * @param url
     *            请求地址
     * @param headerMap
     *            请求头键值对
     * @param encoding
     *            编码格式
     * @param paramMap
     *            字符串参数键值对
     * @param fileMap
     *            上传文件键值对
     * @return 字符串结果
     */
    public static Result post(String url, Map<String, String> headerMap, String encoding, Map<String, String> paramMap, Map<String, File> fileMap) {
        Result result = null;

        // 参数检查
        if (StringUtils.isBlank(url)) {
            return result;
        }
        if (StringUtils.isBlank(encoding)) {
            encoding = ENCODING;
        }

        CloseableHttpClient client = null;
        HttpPost method = null;
        try {
            // 创建client
            client = createClient(url);
            // 创建请求方法
            method = new HttpPost(url);
            method.setConfig(createConfig());
            // 设置请求头
            if (headerMap!=null) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    method.setHeader(entry.getKey(), entry.getValue());
                }
            }
            // 配置参数
            if (paramMap!=null || fileMap!=null) {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                if (paramMap!=null) {
                    for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                        builder.addPart(entry.getKey(), new StringBody(entry.getValue(), ContentType.create("text/plain", encoding)));
                    }
                }
                if (paramMap!=null) {
                    for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                        builder.addPart(entry.getKey(), new FileBody(entry.getValue(), ContentType.create("multipart/form-data", encoding)));
                    }
                }
                method.setEntity(builder.build());
            }
            // 执行请求
            HttpResponse response = client.execute(method);
            // 获取结果
            result = getResult(response, encoding);
        } catch (Exception e) {
            if (method != null) {
                method.abort();
            }
            throw new RuntimeException(e);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                }
            }
        }

        return result;
    }

    /**
     * 获取客户端IP地址
     *
     * @param request
     * @return
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isBlank(ip)) {
            ip = "";
        } else if ("0:0:0:0:0:0:0:1".equals(ip) || "localhost".equalsIgnoreCase(ip)) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException unknownhostexception) {
            }
        }

        return ip;
    }

    /**
     * 发送GET请求获取返回数据字节数组
     *
     * @param url
     *            请求地址
     * @param headerMap
     *            请求头键值对
     * @return 数据字节数组
     */
    public static Result getByteArray(String url, Map<String, String> headerMap) {
        Result result = null;

        // 参数检查
        if (StringUtils.isBlank(url)) {
            return result;
        }

        CloseableHttpClient client = null;
        HttpGet method = null;
        try {
            // 创建client
            client = createClient(url);
            // 创建请求方法
            method = new HttpGet(url);
            method.setConfig(createConfig());
            // 设置请求头
            if (headerMap!=null) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    method.setHeader(entry.getKey(), entry.getValue());
                }
            }
            // 执行请求
            HttpResponse response = client.execute(method);
            // 获取结果
            if (response != null && response.getStatusLine() != null) {
                result = new Result();
                result.setStatusCode(response.getStatusLine().getStatusCode());
                result.setHeaders(response.getAllHeaders());
                try {
                    result.setRespByteArray(EntityUtils.toByteArray(response.getEntity()));
                } catch (IOException e) {
                }
            }
        } catch (Exception e) {
            if (method != null) {
                method.abort();
            }
            throw new RuntimeException(e);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                }
            }
        }

        return result;
    }

    /**
     * 返回请求结果的内部类
     *
     * @author mervinSun
     *
     */
    public static class Result implements Serializable {

        private static final long serialVersionUID = -2331710763657101179L;
        /**
         * HTTP状态码
         */
        private int statusCode;
        /**
         * 返回字符串内容
         */
        private String respString;
        /**
         * 返回字节数组内容
         */
        private byte[] respByteArray;
        /**
         * 返回全部Header
         */
        private Header[] headers;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getRespString() {
            return respString;
        }

        public void setRespString(String respString) {
            this.respString = respString;
        }

        public byte[] getRespByteArray() {
            return respByteArray;
        }

        public void setRespByteArray(byte[] respByteArray) {
            this.respByteArray = respByteArray;
        }

        public Header[] getHeaders() {
            return headers;
        }

        public void setHeaders(Header[] headers) {
            this.headers = headers;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

    }

}
