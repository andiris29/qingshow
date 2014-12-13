//
//  QSWaterFallCollectionViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <QuartzCore/QuartzCore.h>

#import "QSShowCollectionViewCell.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "ServerPath.h"
#import "DatabaseConstant.h"

#import "QSShowUtil.h"
#import "QSPeopleUtil.h"

@interface QSShowCollectionViewCell ()

- (void)updateLayoutWithData:(NSDictionary*)showData;


//Basic height
- (void)baseHeightSetup;
- (void)updateViewFrame:(UIView*)view withBase:(float)base imageHeight:(float)imgHeight;
@property (assign, nonatomic) float headIconImageViewBaseY;
@property (assign, nonatomic) float nameLabelBaseY;
@property (assign, nonatomic) float statusLabelBaseY;
@property (assign, nonatomic) float favorNumberLabelBaseY;
@property (assign, nonatomic) float favorButtonBaseY;
@property (assign, nonatomic) float shadowBaseY;
@end


@implementation QSShowCollectionViewCell

#pragma mark - Life Cycle
- (void)awakeFromNib
{
    [super awakeFromNib];
//    self.layer.cornerRadius = 4;
//    self.layer.masksToBounds = YES;
    [self baseHeightSetup];
    self.headIconImageView.layer.cornerRadius = self.headIconImageView.frame.size.height / 2;
    self.headIconImageView.layer.masksToBounds = YES;
    self.headIconImageView.layer.borderColor = [UIColor whiteColor].CGColor;
    self.headIconImageView.layer.borderWidth = 1.f;
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(peopleTap:)];
    [self.headIconImageView addGestureRecognizer:ges];
}

#pragma mark - Data
- (void)bindData:(NSDictionary*)showData
{
    [self updateLayoutWithData:showData];
    
    NSDictionary* modelDict = [QSShowUtil getPeopleFromShow:showData];
    if (modelDict && [modelDict isKindOfClass:[NSDictionary class]]) {
        self.nameLabel.text = [QSPeopleUtil getName:modelDict];
        self.statusLabel.text = [QSPeopleUtil getDetailDesc:modelDict];
        [self.headIconImageView setImageFromURL:[QSPeopleUtil getHeadIconUrl:modelDict]];
    }

    [self.photoImageView setImageFromURL:[QSShowUtil getCoverUrl:showData] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"] animation:NO];
    self.favorNumberLabel.text = [QSShowUtil getNumberLikeDescription:showData];
    self.favorButton.selected = [QSShowUtil getIsLike:showData];
}

#pragma mark - Layout Update
- (void)baseHeightSetup
{
    float baseHeight = self.photoImageView.frame.size.height;
    
    self.headIconImageViewBaseY = self.headIconImageView.frame.origin.y - baseHeight;
    self.nameLabelBaseY = self.nameLabel.frame.origin.y - baseHeight;
    self.statusLabelBaseY = self.statusLabel.frame.origin.y - baseHeight;
    self.favorNumberLabelBaseY = self.favorNumberLabel.frame.origin.y - baseHeight;
    self.favorButtonBaseY = self.favorButton.frame.origin.y - baseHeight;
    self.shadowBaseY = self.shadowImageView.frame.origin.y - baseHeight;
}
- (void)updateLayoutWithData:(NSDictionary*)showData
{
    float height = [QSShowCollectionViewCell getImageHeightWithData:showData];
    CGRect photoRect = self.photoImageView.frame;
    photoRect.size.height = height;
    self.photoImageView.frame = photoRect;
    
    //layout other view
    [self updateViewFrame:self.headIconImageView withBase:self.headIconImageViewBaseY imageHeight:height];
    [self updateViewFrame:self.nameLabel withBase:self.nameLabelBaseY imageHeight:height];
    [self updateViewFrame:self.statusLabel withBase:self.statusLabelBaseY imageHeight:height];
    [self updateViewFrame:self.favorNumberLabel withBase:self.favorNumberLabelBaseY imageHeight:height];
    [self updateViewFrame:self.favorButton withBase:self.favorButtonBaseY imageHeight:height];
    [self updateViewFrame:self.shadowImageView withBase:self.shadowBaseY imageHeight:height];
}

- (void)updateViewFrame:(UIView*)view withBase:(float)base imageHeight:(float)imgHeight
{
    CGRect rect = view.frame;
    rect.origin.y = base + imgHeight;
    view.frame = rect;
}

#pragma mark - Static Method
+ (float)getImageHeightWithData:(NSDictionary*)showData
{
    NSDictionary* coverMetadata = [QSShowUtil getCoverMetadata:showData];
    float iniWidth = 158;
    float height = 212;
    float width = iniWidth;
    //212 158
    if (coverMetadata && coverMetadata[@"height"]) {
        height = ((NSNumber*)coverMetadata[@"height"]).floatValue;
    }
    if (coverMetadata && coverMetadata[@"width"]) {
        width = ((NSNumber*)coverMetadata[@"width"]).floatValue;
    }
    height = height * iniWidth / width;
    return height;
}

+ (float)getHeightWithData:(NSDictionary*)showData
{
    return [self getImageHeightWithData:showData];
}

#pragma mark - IBAction
- (void)peopleTap:(id)sender {
    if ([self.delegate respondsToSelector:@selector(peoplePressed:)]) {
        [self.delegate peoplePressed:self];
    }
}

- (IBAction)favorBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(favorBtnPressed:)]) {
        [self.delegate favorBtnPressed:self];
    }
}
@end
