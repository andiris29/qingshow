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

@optional
- (void)didClickCell:(UITableViewCell*)cell ofData:(NSDictionary*)dict;

- (void)clickCommentOfDict:(NSDictionary*)dict;
- (void)clickShareOfDict:(NSDictionary*)dict;
- (void)clickLikeOfDict:(NSDictionary*)dict;

@end

@interface QSBigImageTableViewDelegateObj : QSTableViewBasicDelegateObj <QSBigImageTableViewCellDelegate>

@property (assign, nonatomic) QSBigImageTableViewCellType type;
@property (weak, nonatomic) NSObject<QSBigImageTableViewDelegateObjDelegate>* delegate;

- (void)rebindData:(NSDictionary*)dict;

@end
