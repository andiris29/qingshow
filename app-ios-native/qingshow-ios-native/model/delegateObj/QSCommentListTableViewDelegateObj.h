//
//  QSCommentListTableViewDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSTableViewBasicDelegateObj.h"

@protocol QSCommentListTableViewDelegateObj <QSTableViewBasicDelegateObjDelegate>

- (void)didClickComment:(NSDictionary*)commemntDict atIndex:(int)index;

@end

@interface QSCommentListTableViewDelegateObj : QSTableViewBasicDelegateObj

@property (weak, nonatomic) NSObject<QSCommentListTableViewDelegateObj>* delegate;

@end
