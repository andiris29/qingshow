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
#import "QSUserManager.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSLayoutUtil.h"

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
    self.timeLabel.text = [NSString stringWithFormat:@"%d:00~%2d:00", hour.intValue, hour.intValue + 1];
    
    NSArray* topOwners = [data arrayValueForKeyPath:@"topOwners"];
    [self.userHeadsView bindWithUsers:topOwners];
    NSNumber* numOwners = [data numberValueForKeyPath:@"numOwners"];
    self.numLabel.text = [NSString stringWithFormat:@"+%d", numOwners.intValue];
    
    int number = [data numberValueForKeyPath:@"numViewOfCurrentUser"].intValue;
    self.userArray = topOwners;
    
    if (number != -1) {
        NSDictionary* peopleDict = [QSUserManager shareUserManager].userInfo;
        self.singleUser = peopleDict;
        self.userHeadImgView.hidden = NO;
        self.topLabel.hidden = NO;
        self.eyeImgView.hidden = NO;
        self.rankImgView.hidden = NO;
        
        [self.userHeadImgView setImageFromURL:[QSPeopleUtil getHeadIconUrl:peopleDict type:QSImageNameType100]];

        self.topLabel.text = [NSString stringWithFormat:@"%d", number];
        
        CGSize labelSize = [QSLayoutUtil sizeForString:self.topLabel.text withMaxWidth:INFINITY height:9.f font:self.topLabel.font];
        CGSize eyeSize = self.eyeImgView.frame.size;
        CGFloat spaceX = 2.f;
        CGFloat totalWidth = labelSize.width + spaceX + eyeSize.width;
        CGFloat eyeOri = self.userHeadImgView.center.x - totalWidth / 2;
        self.eyeImgView.center = self.topLabel.center;
        CGRect frame = self.eyeImgView.frame;
        frame.origin.x = eyeOri;
        self.eyeImgView.frame = frame;
        frame = self.topLabel.frame;
        frame.origin.x = eyeOri + eyeSize.width + spaceX;
        self.topLabel.frame = frame;
    } else {
        self.userHeadImgView.hidden = YES;
        self.topLabel.hidden = YES;
        self.eyeImgView.hidden = YES;
        self.rankImgView.hidden = YES;
    }
    self.rankImgView.image = [QSPeopleUtil rankImgView:dict];
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
            
            [foregroundView setImageFromURL:[QSImageNameUtil appendImageNameUrl:[QSShowUtil getCoverForegroundUrl:showDict] type:QSImageNameTypeXS]];
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
