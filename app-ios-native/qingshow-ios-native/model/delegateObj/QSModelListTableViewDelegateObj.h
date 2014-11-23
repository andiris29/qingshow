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

@interface QSModelListTableViewDelegateObj : QSTableViewBasicDelegateObj< QSModelListTableViewCellDelegate>

@property (weak, nonatomic) NSObject<QSModelListTableViewDelegateObjDelegate>* delegate;
@end
