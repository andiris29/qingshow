//
//  QSS15TopicTableViewCell.h
//  qingshow-ios-native
//
//  Created by ching show on 15/4/27.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#define TOPIC_TALBE_VIEW_CELL_IDENTIFIER @"QSS15TopicTableViewCellIdentifier"

@interface QSS15TopicTableViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIImageView *imgView;
@property (weak, nonatomic) IBOutlet UIImageView *smallImageView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *likeButton;

- (void)bindWithDict:(NSDictionary *)dict;

@end
