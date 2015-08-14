//
//  QSDiscountTableViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSAbstractDiscountTableViewCell.h"
@interface QSDiscountTableViewController : UITableViewController<QSDiscountTableViewCellDelegate>

- (instancetype)initWithItem:(NSDictionary*)itemDict;

- (BOOL)checkComplete;
- (NSDictionary*)getResult;
@end
