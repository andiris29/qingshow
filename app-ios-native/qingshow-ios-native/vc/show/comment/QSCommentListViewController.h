//
//  QSCommentListViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSCommentListTableViewDelegateObj.h"

@interface QSCommentListViewController : UIViewController<QSCommentListTableViewDelegateObj>

- (id)initWithShow:(NSDictionary*)showDict;

@end
