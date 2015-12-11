//
//  QSPaymentService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/26/15.
//  Copyright (c) 2015 QS. All rights reserved.
//


#import "QSPaymentService.h"
#import "QSTradeUtil.h"
#import "QSShareService.h"
#import "AlipayOrder.h"
#import "APAuthV2Info.h"
#import <AlipaySDK/AlipaySDK.h>
#import "WXApi.h"
#import "DataSigner.h"
#import "QSSharePlatformConst.h"
#import "NSString+MKNetworkKitAdditions.h"
#import "QSTradeUtil.h"
#import "QSEntityUtil.h"
#import "QSItemUtil.h"
#import "QSPaymentConst.h"
#import "QSNetworkKit.h"
#import "QSNetworkEngine+ShareService.h"
#import "QSShareUtil.h"

#define ALIPAY_PARTNER @"2088301244798510"
#define ALIPAY_SELLER @"service@focosee.com"
#define ALIPAY_PRIVATE_KEY @"MIICXAIBAAKBgQDB/r4VcnLMRYodfK0vh8i37fL+VggeLnAhn7vxEvR1vOwxnaNXwjMJYk9abLe3YUPyeBTC07IXMlrjFakw367vNqj+E6vJUA1y4np6VQLbcl7wejyH4aOEe4ytrOabCVC2XsZ+BPfQoH6KtVWDghVN+18D4fD7FWYLYhCmgkNVrQIDAQABAoGAATdyw7mrBKLvAc5VW7XzSUwBuRybAm1yIJPa3uEqjU55ALqnWpaKMWXfb4a9BDZk8bFVF/+x3zlenov1Oqw8cZrOy2lNt30mBZ49rGZXHF5UDKndxIhyYQFX+h4/8+2VqFM0acjK5gjU6on9kEiBz5gONnXdU7mtO3gUUmrs7PUCQQDpmXUiPU82fRn4F71S66JsyREbVGjl3nPJQukZ+UJJWUHvLjnK0YSmNRr1692QVw6cLRzO3UzB8B41Zc7YKd/PAkEA1JkNjbFA4S/ymdqNqVMzJHf61FxZZlXpoNva2yLNg85m0YxwUqOaAStOyy7th8vjRSedhxAy/sbPFSDg2ng1wwJACs+QTTJbLSFjB0lJ+MFw9enkQciJRkIiR6kyEoKnn69izsfr4sgJhIumoMT2rwxoX6/yylwRhlQvgbcheH2PnwJBAKET4dAEh+rWgFKP5Btx/WLZQQPbgKTn3R7S1UyJXvtJzF9ir8v9Rvcxz/5kbPYhxe2kqVcnL+wXx9jzU0pUIC8CQDGei23BahmXvkNPF07JwGIoikoNZyUQZyd/NKpPiwMJTnyYrjp/rZEkwEUz5z3yJGLsuauqInWZDxyx32BUpUM="
#define ALIPAY_NOTIFY_URL [NSString stringWithFormat:@"%@/alipay/callback",PATH_SERVER_ADDR_PAY]
#define ALIPAY_URL_SCHEMA @"alipay2088301244798510"

#define WECHAT_SIGN_KEY @"1qaz2wsx3edc4rfv1234qwerasdfzxcv"
#define WECHAT_PARTNER_ID @"1234859302"

@interface QSPaymentService ()

@property (strong, nonatomic) VoidBlock succeedBlock;
@property (strong, nonatomic) ErrorBlock errorBlock;

@end

@implementation QSPaymentService

static NSString* s_paymentHost = nil;
- (NSString*)getPaymentHost {
    if (!s_paymentHost) {
        return @"";
    } else {
        return s_paymentHost;
    }
}
+ (void)configPaymentHost:(NSString*)paymentHost {
    s_paymentHost = paymentHost;
}

#pragma mark - Singleton
+ (QSPaymentService*)shareService
{
    static QSPaymentService* s_paymentService = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        s_paymentService = [[QSPaymentService alloc] init];
    });
    return s_paymentService;
}

#pragma mark - Life Cycle
- (instancetype)init {
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(invokePaymentSuccessCallback:) name:kPaymentSuccessNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(invokePaymentFailCallback:) name:kPaymentFailNotification object:nil];
    }
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - Payment Api
- (void)sharedForTrade:(NSDictionary*)tradeDict
             onSucceed:(DicBlock)succeedBlock
               onError:(ErrorBlock)errorBlock {
    //分享
    NSString* tradeId = [QSEntityUtil getIdOrEmptyStr:tradeDict];
    [SHARE_NW_ENGINE shareCreateTrade:tradeId onSucceed:^(NSDictionary *shareDic) {
        [[QSShareService shareService] shareWithWechatMoment:[QSShareUtil getShareTitle:shareDic] desc:[QSShareUtil getShareDesc:shareDic]imagePath:[QSShareUtil getShareIcon:shareDic] url:[QSShareUtil getshareUrl:shareDic] onSucceed:^{
            succeedBlock(tradeDict);
        } onError:errorBlock];
    } onError:errorBlock];
}

- (void)payForTrade:(NSDictionary*)tradeDict
          onSuccess:(VoidBlock)succeedBlock
            onError:(ErrorBlock)errorBlock
{
    self.succeedBlock = succeedBlock;
    self.errorBlock = errorBlock;
    
    NSString* prepayId = [QSTradeUtil getWechatPrepayId:tradeDict];
    NSDictionary* itemDict = [QSTradeUtil getItemSnapshot:tradeDict];
    NSMutableString* names = [@"" mutableCopy];
    [names appendString:[QSItemUtil getItemName:itemDict]];

    
    if (prepayId && prepayId.length) {
        //Pay with Wechat
        [self payWithWechatPrepayId:prepayId productName:names];

    } else {
        //Pay with Alipay
        [self payWithAliPayTrade:tradeDict productName:names];
    }
}


#pragma mark - Detail Payment
- (void)payWithAliPayTrade:(NSDictionary*)tradeDict
               productName:(NSString*)productName;
{
    NSString* tradeId = [QSEntityUtil getIdOrEmptyStr:tradeDict];
    AlipayOrder* order = [[AlipayOrder alloc] init];
    order.partner = ALIPAY_PARTNER;
    order.seller = ALIPAY_SELLER;
    order.tradeNO = tradeId;
    //item name
    order.productName = productName;
    order.productDescription = @"desc";
    order.amount = [QSTradeUtil getTotalFeeDesc:tradeDict];
    order.notifyURL = [NSString stringWithFormat:@"%@/alipay/callback",s_paymentHost];
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
            NSNumber* resultStatus = resultDic[@"resultStatus"];
            if (resultStatus.intValue == kAlipayPaymentSuccessCode) {
                [self invokePaymentSuccessCallback:nil];
            } else {
                [self invokePaymentFailCallback:nil];
            }
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
    NSString* str = [NSString stringWithFormat:@"appid=%@&noncestr=%@&package=Sign=WXPay&partnerid=%@&prepayid=%@&timestamp=%u&key=%@",kWechatAppID, request.nonceStr, request.partnerId, request.prepayId, (unsigned int)request.timeStamp, WECHAT_SIGN_KEY];
    request.sign = [[str md5] uppercaseString];

    [WXApi sendReq:request];
}

#pragma mark - Helper
- (void)invokePaymentSuccessCallback:(NSNotification*)notification
{
    if (self.succeedBlock) {
        self.succeedBlock();
        self.succeedBlock = nil;
        self.errorBlock = nil;
    }
}

- (void)invokePaymentFailCallback:(NSNotification*)notification
{
    if (self.errorBlock) {
        self.errorBlock(nil);
        self.succeedBlock = nil;
        self.errorBlock = nil;
    }
}

@end





