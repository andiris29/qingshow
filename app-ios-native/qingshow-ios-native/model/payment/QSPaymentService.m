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
#import "DataSigner.h"

#define ALIPAY_PARTNER @"2088301244798510"
#define ALIPAY_SELLER @"service@focosee.com"
#define ALIPAY_PRIVATE_KEY @"MIICXAIBAAKBgQDB/r4VcnLMRYodfK0vh8i37fL+VggeLnAhn7vxEvR1vOwxnaNXwjMJYk9abLe3YUPyeBTC07IXMlrjFakw367vNqj+E6vJUA1y4np6VQLbcl7wejyH4aOEe4ytrOabCVC2XsZ+BPfQoH6KtVWDghVN+18D4fD7FWYLYhCmgkNVrQIDAQABAoGAATdyw7mrBKLvAc5VW7XzSUwBuRybAm1yIJPa3uEqjU55ALqnWpaKMWXfb4a9BDZk8bFVF/+x3zlenov1Oqw8cZrOy2lNt30mBZ49rGZXHF5UDKndxIhyYQFX+h4/8+2VqFM0acjK5gjU6on9kEiBz5gONnXdU7mtO3gUUmrs7PUCQQDpmXUiPU82fRn4F71S66JsyREbVGjl3nPJQukZ+UJJWUHvLjnK0YSmNRr1692QVw6cLRzO3UzB8B41Zc7YKd/PAkEA1JkNjbFA4S/ymdqNqVMzJHf61FxZZlXpoNva2yLNg85m0YxwUqOaAStOyy7th8vjRSedhxAy/sbPFSDg2ng1wwJACs+QTTJbLSFjB0lJ+MFw9enkQciJRkIiR6kyEoKnn69izsfr4sgJhIumoMT2rwxoX6/yylwRhlQvgbcheH2PnwJBAKET4dAEh+rWgFKP5Btx/WLZQQPbgKTn3R7S1UyJXvtJzF9ir8v9Rvcxz/5kbPYhxe2kqVcnL+wXx9jzU0pUIC8CQDGei23BahmXvkNPF07JwGIoikoNZyUQZyd/NKpPiwMJTnyYrjp/rZEkwEUz5z3yJGLsuauqInWZDxyx32BUpUM="
#define ALIPAY_NOTIFY_URL @"http://chingshow.com/payment/alipay/callback"
#define ALIPAY_URL_SCHEMA @"alipay2088301244798510"

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

- (void)testAlipay
{
    AlipayOrder* order = [[AlipayOrder alloc] init];
    order.partner = ALIPAY_PARTNER;
    order.seller = ALIPAY_SELLER;
    order.tradeNO = [self generateTradeNO];
    order.productName = @"testProductName";
    order.productDescription = @"testDesc";
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

- (NSString *)generateTradeNO
{
#warning TODO get trade no from server
    static int kNumber = 15;
    
    NSString *sourceStr = @"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    NSMutableString *resultStr = [[NSMutableString alloc] init];
    srand(time(0));
    for (int i = 0; i < kNumber; i++)
    {
        unsigned index = rand() % [sourceStr length];
        NSString *oneStr = [sourceStr substringWithRange:NSMakeRange(index, 1)];
        [resultStr appendString:oneStr];
    }
    return resultStr;
}
@end
