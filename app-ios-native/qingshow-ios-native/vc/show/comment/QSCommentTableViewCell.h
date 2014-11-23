//
//  QSCommentTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSCommentTableViewCell : UITableViewCell

@property (strong, nonatomic) IBOutlet UIImageView* iconImageView;
@property (strong, nonatomic) IBOutlet UILabel* nameLabel;
@property (strong, nonatomic) IBOutlet UILabel* dateLabel;
@property (strong, nonatomic) IBOutlet UILabel* contentLabel;

- (void)bindWithComment:(NSDictionary*)commentDict;

@end
