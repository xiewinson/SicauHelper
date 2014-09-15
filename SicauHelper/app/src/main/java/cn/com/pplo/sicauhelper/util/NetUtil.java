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
    //错误监听器
    private static class ErrorListener implements Response.ErrorListener {

        private Context context;
        private Callbcak callbcak;

        public ErrorListener(Context context, Callbcak callbcak) {
            this.context = context;
            this.callbcak = callbcak;
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            callbcak.onFailure(volleyError.getMessage(), context);
        }
    }

    //发送get或者post请求
    public static void getOrPostRequest(Context context,
                                        final int method,
                                        String url,
                                        final Map<String, String> headers,
                                        final Map<String, String> params,
                                        Response.Listener<String> responseListner,
                                        Response.ErrorListener errorListner) {
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
        StringRequest stringRequest = new StringRequest(method, url, responseListner, errorListner) {
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
                if(isFirst == true){
                    cookie = response.headers.get("Set-Cookie");
                    isFirst = false;
                }


                Log.d("winson", "cookie:" + cookie );
//                return super.parseNetworkResponse(response);
                String result = "";
//                try {
//                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "GB2312"));
//                    String line = "";
//                    while ((line = br.readLine()) != null){
//                        Log.d("winson", "line--->  " + line );
//                    }
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
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


    //登录模块，在params中传入user和pwd
    public static void login(final Context context,
                             final Map<String, String> params,
                             final Callbcak callback) {
        try {
            //请求教务首页的HTML页面
            getOrPostRequest(context,
                    Request.Method.GET,
                    JiaowuConfig.JIAOWU_INDEX,
                    null,
                    null,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //模拟教务系统的js加密密码
                            Log.d("winson", "原密码：" + params.get("pwd"));
                            params.put("pwd", StringUtil.encodePswd(StringUtil.getDcode(s), params.get("pwd")));
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
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String s) {
                                            //Log.d("winson", "验证密码成功结果：" + s);
                                            callback.onSuccess(s);
                                        }
                                    }, new ErrorListener(context, callback));

                        }
                    }, new ErrorListener(context, callback));
        } catch (Exception e) {
            callback.onFailure(e.getMessage(), context);
        }
    }

    //获取成绩单
    public static void getScoreHtmlStr(final Context context, final Map<String, String> params, final Callbcak callbcak){
        login(context, params, new Callbcak() {
            @Override
            public void onSuccess(String result) {
                try {
                    Map<String, String> headerMap = new HashMap<String, String>();
                    headerMap.put("Cookie", cookie);
                    headerMap.put("Referer", "http://jiaowu.sicau.edu.cn/xuesheng/bangong/main/index1.asp");
                    headerMap.put("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
                    getOrPostRequest(context, Request.Method.POST, JiaowuConfig.JIAOWU_SCORE_NICE, headerMap, null, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            Log.d("winson", "成绩单：" + s);
                        }
                    }, new ErrorListener(context, callbcak));
                }catch (Exception e){
                    callbcak.onFailure(e.getMessage(), context);
                }
            }
        });
    }


    //回调接口
    public abstract static class Callbcak {
        public abstract void onSuccess(String result);

        public void onFailure(String message, Context context) {
            Log.d("winson", "错误结果：" + message);
            if (message != null && !message.equals("")) {
                if (message.contains("java.net.UnknownHostException")) {
                    UIUtil.showShortToast(context, "亲爱的，你的网络连接有问题，还用个毛啊～");
                }
            } else {
                UIUtil.showShortToast(context, "一定是教务网出了问题，一定是嗒～");
            }
        }

        ;
    }
}
