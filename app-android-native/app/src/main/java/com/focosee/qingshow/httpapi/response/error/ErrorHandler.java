package com.focosee.qingshow.httpapi.response.error;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by zenan on 1/2/15.
 */
public class ErrorHandler {

    public static void handle(Context context, int errorCode) {
        switch (errorCode) {
            case ErrorCode.ServerError:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.IncorrectMailOrPassword:
                Toast.makeText(context, "账号或密码错误", Toast.LENGTH_LONG).show();
                break;
            case ErrorCode.SessionExpired:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.ShowNotExist:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.ItemNotExist:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.PeopleNotExist:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.BrandNotExist:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.InvalidEmail:
                Toast.makeText(context, "不合法的邮箱", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.NotEnoughParam:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.PagingNotExist:
                Toast.makeText(context, "页面不存在", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.EmailAlreadyExist:
                Toast.makeText(context, "账号已存在", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.AlreadyLikeShow:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.NeedLogin:
                Toast.makeText(context, "需要登录", Toast.LENGTH_LONG).show();
                break;
            case ErrorCode.AlreadyFollowPeople:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.DidNotFollowPeople:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.AlreadyFollowBrand:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.DidNotFollowBrand:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.PItemNotExist:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.RequestValidationFail:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.AlreadyRelated:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.AlreadyUnrelated:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.InvalidCurrentPassword:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
