//
//  QSTradeStatus.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/18/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

typedef NS_ENUM(NSInteger, QSTradeStatus) {
    QSTradeStatusUnpaid = 0,
    QSTradeStatusPaid = 1,
    QSTradeStatusDaigou = 2,
    QSTradeStatusFahuo = 3,
    QSTradeStatusQueren = 5,
    QSTradeStatusTuihuo = 7,
    QSTradeStatusTuihuoSuccess = 9,
    QSTradeStatusTuihuoFail = 10,
    QSTradeStatusHuanhuo = 11,
    QSTradeStatusHuanhuoSuccess = 12,
    QSTradeStatusHuanhuoFail = 13,
    QSTradeStatusHuanhuoFachu = 14,
    QSTradeStatusAutoQueren = 15,
    QSTradeStatusTuihuoAgain = 16,
    QSTradeStatusTuikuanSuccess = 17
};

NSString* QSTradeStatusToDesc(QSTradeStatus s);