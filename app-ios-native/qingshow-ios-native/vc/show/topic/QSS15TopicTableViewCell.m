//
//  QSS15TopicTableViewCell.m
//  qingshow-ios-native
//
//  Created by ching show on 15/4/27.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS15TopicTableViewCell.h"
#import "QSTopicUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
@implementation QSS15TopicTableViewCell

- (void)awakeFromNib {
    // Initialization code
    self.selectionStyle = UITableViewCellEditingStyleNone;
    self.smallImageView.layer.masksToBounds = YES;
    self.smallImageView.layer.cornerRadius = self.smallImageView.frame.size.width / 2;
    self.smallImageView.layer.borderWidth = 1.f;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
- (void)bindWithDict:(NSDictionary *)dict
{
    self.titleLabel.text = [QSTopicUtil getTitle:dict];
    [self.imgView setImageFromURL:[QSTopicUtil getCoverUrl:dict]];
}
@end
