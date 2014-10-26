package cn.com.pplo.sicauhelper.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.config.JiaowuConfig;

/**
 * Created by winson on 2014/9/13.
 */
public class NetUtil {

    public static String cookie = "";
    public static boolean isFirst = true;

    /**
     * 发送get或者post请求
     *
     * @param context
     * @param method
     * @param url
     * @param headers
     * @param params
     * @param netCallbcak
     */
    public static void getOrPostRequest(Context context,
                                        final int method,
                                        String url,
                                        final Map<String, String> headers,
                                        final Map<String, String> params,
                                        NetCallback netCallbcak) {
        RequestQueue requestQueue = SicauHelperApplication.getRequestQueue(context);

        //若为get请求则将参数加入到url之中
        if (method == Request.Method.GET && params != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            sb.append("?");
            Set<Map.Entry<String, String>> set = params.entrySet();
            Iterator<Map.Entry<String, String>> iterator = set.iterator();
            boolean isAddAnd = false;
            do {
                if (isAddAnd == true) {
                    sb.append("&");
                }
                Map.Entry<String, String> next = iterator.next();
                sb.append(next.getKey() + "=" + next.getValue());
                isAddAnd = true;
            }
            while (iterator.hasNext());
            url = sb.toString();
        }
        StringRequest stringRequest = new StringRequest(method, url, netCallbcak, netCallbcak) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers != null) {
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }

            //若为post请求则在此添加参数
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (params != null && method == Method.POST) {
                    return params;
                } else {
                    return super.getParams();
                }
            }

            @Override
            public String getUrl() {
                return super.getUrl();
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                //取得cookie
                if (isFirst == true) {
                    cookie = response.headers.get("Set-Cookie");
                    isFirst = false;
                }
                Log.d("winson", "cookie:" + cookie);
                String result = "";
                try {
                    result = new String(response.data, "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        requestQueue.add(stringRequest);
        requestQueue.start();
    }


    /**
     * 登录模块，在params中传入user和pwd
     *
     * @param context
     * @param params
     * @param callback
     */
    public static void login(final Context context,
                             final Map<String, String> params,
                             final NetCallback callback) {
        try {
            //请求教务首页的HTML页面
            getOrPostRequest(context,
                    Request.Method.GET,
                    JiaowuConfig.JIAOWU_INDEX,
                    null,
                    null,
                    new NetCallback(context) {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            super.onErrorResponse(volleyError);

                        }

                        @Override
                        public void onSuccess(String result) {
                            //模拟教务系统的js加密密码
                            Log.d("winson", "原密码：" + params.get("pwd"));
                            params.put("pwd", StringUtil.encodePswd(StringUtil.getDcode(result), params.get("pwd")));
                            Log.d("winson", "加密密码：" + params.get("pwd"));

                            //新的header
                            Map<String, String> newHeader = new HashMap<String, String>();
                            //设置referer
                            newHeader.put("Referer", "http://jiaowu.sicau.edu.cn/web/web/web/index.asp");
                            //必须设置cookie
                            newHeader.put("Cookie", cookie);
                            //                            newHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36");
                            //验证密码
                            getOrPostRequest(context,
                                    Request.Method.POST,
                                    JiaowuConfig.JIAOWU_CHECK,
                                    newHeader,
                                    params,
                                    callback);
                        }
                    });
        } catch (Exception e) {
            UIUtil.showShortToast(context, "呵呵，出了点我也不知道的什么错误");
        }
        clearCookie();
    }

    //每次一系列请求完成后清除cookie
    private static void clearCookie() {
        cookie = "";
        isFirst = true;
    }

    /**
     * 获取成绩单
     *
     * @param context
     * @param params
     * @param callback
     */
    public static void getScoreHtmlStr(final Context context, final Map<String, String> params, final NetCallback callback) {
        login(context, params, new NetCallback(context) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                callback.onErrorResponse(volleyError);
            }

            @Override
            public void onSuccess(String result) {
                try {
                    Map<String, String> headerMap = new HashMap<String, String>();
                    headerMap.put("Cookie", cookie);
                    headerMap.put("Referer", "http://jiaowu.sicau.edu.cn/xuesheng/bangong/main/index1.asp");
                    headerMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
                    getOrPostRequest(context, Request.Method.POST, JiaowuConfig.JIAOWU_SCORE_NICE, headerMap, null, callback);
                } catch (Exception e) {
                    UIUtil.showShortToast(context, "呵呵，出了点我也不知道的什么错误～");
                }
            }
        });
        clearCookie();
    }

    /**
     * 获取课程表
     * @param context
     * @param params
     * @param callback
     */
    public static void getCourseHtmlStr(final Context context, final Map<String, String> params, final NetCallback callback) {
        login(context, params, new NetCallback(context) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                callback.onErrorResponse(volleyError);
            }

            @Override
            public void onSuccess(String result) {
                try {
                    Map<String, String> headerMap = new HashMap<String, String>();
                    headerMap.put("Cookie", cookie);
                    headerMap.put("Referer", "http://jiaowu.sicau.edu.cn/xuesheng/bangong/main/index1.asp");
                    headerMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
                    getOrPostRequest(context, Request.Method.POST, JiaowuConfig.JIAOWU_COURSE, headerMap, null, callback);
                } catch (Exception e) {
                    UIUtil.showShortToast(context, "呵呵，出了点我也不知道的什么错误～");
                }
            }
        });
        clearCookie();
    }

    /**
     * 获取课程表
     * @param context
     * @param params
     * @param callback
     */
    public static void getCourse2HtmlStr(final Context context, final Map<String, String> params, final NetCallback callback) {
        login(context, params, new NetCallback(context) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                callback.onErrorResponse(volleyError);
            }

            @Override
            public void onSuccess(String result) {
                try {
                    Map<String, String> headerMap = new HashMap<String, String>();
                    headerMap.put("Cookie", cookie);
                    headerMap.put("Referer", "http://jiaowu.sicau.edu.cn/xuesheng/bangong/main/index1.asp");
                    headerMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
                    getOrPostRequest(context, Request.Method.POST, "http://jiaowu.sicau.edu.cn/xuesheng/gongxuan/gongxuan/xszhinan.asp?xueqi=2013-2014-1", headerMap, null, new NetCallback(context){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            super.onErrorResponse(volleyError);
                            callback.onErrorResponse(volleyError);
                        }

                        @Override
                        public void onSuccess(String result) {
                            try {
                                Map<String, String> headerMap = new HashMap<String, String>();
                                headerMap.put("Cookie", cookie);
                                headerMap.put("Referer", "http://jiaowu.sicau.edu.cn/xuesheng/gongxuan/gongxuan/xszhinan.asp?xueqi=2013-2014-1");
                                headerMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
                                getOrPostRequest(context, Request.Method.GET, JiaowuConfig.JIAOWU_COURSE_TEMP, headerMap, null, callback);
                            } catch (Exception e) {
                                UIUtil.showShortToast(context, "呵呵，出了点我也不知道的什么错误～");
                            }
                        }
                    });
                } catch (Exception e) {
                    UIUtil.showShortToast(context, "呵呵，出了点我也不知道的什么错误～");
                }
            }
        });
        clearCookie();
    }

    /**
     * 获取新闻列表
     * @param context
     * @param callback
     */
    public static void getNewsListHtmlStr(final Context context, final NetCallback callback){
        try {
            //请求教务首页的HTML页面
            getOrPostRequest(context,
                    Request.Method.GET,
                    JiaowuConfig.JIAOWU_INDEX,
                    null,
                    null,
                    new NetCallback(context) {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            super.onErrorResponse(volleyError);
                            callback.onErrorResponse(volleyError);
                        }

                        @Override
                        public void onSuccess(String result) {

                            //新的header
                            Map<String, String> newHeader = new HashMap<String, String>();
                            //设置referer
                            newHeader.put("Referer", "http://jiaowu.sicau.edu.cn/web/web/web/index.asp");
                            //必须设置cookie
                            newHeader.put("Cookie", cookie);
                            //                            newHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36");
                            getOrPostRequest(context,
                                    Request.Method.POST,
                                    JiaowuConfig.JIAOWU_NEWS_LIST,
                                    newHeader,
                                    null,
                                    callback);
                        }
                    });
        } catch (Exception e) {
            UIUtil.showShortToast(context, "呵呵，出了点我也不知道的什么错误");
        }
        clearCookie();
    }


    /**
     * 获取具体新闻内容
     * @param context
     * @param params
     * @param callback
     */
    public static void getNewsHtmlStr(final Context context, final Map<String, String> params, final NetCallback callback){
        try {
            //请求教务首页的HTML页面
            getOrPostRequest(context,
                    Request.Method.GET,
                    JiaowuConfig.JIAOWU_INDEX,
                    null,
                    null,
                    new NetCallback(context) {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            super.onErrorResponse(volleyError);
                            callback.onErrorResponse(volleyError);
                        }
                        @Override
                        public void onSuccess(String result) {

                            //新的header
                            Map<String, String> newHeader = new HashMap<String, String>();
                            //设置referer
                            newHeader.put("Referer", "http://jiaowu.sicau.edu.cn/web/web/web/index.asp");
                            //必须设置cookie
                            newHeader.put("Cookie", cookie);
                            getOrPostRequest(context,
                                    Request.Method.POST,
                                    JiaowuConfig.JIAOWU_NEWS_CONTENT,
                                    newHeader,
                                    params,
                                    callback);
                        }
                    });
        } catch (Exception e) {
            UIUtil.showShortToast(context, "呵呵，出了点我也不知道的什么错误");
        }
        clearCookie();
    }


    //回调接口
    public static abstract class NetCallback implements Response.Listener<String>, Response.ErrorListener {
        private Context context;

        public NetCallback(Context context) {
            this.context = context;
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Log.d("winson", "错误结果：" + volleyError.getMessage() + "===");
            try {
                if (volleyError != null && volleyError.getMessage() != null && !volleyError.getMessage().equals("")) {
                    if (volleyError.getMessage().contains("java.net.UnknownHostException")) {
                        UIUtil.showShortToast(context, "亲爱的，你的网络连接有问题，还用个毛啊～");
                    }
                } else {
                    UIUtil.showShortToast(context, "一定是教务网出了问题，一定是嗒～");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onResponse(String result) {
            if (result.contains("密码不对")) {
                UIUtil.showShortToast(context, "你连学号和密码都忘了吗～那么，拜拜～");
            } else if (result.contains("登录超时")) {
                UIUtil.showShortToast(context, "亲爱的，教务系统出问题了～");
            }
            else if(result.contains("您的电脑上所安装的个人防火墙软件拦截了你的验证信息")){
                UIUtil.showShortToast(context, "您的电脑上所安装的个人防火墙软件拦截了你的验证信息");
            }
            else {
                onSuccess(result);
            }
        }

        /**
         * 覆写此方法处理正确结果
         * @param result
         */
        protected abstract void onSuccess(String result);
    }
}
