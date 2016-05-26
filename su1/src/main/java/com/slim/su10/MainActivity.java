package com.slim.su10;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class MainActivity extends Activity {
    private IWXAPI mWxApi;

    @Override
    protected void onResume() {
        super.onResume();
        if ("finish".equals(PreferencesUtils.getString(this, "finish", ""))) {
            PreferencesUtils.putString(this, "finish", "");
            MainActivity.this.finish();
        }
    }

    private void initCrash() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent() == null) {
            Log.e("MainActivity", "no intent");
            finish();
            return;
        }
        initCrash();
        mWxApi = WXAPIFactory.createWXAPI(this, getString(R.string.wx_appid));
        mWxApi.registerApp(getString(R.string.wx_appid));
        wxShare(getIntent().getIntExtra("type", -1),
                getIntent().getStringExtra("shareTitle"),
                getIntent().getStringExtra("shareContent"),
                getIntent().getStringExtra("shareImg"),
                getIntent().getStringExtra("shareUrl")
        );
//        wxShare(1,"朝鲜核爆不算啥 看美苏当年核试验有多疯狂","1月6日上午11点30分，朝鲜宣布该国进行了一次成功的氢弹试验，全面提升了核武器水平。中国地震台网消息，1月6日上午9时30分，朝鲜咸镜北道境内发生4.9级地震。震中位于北纬41.30度",
//                "http://wx.redhf.com/uploads/big/20160523/1bsguubisqn.jpg","http://wx.hongbao100.cn/share/archive/index?uid=881dd1ccc853dbe5&aid=30502&url=http%3A%2F%2Fwx.redhf.com%2Fhtml%2Fmuying%2F2016%2F0523%2F30502.html&title=%E4%BD%A0%E4%BB%A5%E4%B8%BA%E4%B8%8D%E8%AE%A9%E5%AD%A9%E5%AD%90%E7%9C%8B%E7%94%B5%E8%A7%86%E5%B0%B1%E4%B8%8D%E4%BC%9A%E8%BF%91%E8%A7%86%E4%BA%86%EF%BC%9F%E5%AE%B6%E9%95%BF%E8%A6%81%E5%B0%8F%E5%BF%83%E4%BC%A4%E5%AE%B3%E5%AE%9D%E5%AE%9D%E7%9C%BC%E7%9D%9B%E7%9A%84%E9%9A%90%E5%BD%A2%E6%9D%80%E6%89%8B%EF%BC%81%EF%BC%81&imgurl=http%3A%2F%2Fwx.redhf.com%2Fuploads%2Fbig%2F20160523%2F1bsguubisqn.jpg&money=0.02&version=1.0.2");
    }

    public void wxShare(final int type, final String shareTitle,
                        final String shareContent, final String shareImg, final String shareUrl) {
        Log.e("MainActivity", "" + shareTitle + "  " + shareContent + "  " + shareImg + "  " + shareUrl);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (type == -1)
                        return;

                    WXWebpageObject webpage = new WXWebpageObject();
                    webpage.webpageUrl = shareUrl;

                    final WXMediaMessage msg = new WXMediaMessage(webpage);
                    msg.title = shareTitle;
                    msg.description = shareContent;

                    Bitmap bmp = BitmapFactory.decodeStream(new URL(shareImg).openStream());
                    if (bmp != null) {
                        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                        bmp.recycle();
                        msg.thumbData = bmpToByteArray(thumbBmp, true);
                    }

                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = String.valueOf(System.currentTimeMillis());
                    req.message = msg;
                    req.scene = (type == 0) ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
                    mWxApi.sendReq(req);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 30, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
