//
//  QSPaymentService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/26/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSPaymentService.h"
#import "AlipayOrder.h"
#import "APAuthV2Info.h"
#import <AlipaySDK/AlipaySDK.h>
#import "WXApi.h"
#import "DataSigner.h"
#import "QSSharePlatformConst.h"
#import "NSString+MKNetworkKitAdditions.h"
#import "QSTradeUtil.h"
#import "QSOrderUtil.h"
#import "QSCommonUtil.h"
#import "QSItemUtil.h"

#define ALIPAY_PARTNER @"2088301244798510"
#define ALIPAY_SELLER @"service@focosee.com"
#define ALIPAY_PRIVATE_KEY @"MIICXAIBAAKBgQDB/r4VcnLMRYodfK0vh8i37fL+VggeLnAhn7vxEvR1vOwxnaNXwjMJYk9abLe3YUPyeBTC07IXMlrjFakw367vNqj+E6vJUA1y4np6VQLbcl7wejyH4aOEe4ytrOabCVC2XsZ+BPfQoH6KtVWDghVN+18D4fD7FWYLYhCmgkNVrQIDAQABAoGAATdyw7mrBKLvAc5VW7XzSUwBuRybAm1yIJPa3uEqjU55ALqnWpaKMWXfb4a9BDZk8bFVF/+x3zlenov1Oqw8cZrOy2lNt30mBZ49rGZXHF5UDKndxIhyYQFX+h4/8+2VqFM0acjK5gjU6on9kEiBz5gONnXdU7mtO3gUUmrs7PUCQQDpmXUiPU82fRn4F71S66JsyREbVGjl3nPJQukZ+UJJWUHvLjnK0YSmNRr1692QVw6cLRzO3UzB8B41Zc7YKd/PAkEA1JkNjbFA4S/ymdqNqVMzJHf61FxZZlXpoNva2yLNg85m0YxwUqOaAStOyy7th8vjRSedhxAy/sbPFSDg2ng1wwJACs+QTTJbLSFjB0lJ+MFw9enkQciJRkIiR6kyEoKnn69izsfr4sgJhIumoMT2rwxoX6/yylwRhlQvgbcheH2PnwJBAKET4dAEh+rWgFKP5Btx/WLZQQPbgKTn3R7S1UyJXvtJzF9ir8v9Rvcxz/5kbPYhxe2kqVcnL+wXx9jzU0pUIC8CQDGei23BahmXvkNPF07JwGIoikoNZyUQZyd/NKpPiwMJTnyYrjp/rZEkwEUz5z3yJGLsuauqInWZDxyx32BUpUM="
#define ALIPAY_NOTIFY_URL @"http://chingshow.com/payment/alipay/callback"
#define ALIPAY_URL_SCHEMA @"alipay2088301244798510"

#define WECHAT_SIGN_KEY @"1qaz2wsx3edc4rfv1234qwerasdfzxcv"
#define WECHAT_PARTNER_ID @"1234859302"

@implementation QSPaymentService

+ (QSPaymentService*)shareService
{
    static QSPaymentService* s_paymentService = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        s_paymentService = [[QSPaymentService alloc] init];
        
    });
    return s_paymentService;
}

- (void)payForTrade:(NSDictionary*)tradeDict
{
    NSString* prepayId = [QSTradeUtil getWechatPrepayId:tradeDict];
    
    
    NSString* tradeId = [QSCommonUtil getIdOrEmptyStr:tradeDict];
    NSArray* orderArray = [QSTradeUtil getOrderArray:tradeDict];
    NSMutableString* names = [@"" mutableCopy];
    for (NSDictionary* orderDict in orderArray) {
        NSDictionary* itemDict = [QSOrderUtil getItemSnapshot:orderDict];
        [names appendString:[QSItemUtil getItemName:itemDict]];
    }
    
    if (prepayId && prepayId.length) {
        //Pay with Wechat
        [self payWithWechatPrepayId:prepayId productName:names];

    } else {
        //Pay with Alipay
        [self payWithAliPayTradeId:tradeId productName:names];
    }
}

- (void)payWithAliPayTradeId:(NSString*)tradeId
                 productName:(NSString*)productName
{
    AlipayOrder* order = [[AlipayOrder alloc] init];
    order.partner = ALIPAY_PARTNER;
    order.seller = ALIPAY_SELLER;
    order.tradeNO = tradeId;
    //item name
    order.productName = productName;
    order.productDescription = @"desc";
    order.amount = @"0.01";
    order.notifyURL = ALIPAY_NOTIFY_URL;
    order.service = @"mobile.securitypay.pay";
    order.paymentType = @"1";
    order.inputCharset = @"utf-8";
    order.itBPay = @"30m";
    order.showUrl = @"m.alipay.com";
    NSString *orderSpec = [order description];
    id<DataSigner> signer = CreateRSADataSigner(ALIPAY_PRIVATE_KEY);
    NSString *signedString = [signer signString:orderSpec];
    NSString *orderString = nil;
    if (signedString != nil) {
        orderString = [NSString stringWithFormat:@"%@&sign=\"%@\"&sign_type=\"%@\"",
                       orderSpec, signedString, @"RSA"];
        
        [[AlipaySDK defaultService] payOrder:orderString fromScheme:ALIPAY_URL_SCHEMA callback:^(NSDictionary *resultDic) {
            NSLog(@"reslut = %@",resultDic);
        }];
    }
}

- (void)payWithWechatPrepayId:(NSString*)prepayId
                  productName:(NSString*)productName;
{
    PayReq *request = [[PayReq alloc] init];
    request.partnerId = WECHAT_PARTNER_ID;
    request.prepayId= prepayId;
    request.package = @"Sign=WXPay";
    request.nonceStr= @(random()).stringValue;
    request.timeStamp= [[NSDate date] timeIntervalSince1970];
    NSString* str = [NSString stringWithFormat:@"appid=%@&noncestr=%@&package=Sign=WXPay&partnerid=%@&prepayid=%@&timestamp=%ld&key=%@",kWechatAppID, request.nonceStr, request.partnerId, request.prepayId, request.timeStamp, WECHAT_SIGN_KEY];
    request.sign= [[str md5] uppercaseString];

    [WXApi sendReq:request];
}

@end





