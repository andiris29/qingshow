//
//  QSItemListTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#define QSItemListTableViewCellIdentifier @"QSItemListTableViewCell"

@interface QSItemListTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel* typeLabel;
@property (weak, nonatomic) IBOutlet UILabel* nameLabel;

- (void)bindWithItem:(NSDictionary*)itemDict;

@end
