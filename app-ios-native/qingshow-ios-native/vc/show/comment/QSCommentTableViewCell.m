//
//  QSCommentTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>

#import "QSCommentTableViewCell.h"

#import "UIImageView+MKNetworkKitAdditions.h"

#import "QSCommentUtil.h"
#import "QSPeopleUtil.h"


@implementation QSCommentTableViewCell

- (void)awakeFromNib {
    self.iconImageView.layer.cornerRadius = self.iconImageView.bounds.size.width / 2;
    self.iconImageView.layer.masksToBounds = YES;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

#pragma mark - Bind
- (void)bindWithComment:(NSDictionary*)commentDict
{
    NSDictionary* peopleDict = [QSCommentUtil getPeople:commentDict];
    self.nameLabel.text = [QSPeopleUtil getName:peopleDict];
    [self.iconImageView setImageFromURL:[QSPeopleUtil getHeadIconUrl:peopleDict] placeHolderImage:[UIImage imageNamed:@"nav_btn_account"] animation:YES];
    self.dateLabel.text = [QSCommentUtil getFormatedDateString:commentDict];
    self.contentLabel.text = [QSCommentUtil getContent:commentDict];
}

@end
