//
//  QSTopicTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#define TOPIC_TALBE_VIEW_CELL_IDENTIFIER @"QSTopicTableViewCellIdentifier"

@interface QSTopicTableViewCell : UITableViewCell

@property (strong, nonatomic) IBOutlet UILabel* titleLabel;
@property (strong, nonatomic) IBOutlet UILabel* subtitleLabel;
//@property (strong, nonatomic) IBOutlet UILabel* tagLabel;
//@property (strong, nonatomic) IBOutlet UILabel* numberLabel;
@property (strong, nonatomic) IBOutlet UIImageView* imgView;
//@property (strong, nonatomic) IBOutlet UIView* tagBgView;
@property (strong, nonatomic) IBOutlet UIButton* likeBtn;

- (void)bindWithDict:(NSDictionary*)dict;
@end
