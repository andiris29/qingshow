//
//  QSModelListTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSModelListTableViewCell.h"
#import <QuartzCore/QuartzCore.h>
#import "UIImageView+MKNetworkKitAdditions.h"
#import "ServerPath.h"
#import "QSPeopleUtil.h"

@implementation QSModelListTableViewCell

#pragma mark - Life Cycle
- (void)awakeFromNib
{
    self.headPhotoImageView.layer.cornerRadius = self.headPhotoImageView.frame.size.height / 2;
    self.headPhotoImageView.layer.masksToBounds = YES;
}


#pragma mark - Table View Cell
- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

#pragma mark - IBAction
- (IBAction)followButtonPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(favorBtnPressed:)]) {
        [self.delegate favorBtnPressed:self];
    }
}

#pragma mark - Binding
- (void)bindWithPeople:(NSDictionary*)modelDict
{
    
    self.nameLabel.text = [QSPeopleUtil getName:modelDict];
    self.detailLabel.text = [QSPeopleUtil getDetailDesc:modelDict];
    [self.headPhotoImageView setImageFromURL:[QSPeopleUtil getHeadIconUrl:modelDict]];
    if ([QSPeopleUtil getPeopleIsFollowed:modelDict]) {
        [self.followBtn setTitle:@"取消" forState:UIControlStateNormal];
    } else {
        [self.followBtn setTitle:@"关注" forState:UIControlStateNormal];
    }
    self.showNumLabel.text = [QSPeopleUtil getNumberShowsDescription:modelDict];
    self.followerNumLabel.text = [QSPeopleUtil getNumberFollowersDescription:modelDict];
}

@end
