//
//  QSShowTableViewDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/14/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSTableViewBasicDelegateObj.h"
#import "QSBigImageTableViewCell.h"

@protocol QSBigImageTableViewDelegateObjDelegate <QSTableViewBasicDelegateObjDelegate>

- (void)didClickCell:(UITableViewCell*)cell ofData:(NSDictionary*)dict;

@end

@interface QSBigImageTableViewDelegateObj : QSTableViewBasicDelegateObj

@property (assign, nonatomic) QSBigImageTableViewCellType type;
@property (weak, nonatomic) NSObject<QSBigImageTableViewDelegateObjDelegate>* delegate;
@end
