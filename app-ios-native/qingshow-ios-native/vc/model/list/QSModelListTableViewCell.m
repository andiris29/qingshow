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
#import "QSModelUtil.h"

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
    self.nameLabel.text = modelDict[@"name"];
    self.detailLabel.text = [QSModelUtil buildModelStatusString:modelDict];
    NSString* headPhotoPath = modelDict[@"portrait"];
    [self.headPhotoImageView setImageFromURL:[NSURL URLWithString:headPhotoPath]];
    NSNumber* hasFollowed = modelDict[@"hasFollowed"];
    if (hasFollowed && hasFollowed.boolValue) {
        [self.followBtn setTitle:@"取消" forState:UIControlStateNormal];
    } else {
        [self.followBtn setTitle:@"关注" forState:UIControlStateNormal];
    }
#warning 缺ShowNumber, FavorNumber
}

@end
