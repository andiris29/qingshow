//
//  QSMatcherTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSMatcherTableViewCell.h"
#import "NSDictionary+QSExtension.h"
#import "QSMatcherCollectionViewHeaderUserRowView.h"
#import "QSDateUtil.h"
#import "QSPeopleUtil.h"
#import "QSShowUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@interface QSMatcherTableViewCell ()
@property (strong, nonatomic) QSMatcherCollectionViewHeaderUserRowView* userHeadsView;

@end

@implementation QSMatcherTableViewCell

- (void)awakeFromNib {
    // Initialization code
    self.userHeadsView = [[QSMatcherCollectionViewHeaderUserRowView alloc] init];
    [self.usersContainer addSubview:self.userHeadsView];
    self.userHeadsView.frame = self.usersContainer.bounds;
    self.userHeadImgView.layer.cornerRadius = self.userHeadImgView.bounds.size.height / 2;
    self.userHeadImgView.layer.masksToBounds = YES;
    self.selectionStyle = UITableViewCellSelectionStyleNone;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.userHeadsView.frame = self.usersContainer.bounds;;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)bindWithDict:(NSDictionary*)dict {
    NSNumber* hour = [dict numberValueForKeyPath:@"hour"];
    NSDictionary* data = [dict dictValueForKeyPath:@"data"];
    NSDate* date = [dict dateValueForKeyPath:@"date"];
    self.dateLabel.text = [QSDateUtil buildDayStringFromDate:date];
    self.timeLabel.text = [NSString stringWithFormat:@"%2d:00~%2d:00", hour.intValue, hour.intValue + 1];
    
    NSArray* topOwners = [data arrayValueForKeyPath:@"topOwners"];
    [self.userHeadsView bindWithUsers:topOwners];
    NSNumber* numOwners = [data numberValueForKeyPath:@"numOwners"];
    self.numLabel.text = [NSString stringWithFormat:@"+%d", numOwners.intValue];
    
    int index = [data numberValueForKeyPath:@"indexOfCurrentUser"].intValue;
    if (index >= 0 && index < topOwners.count) {
        NSDictionary* peopleDict = topOwners[index];
        [self.userHeadImgView setImageFromURL:[QSPeopleUtil getHeadIconUrl:peopleDict]];
    }
    NSArray* topShows = [data arrayValueForKeyPath:@"topShows"];
    
    for (int i = 0; i < self.showImgViews.count; i++) {
        UIImageView* imgView = self.showImgViews[i];
        UIImageView* foregroundView = self.showForegroundImgViews[i];
        if (i < topShows.count) {
            imgView.hidden = NO;
            foregroundView.hidden = NO;
            NSDictionary* showDict = topShows[i];
            [imgView setImageFromURL:[QSShowUtil getCoverUrl:showDict]];
            [foregroundView setImageFromURL:[QSShowUtil getCoverForegroundUrl:showDict]];
        } else {
            imgView.hidden = YES;
            foregroundView.hidden = YES;
        }
    }
    
}
@end
