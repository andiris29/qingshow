//
//  QSPaymentConst.h
//  qingshow-ios-native
//
//  Created by wxy325 on 4/9/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#ifndef qingshow_ios_native_QSPaymentConst_h
#define qingshow_ios_native_QSPaymentConst_h

#define kPaymentSuccessNotification @"kPaymentSuccessNotification"
#define kPaymentFailNotification @"kPaymentFailNotification"

/*
 Alipay Code
 9000 支付成功
 8000 正在处理中
 4000 支付失败
 6001 中途取消
 6002 网络连接出错
 */

#define kAlipayPaymentSuccessCode 9000

/*
 Wechat Code
 0 支付成功
 */
#define kWechatPaymentSuccessCode 0
#endif
