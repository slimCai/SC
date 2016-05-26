package com.slim.su10.wxapi;


import android.app.Activity;
import android.os.Bundle;

import com.slim.su10.PreferencesUtils;
import com.slim.su10.R;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    @SuppressWarnings("FieldCanBeLocal")
    private IWXAPI mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApi = WXAPIFactory.createWXAPI(WXEntryActivity.this, getString(R.string.wx_appid), false);
        mApi.handleIntent(getIntent(), WXEntryActivity.this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
//                NormalToast.getInstance().show(WXEntryActivity.this, "分享成功");
//				String str = Constants.getWxPostUrl();
//				if (str == null || "".equals(str)) {
//					finish();
//					return;
//				}
//				OkHttpUtils.get().url(str).build().execute(new StringCallback() {
//					@Override
//					public void onError(Request request, Exception e) {
//
//					}
//
//					@Override
//					public void onResponse(String response) {
//						Constants.setWxPostUrl("");
//						PreferencesUtils.putString(WXEntryActivity.this, "refreshGift", "refresh");
//						WXEntryActivity.this.finish();
//					}
//				});
                PreferencesUtils.putString(this, "finish", "finish");
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                NormalToast.getInstance().show(WXEntryActivity.this, "您取消了分享");
                PreferencesUtils.putString(this, "finish", "finish");
                finish();
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
//                NormalToast.getInstance().show(WXEntryActivity.this, "分享错误");
                PreferencesUtils.putString(this, "finish", "finish");
                finish();
                break;
            default:
                break;
        }
    }
}