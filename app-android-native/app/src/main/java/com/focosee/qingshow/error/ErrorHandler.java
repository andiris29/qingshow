package com.focosee.qingshow.error;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by zenan on 1/2/15.
 */
public class ErrorHandler {

    public static void handle(Context context, int errorCode) {
        Toast.makeText(context, "出错了，请重试", Toast.LENGTH_SHORT).show();

        switch (errorCode) {
            case ErrorCode.ServerError:

                break;
            case ErrorCode.IncorrectMailOrPassword:
                Toast.makeText(context, "账号或密码错误", Toast.LENGTH_LONG).show();
                break;
            case ErrorCode.SessionExpired:

                break;
            case ErrorCode.ShowNotExist:

                break;
            case ErrorCode.ItemNotExist:

                break;
            case ErrorCode.PeopleNotExist:

                break;
            case ErrorCode.BrandNotExist:

                break;
            case ErrorCode.InvalidEmail:

                break;
            case ErrorCode.NotEnoughParam:

                break;
            case ErrorCode.PagingNotExist:

                break;
            case ErrorCode.EmailAlreadyExist:

                break;
            case ErrorCode.AlreadyLikeShow:

                break;
            case ErrorCode.NeedLogin:
                Toast.makeText(context, "请登录", Toast.LENGTH_LONG).show();
                break;
            case ErrorCode.AlreadyFollowPeople:

                break;
            case ErrorCode.DidNotFollowPeople:

                break;
            case ErrorCode.AlreadyFollowBrand:

                break;
            case ErrorCode.DidNotFollowBrand:

                break;
            case ErrorCode.PItemNotExist:

                break;
            case ErrorCode.RequestValidationFail:

                break;
            case ErrorCode.AlreadyRelated:

                break;
            case ErrorCode.AlreadyUnrelated:

                break;
            case ErrorCode.InvalidCurrentPassword:

                break;
            default:

                break;
        }
    }
}
