//
//  QSShowTableViewDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/14/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"
#import "QSBigImageTableViewCell.h"

@protocol QSBigImageTableViewProviderDelegate <QSAbstractScrollProviderDelegate>

@optional
- (void)didClickCell:(UITableViewCell*)cell ofData:(NSDictionary*)dict type:(QSBigImageTableViewCellType)type;

- (void)clickCommentOfDict:(NSDictionary*)dict;
- (void)clickShareOfDict:(NSDictionary*)dict;
- (void)clickLikeOfDict:(NSDictionary*)dict;
- (void)clickDetailOfDict:(NSDictionary*)dict type:(QSBigImageTableViewCellType)type;

@end

@interface QSBigImageTableViewProvider : QSTableViewBasicProvider <QSBigImageTableViewCellDelegate>

@property (assign, nonatomic) QSBigImageTableViewCellType type;
@property (weak, nonatomic) NSObject<QSBigImageTableViewProviderDelegate>* delegate;

- (void)rebindData:(NSDictionary*)dict;

@end
