//
//  QSTableViewDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/7/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "QSTableViewBasicProvider.h"
#import "QSModelListTableViewCell.h"

@protocol QSModelListTableViewProviderDelegate <QSAbstractScrollProviderDelegate>

@optional
- (void)clickModel:(NSDictionary*)model;
- (void)followBtnPressed:(NSDictionary*)model;
@end

typedef NS_ENUM(NSInteger, QSModelListTableViewDelegateObjType) {
    QSModelListTableViewDelegateObjTypeShowFollow = 0,
    QSModelListTableViewDelegateObjTypeHideFollow = 1
};


@interface QSModelListTableViewProvider : QSTableViewBasicProvider< QSModelListTableViewCellDelegate>

@property (weak, nonatomic) NSObject<QSModelListTableViewProviderDelegate>* delegate;
@property (assign ,nonatomic) QSModelListTableViewDelegateObjType type;

@end
