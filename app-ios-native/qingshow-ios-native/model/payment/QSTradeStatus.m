//
//  QSTradeStatus.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/18/15.
//  Copyright (c) 2015 QS. All rights reserved.
//
#import "QSTradeStatus.h"

NSString* QSTradeStatusToDesc(QSTradeStatus s) {
    switch (s) {
        case QSTradeStatusUnpaid: {
            return @"等待付款";
        }
        case QSTradeStatusPaid:
        case QSTradeStatusDaigou: {
            return @"备货中";
        }
        case QSTradeStatusFahuo: {
            return @"已发货";
        }
        case QSTradeStatusQueren : {
            return @"交易成功";
        }
        case QSTradeStatusTuihuo :{
            return @"退货中";
        }
        case QSTradeStatusTuihuoSuccess : {
            return @"交易关闭";
        }
        case QSTradeStatusTuihuoFail : {
            return @"结束";
        }
        case QSTradeStatusHuanhuo : {
            return @"换货中";
        }
        case QSTradeStatusHuanhuoSuccess : {
            return @"交易成功";
        }
        case QSTradeStatusHuanhuoFail : {
            return @"结束";
        }
        case QSTradeStatusHuanhuoFachu : {
            return @"已发货";
        }
        case QSTradeStatusAutoQueren : {
            return @"交易成功";
        }
        case QSTradeStatusTuihuoAgain : {
            return @"已发货";
        }
        case QSTradeStatusTuikuanSuccess : {
            return @"交易关闭";
        }
        default:
            break;
    }
    return nil;
}
