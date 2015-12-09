package com.focosee.qingshow.httpapi.response.error;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.activity.LaunchActivity;
import com.focosee.qingshow.activity.U07RegisterActivity;
import com.focosee.qingshow.activity.U19LoginGuideActivity;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.util.ValueUtil;
import de.greenrobot.event.EventBus;

/**
 * Created by zenan on 1/2/15.
 */
public class ErrorHandler {

    private static final String TAG = ErrorHandler.class.getSimpleName();

    public static void handle(Context context, String errorCode){
        handle(context, Integer.parseInt(errorCode));
    }

    public static void handle(Context context, int errorCode) {
        switch (errorCode) {
            case ErrorCode.ServerError:
                ToastUtil.showShortToast(context.getApplicationContext(), "请重试");
                break;
            case ErrorCode.IncorrectMailOrPassword:
                ToastUtil.showShortToast(context.getApplicationContext(), "账号或密码错误");
                break;
            case ErrorCode.SessionExpired:
                Log.d(TAG, "SessionExpired");
                break;
            case ErrorCode.ShowNotExist:
                Log.d(TAG, "ShowNotExist");
                break;
            case ErrorCode.ItemNotExist:
                Log.d(TAG, "ItemNotExist");
                break;
            case ErrorCode.PeopleNotExist:
                Log.d(TAG, "PeopleNotExist");
                break;
            case ErrorCode.InvalidEmail:
                ToastUtil.showShortToast(context.getApplicationContext(), "不合法的邮箱");
                break;
            case ErrorCode.NotEnoughParam:
                Log.d(TAG, "参数不够");
                break;
            case ErrorCode.PagingNotExist:
                Log.d(TAG, "没有更多数据了");
                break;
            case ErrorCode.EmailAlreadyExist:
                ToastUtil.showShortToast(context.getApplicationContext(), "账号已存在");
                break;
            case ErrorCode.AlreadyLikeShow:
                Log.d(TAG, "AlreadyLikeShow");
                break;
            case ErrorCode.NeedLogin:
                Class _class = U19LoginGuideActivity.class;
                if(!TextUtils.isEmpty(QSModel.INSTANCE.getUserId())){
                    _class = LaunchActivity.class;
                }
                Intent intent = new Intent(context, _class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            case ErrorCode.AlreadyFollowPeople:
                Log.d(TAG, "AlreadyFollowPeople");
                break;
            case ErrorCode.DidNotFollowPeople:
                Log.d(TAG, "DidNotFollowPeople");
                break;
            case ErrorCode.PItemNotExist:
                break;
            case ErrorCode.RequestValidationFail:
                Log.d(TAG, "RequestValidationFail");
                break;
            case ErrorCode.AlreadyRelated:
                Log.d(TAG, "AlreadyRelated");
                break;
            case ErrorCode.AlreadyUnrelated:
                Log.d(TAG, "AlreadyUnrelated");
                break;
            case ErrorCode.InvalidCurrentPassword:
                Log.d(TAG, "InvalidCurrentPassword");
                break;
            case ErrorCode.NoNetWork:
                ToastUtil.showShortToast(context.getApplicationContext(), "请检查网络");
                break;
            case ErrorCode.MobileAlreadyExist:
                ToastUtil.showShortToast(context.getApplicationContext(), "手机已注册");
                break;
            case ErrorCode.MobileVerifyFailed:
                ToastUtil.showShortToast(context.getApplicationContext(), "验证失败");
                break;
            case ErrorCode.SMSlimitedSend:
                ToastUtil.showShortToast(context.getApplicationContext(), "验证失败");
                Log.d(ErrorHandler.class.getSimpleName(), "error: 获取验证码次数超过限制");
                break;
            case ErrorCode.FrequentlyRequest:
                ToastUtil.showShortToast(context.getApplicationContext(), "请求太过频繁");
                break;
            case ErrorCode.NickNameAlredyExist:
                ToastUtil.showShortToast(context.getApplicationContext(), "昵称已存在");
                break;
            case ErrorCode.UnSupportVersion:
                SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
                editor.putBoolean(ValueUtil.UPDATE_APP_FORCE, true);
                editor.commit();
                EventBus.getDefault().post(ValueUtil.UPDATE_APP_EVENT);
                break;
            case  ErrorCode.VolleyError:
                ToastUtil.showShortToast(context.getApplicationContext(), "网络请求失败");
                break;
        }
    }
}
