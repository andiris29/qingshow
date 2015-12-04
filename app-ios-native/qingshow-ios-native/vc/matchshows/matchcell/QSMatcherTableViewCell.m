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

@interface QSMatcherTableViewCell () <QSMatcherCollectionViewHeaderUserRowViewDelegate>
@property (strong, nonatomic) QSMatcherCollectionViewHeaderUserRowView* userHeadsView;
@property (weak, nonatomic) NSArray* userArray;
@property (weak, nonatomic) NSDictionary* singleUser;
@property (weak, nonatomic) IBOutlet UILabel* topLabel;
@end

@implementation QSMatcherTableViewCell

- (void)awakeFromNib {
    // Initialization code
    self.userHeadsView = [[QSMatcherCollectionViewHeaderUserRowView alloc] init];
    self.userHeadsView.delegate = self;
    [self.usersContainer addSubview:self.userHeadsView];
    self.userHeadsView.frame = self.usersContainer.bounds;
    self.userHeadImgView.layer.cornerRadius = self.userHeadImgView.bounds.size.height / 2;
    self.userHeadImgView.layer.masksToBounds = YES;
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapUserImgView:)];
    self.userHeadImgView.userInteractionEnabled = YES;
    [self.userHeadImgView addGestureRecognizer:ges];
    
    self.bottomContainer.layer.shadowColor = [UIColor blackColor].CGColor;
    self.bottomContainer.layer.shadowOffset = CGSizeMake(2, 2);
    self.bottomContainer.layer.shadowOpacity = 0.5f;
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
    self.userArray = topOwners;
    if (index >= 0 && index < topOwners.count) {
        NSDictionary* peopleDict = topOwners[index];
        self.singleUser = peopleDict;
        [self.userHeadImgView setImageFromURL:[QSPeopleUtil getHeadIconUrl:peopleDict type:QSImageNameType100]];
        self.topLabel.hidden = NO;
        self.topLabel.text = [NSString stringWithFormat:@"TOP%d", index];
    } else {
        self.topLabel.hidden = YES;
    }
    NSArray* topShows = [data arrayValueForKeyPath:@"topShows"];
    for (int i = 0; i < self.showImgViews.count; i++) {
        UIImageView* imgView = self.showImgViews[i];
        UIImageView* foregroundView = self.showForegroundImgViews[i];
        UIView* backgroundView = self.showBackgroundViews[i];
        if (i < topShows.count) {
            imgView.hidden = NO;
            foregroundView.hidden = NO;
            backgroundView.hidden = NO;
            NSDictionary* showDict = topShows[i];
            [imgView setImageFromURL:[QSImageNameUtil appendImageNameUrl:[QSShowUtil getCoverUrl:showDict] type:QSImageNameTypeXS]];
            [foregroundView setImageFromURL:[QSShowUtil getCoverForegroundUrl:showDict]];
        } else {
            imgView.hidden = YES;
            foregroundView.hidden = YES;
            backgroundView.hidden = YES;
        }
    }
}

- (void)userRowView:(QSMatcherCollectionViewHeaderUserRowView*)view didClickIndex:(NSUInteger)index {
    if (index < self.userArray.count) {
        NSDictionary* userDict = self.userArray[index];
        if ([self.delegate respondsToSelector:@selector(cell:didClickUser:)]) {
            [self.delegate cell:self didClickUser:userDict];
        }
    }
}
- (void)didTapUserImgView:(UITapGestureRecognizer*)ges {
    if (self.singleUser && [self.delegate respondsToSelector:@selector(cell:didClickUser:)]) {
        [self.delegate cell:self didClickUser:self.singleUser];
    }
}
@end
