//
//  QSWaterFallCollectionViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSShowCollectionViewCell.h"
#import <QuartzCore/QuartzCore.h>
#import "UIImageView+MKNetworkKitAdditions.h"

#import "ServerPath.h"
#import "DatabaseConstant.h"


@interface QSShowCollectionViewCell ()

- (void)updateLayoutWithData:(NSDictionary*)showData;


//Basic height
- (void)baseHeightSetup;
- (void)updateViewFrame:(UIView*)view withBase:(float)base imageHeight:(float)imgHeight;
@property (assign, nonatomic) float headIconImageViewBaseY;
@property (assign, nonatomic) float nameLabelBaseY;
@property (assign, nonatomic) float statusLabelBaseY;
@property (assign, nonatomic) float contentLabelBaseY;
@property (assign, nonatomic) float favorNumberLabelBaseY;
@property (assign, nonatomic) float favorButtonBaseY;
@end


@implementation QSShowCollectionViewCell
#warning TODO 切圆角

#pragma mark - Life Cycle
- (void)awakeFromNib
{
    [super awakeFromNib];
    [self baseHeightSetup];
    self.headIconImageView.layer.cornerRadius = self.headIconImageView.frame.size.height / 2;
}

#pragma mark - IBAction
- (IBAction)favorBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(favorBtnPressed:)]) {
        [self.delegate favorBtnPressed:self];
    }
}

#pragma mark - Data
- (void)bindData:(NSDictionary*)showData
{
    [self updateLayoutWithData:showData];
    NSDictionary* modelDict = showData[@"modelRef"];
//    NSArray* roles = modelDict[@"roles"];
    self.nameLabel.text = modelDict[@"name"];
    self.statusLabel.text = [NSString stringWithFormat:@"%@cm %@kg",modelDict[@"height"], modelDict[@"weight"]];
    NSString* headPhotoPath = modelDict[@"portrait"];
    [self.headIconImageView setImageFromURL:[NSURL URLWithString:headPhotoPath]];
    
    self.contentLabel.text = showData[@"name"];

    NSString* coverPath = showData[@"cover"];
    [self.photoImageView setImageFromURL:[NSURL URLWithString:coverPath]];
    
    /*
    @property (strong, nonatomic) IBOutlet UIImageView *headIconImageView;
    @property (strong, nonatomic) IBOutlet UILabel *favorNumberLabel;
     */
}

#pragma mark - Layout Update
- (void)baseHeightSetup
{
    float baseHeight = self.photoImageView.frame.size.height;
    
    self.headIconImageViewBaseY = self.headIconImageView.frame.origin.y - baseHeight;
    self.nameLabelBaseY = self.nameLabel.frame.origin.y - baseHeight;
    self.statusLabelBaseY = self.statusLabel.frame.origin.y - baseHeight;
    self.contentLabelBaseY = self.contentLabel.frame.origin.y - baseHeight;
    self.favorNumberLabelBaseY = self.favorNumberLabel.frame.origin.y - baseHeight;
    self.favorButtonBaseY = self.favorButton.frame.origin.y - baseHeight;
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
    [self updateViewFrame:self.contentLabel withBase:self.contentLabelBaseY imageHeight:height];
    [self updateViewFrame:self.headIconImageView withBase:self.headIconImageViewBaseY imageHeight:height];
    [self updateViewFrame:self.favorNumberLabel withBase:self.favorNumberLabelBaseY imageHeight:height];
    [self updateViewFrame:self.headIconImageView withBase:self.favorButtonBaseY imageHeight:height];
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
    NSDictionary* coverMetadata = showData[@"coverMetaData"];
    float iniWidth = 145;
    float height = 212;
    float width = iniWidth;
    //212 145
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
    float height = [QSShowCollectionViewCell getImageHeightWithData:showData];
    height += 270 - 212;
    return height;
}

@end
