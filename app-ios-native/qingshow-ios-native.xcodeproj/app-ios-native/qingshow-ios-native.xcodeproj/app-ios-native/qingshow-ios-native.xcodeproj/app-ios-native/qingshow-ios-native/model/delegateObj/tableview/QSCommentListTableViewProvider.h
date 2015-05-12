//
//  QSCommentListTableViewDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"
#import "QSCommentTableViewCell.h"

@protocol QSCommentListTableViewProviderDelegate <QSAbstractScrollProviderDelegate>

- (void)didClickComment:(NSDictionary*)commemntDict atIndex:(int)index;
- (void)didClickPeople:(NSDictionary*)peopleDict;

@end

@interface QSCommentListTableViewProvider : QSTableViewBasicProvider <QSCommentTableViewCellDelegate>

@property (weak, nonatomic) NSObject<QSCommentListTableViewProviderDelegate>* delegate;

@end
