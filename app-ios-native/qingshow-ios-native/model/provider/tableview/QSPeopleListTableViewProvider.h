//
//  QSTableViewDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/7/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "QSTableViewBasicProvider.h"
#import "QSPeopleListTableViewCell.h"

@protocol QSPeoplelListTableViewProviderDelegate <QSAbstractScrollProviderDelegate>

@optional
- (void)clickModel:(NSDictionary*)model;
- (void)followBtnPressed:(NSDictionary*)model;
@end

typedef NS_ENUM(NSInteger, QSModelListTableViewDelegateObjType) {
    QSModelListTableViewDelegateObjTypeShowFollow = 0,
    QSModelListTableViewDelegateObjTypeHideFollow = 1
};


@interface QSPeopleListTableViewProvider : QSTableViewBasicProvider< QSPeopleListTableViewCellDelegate>

@property (weak, nonatomic) NSObject<QSPeoplelListTableViewProviderDelegate>* delegate;
@property (assign ,nonatomic) QSModelListTableViewDelegateObjType type;

@end
