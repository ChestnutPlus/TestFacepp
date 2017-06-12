package com.chestnut.TestFacePlusPlus;

import android.support.annotation.NonNull;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpParams;
import com.kymjs.rxvolley.rx.Result;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2017/6/12 12:05
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */
public class FacePPWebUtils {

    public static String KEY = "ILgUelGGWuS70ukmW-FE1lG-fFD3MZ5P";
    public static String SECRET = "8BdAt4CJHwF1BlW2q-FUj1X1jLM01tz4";
    public static int TIME_OUT = 3000;

    public static final String DETECT = "https://api-cn.faceplusplus.com/facepp/v3/detect";
    public static final String CREATE_FACE_SET = "https://api-cn.faceplusplus.com/facepp/v3/faceset/create";
    public static final String GET_FACE_SETS = "https://api-cn.faceplusplus.com/facepp/v3/faceset/getfacesets";
    public static final String ADD_FACE_TO_FACE_SETS = " https://api-cn.faceplusplus.com/facepp/v3/faceset/addface";
    public static final String SEARCH = "https://api-cn.faceplusplus.com/facepp/v3/search";
    public static final String GET_DETAIL = "https://api-cn.faceplusplus.com/facepp/v3/faceset/getdetail";

    /**
     * 添加人脸到某个集合中
     * @param outer_id  用户标识的Face Set
     * @return  packages
     */
    public static Observable<Result> addFaceToFaceSets(String outer_id,String face_tokens) {
        HttpParams params = new HttpParams();
        params.put("api_key",KEY);
        params.put("api_secret",SECRET);
        params.put("outer_id",outer_id);
        params.put("face_tokens",face_tokens);
        return new RxVolley.Builder()
                .url(ADD_FACE_TO_FACE_SETS)
                .timeout(TIME_OUT)
                .httpMethod(RxVolley.Method.POST)
                .params(params)
                .getResult();
    }

    /**
     * 获取 Face Sets detail
     * @param outer_id  outer_id
     * @return  packages
     */
    public static Observable<Result> getFaceSetDetail(String outer_id) {
        HttpParams params = new HttpParams();
        params.put("api_key",KEY);
        params.put("api_secret",SECRET);
        params.put("outer_id",outer_id);
        return new RxVolley.Builder()
                .url(GET_DETAIL)
                .timeout(TIME_OUT)
                .httpMethod(RxVolley.Method.POST)
                .params(params)
                .getResult();
    }

    /**
     * 检测人脸
     * @param imgURL    图片URL
     * @return  返回参对象
     */
    public static Observable<Result> detect(@NonNull String imgURL) {
        HttpParams params = new HttpParams();
        params.put("api_key",KEY);
        params.put("api_secret",SECRET);
        params.put("image_url",imgURL);
        return new RxVolley.Builder()
                .url(DETECT)
                .timeout(TIME_OUT)
                .httpMethod(RxVolley.Method.POST)
                .params(params)
                .getResult();
    }

    /**
     * 检测人脸
     * @param img_local_path    本地图片地址
     * @return  返回参对象
     */
    public static Observable<String> detect(@NonNull final File img_local_path) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                OkHttpClient okHttpClient = new OkHttpClient();
                MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),FacePPWebUtils.getBytesFromFile(img_local_path));
                body.addFormDataPart("api_key",FacePPWebUtils.KEY);
                body.addFormDataPart("api_secret",FacePPWebUtils.SECRET);
                body.addFormDataPart("image_file","fileName"+".mp3",requestBody);
                final Request request = new Request.Builder()
                        .url(FacePPWebUtils.DETECT)
                        .post(body.build())
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        subscriber.onNext(response.body().string());
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    /**
     * 创建FaceSet
     * @param name  全局唯一名称
     * @return  返回参数集
     */
    public static Observable<Result> createFaceSet(@NonNull String name) {
        HttpParams params = new HttpParams();
        params.put("api_key",KEY);
        params.put("api_secret",SECRET);
        params.put("force_merge",1);
        params.put("outer_id",name);
        return new RxVolley.Builder()
                .url(CREATE_FACE_SET)
                .timeout(TIME_OUT)
                .httpMethod(RxVolley.Method.POST)
                .params(params)
                .getResult();
    }

    /**
     * 查询人脸是否在FaceSet中
     * @param outer_id  指定某个名称的FaceSet
     * @param face_token   脸部Token
     * @return  RX
     */
    public static Observable<Result> searchFace(String outer_id, String face_token) {
        HttpParams params = new HttpParams();
        params.put("api_key",KEY);
        params.put("api_secret",SECRET);
        params.put("face_token",face_token);
        params.put("outer_id",outer_id);
        return new RxVolley.Builder()
                .url(SEARCH)
                .timeout(TIME_OUT)
                .httpMethod(RxVolley.Method.POST)
                .params(params)
                .getResult();
    }

    /**
     * 查找FaceSets
     * @return  packages
     */
    public static Observable<Result> getFaceSets() {
        HttpParams params = new HttpParams();
        params.put("api_key",KEY);
        params.put("api_secret",SECRET);
        return new RxVolley.Builder()
                .url(GET_FACE_SETS)
                .timeout(TIME_OUT)
                .httpMethod(RxVolley.Method.POST)
                .params(params)
                .getResult();
    }

    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }
}
