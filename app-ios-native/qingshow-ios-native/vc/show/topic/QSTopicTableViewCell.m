//
//  QSTopicTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTopicTableViewCell.h"
#import "QSTopicUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@implementation QSTopicTableViewCell

- (void)awakeFromNib {
    // Initialization code
    self.selectionStyle = UITableViewCellSelectionStyleNone;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    // Configure the view for the selected state
}


- (void)bindWithDict:(NSDictionary*)dict
{
    self.titleLabel.text = [QSTopicUtil getTitle:dict];
    self.subtitleLabel.text = [QSTopicUtil getSubTitle:dict];
    [self.imgView setImageFromURL:[QSTopicUtil getCoverUrl:dict]];
}
@end
