//
//  QSTableViewDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/7/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "QSTableViewBasicDelegateObj.h"
#import "QSModelListTableViewCell.h"

@protocol QSModelListTableViewDelegateObjDelegate <QSTableViewBasicDelegateObjDelegate>

@optional
- (void)clickModel:(NSDictionary*)model;
- (void)followBtnPressed:(NSDictionary*)model;
@end

typedef NS_ENUM(NSInteger, QSModelListTableViewDelegateObjType) {
    QSModelListTableViewDelegateObjTypeShowFollow = 0,
    QSModelListTableViewDelegateObjTypeHideFollow = 1
};


@interface QSModelListTableViewDelegateObj : QSTableViewBasicDelegateObj< QSModelListTableViewCellDelegate>

@property (weak, nonatomic) NSObject<QSModelListTableViewDelegateObjDelegate>* delegate;
@property (assign ,nonatomic) QSModelListTableViewDelegateObjType type;
@end
