package com.chestnut.TestFacePlusPlus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chestnut.Common.ui.Toastc;
import com.chestnut.Common.utils.CameraUtils;
import com.chestnut.Common.utils.ImageUtils;
import com.chestnut.Common.utils.LogUtils;
import com.chestnut.TestFacePlusPlus.bean.DetectBean;
import com.google.gson.Gson;
import com.kymjs.rxvolley.rx.Result;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private boolean OpenLog = true;
    private Toastc toast;
    private List<DetectBean.FacesBean> facesBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toast = new Toastc(this, Toast.LENGTH_LONG);

        FacePPWebUtils.getFaceSetDetail("Test_FacePPWebUtils")
                .subscribe(new Action1<Result>() {
                    @Override
                    public void call(Result result) {
                        if (result.isSuccess()) {
                            String data = new String(result.data);
                            LogUtils.w(OpenLog,TAG,"getFaceSetDetail-success:"+data);
                        }
                        else {
                            LogUtils.e(OpenLog,TAG,"getFaceSetDetail-errorCode:"+result.errorCode+",error:"+result.error.getMessage());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.e(OpenLog,TAG,"getFaceSetDetail-error:"+throwable.getMessage()+","+throwable.toString());
                    }
                });

        //选图片
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraUtils.getHeadCropPhotoFromGallery(MainActivity.this);
            }
        });

        //检测脸
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/Temp.jpg");
                if (!file.exists()) {
                    toast.setText("文件不存在！").show();
                    return;
                }
                FacePPWebUtils.detect(file)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Func1<String, DetectBean>() {
                            @Override
                            public DetectBean call(String s) {
                                LogUtils.e(OpenLog, TAG, "detect-ok:" + s);
                                return new Gson().fromJson(s,DetectBean.class);
                            }
                        })
                        .subscribe(new Action1<DetectBean>() {
                            @Override
                            public void call(DetectBean detectBean) {
                                if (detectBean.faces!=null && detectBean.faces.size()>0) {
                                    toast.setText("识别到"+detectBean.faces.size()+"个人脸！").show();
                                    MainActivity.this.facesBeanList = detectBean.faces;
                                }
                                else {
                                    toast.setText("你可能选了张外星人自拍").show();
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                LogUtils.e(OpenLog, TAG, "detect-error:" + throwable.getMessage());
                            }
                        });

            }
        });

        //添加脸到脸集
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (facesBeanList==null || facesBeanList.size()<0) {
                    toast.setText("没脸！").show();
                    return;
                }
                Observable.from(facesBeanList)
                        .flatMap(new Func1<DetectBean.FacesBean, Observable<Result>>() {
                            @Override
                            public Observable<Result> call(DetectBean.FacesBean facesBean) {
                                LogUtils.i(OpenLog,TAG, facesBean.face_token);
                                return FacePPWebUtils.addFaceToFaceSets("Test_FacePPWebUtils",facesBean.face_token);
                            }
                        })
                        .subscribe(new Action1<Result>() {
                            @Override
                            public void call(Result result) {
                                if (result.isSuccess()) {
                                    String data = new String(result.data);
                                    LogUtils.w(OpenLog,TAG,"addFace-success:"+data);
                                }
                                else {
                                    LogUtils.e(OpenLog,TAG,"addFace-errorCode:"+result.errorCode+",error:"+result.error.getMessage());
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                toast.setText("添加人脸失败："+throwable.getMessage()).show();
                                LogUtils.e(OpenLog,TAG,"addFace-error:"+throwable.getMessage()+","+throwable.toString());
                            }
                        });
            }
        });

        //查找脸
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/Temp.jpg");
                if (!file.exists()) {
                    toast.setText("文件不存在！").show();
                    return;
                }
                if (facesBeanList==null || facesBeanList.size()<0) {
                    toast.setText("没脸！").show();
                    return;
                }
                Observable.from(facesBeanList)
                        .delay(2, TimeUnit.SECONDS)
                        .flatMap(new Func1<DetectBean.FacesBean, Observable<Result>>() {
                            @Override
                            public Observable<Result> call(DetectBean.FacesBean facesBean) {
                                return FacePPWebUtils.searchFace("Test_FacePPWebUtils",facesBean.face_token);
                            }
                        })
                        .map(new Func1<Result, String>() {
                            @Override
                            public String call(Result result) {
                                if (result.isSuccess()) {
                                    String data = new String(result.data);
                                    LogUtils.w(OpenLog,TAG,"查找脸-success:"+data);
                                    return data;
                                }
                                else {
                                    LogUtils.e(OpenLog,TAG,"查找脸-errorCode:"+result.errorCode+",error:"+result.error.getMessage());
                                    return null;
                                }
                            }
                        })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {

                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                LogUtils.w(OpenLog,TAG,"查找脸-error:"+throwable.getMessage());
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = CameraUtils.getBitmapFromCG(this,requestCode,0,data,1,1,300,300,true,this.getCacheDir()+"/cutHeadPhotoTemp.jpg");
        ImageUtils.save(bitmap, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/Temp.jpg", Bitmap.CompressFormat.JPEG);
        ((ImageView)findViewById(R.id.imageView)).setImageBitmap(bitmap);
    }
}
