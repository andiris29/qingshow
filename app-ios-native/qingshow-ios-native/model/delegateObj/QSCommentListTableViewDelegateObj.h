//
//  QSCommentListTableViewDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSTableViewBasicDelegateObj.h"
#import "QSCommentTableViewCell.h"

@protocol QSCommentListTableViewDelegateObj <QSAbstractScrollDelegateObjDelegate>

- (void)didClickComment:(NSDictionary*)commemntDict atIndex:(int)index;
- (void)didClickPeople:(NSDictionary*)peopleDict;

@end

@interface QSCommentListTableViewDelegateObj : QSTableViewBasicDelegateObj <QSCommentTableViewCellDelegate>

@property (weak, nonatomic) NSObject<QSCommentListTableViewDelegateObj>* delegate;

@end
