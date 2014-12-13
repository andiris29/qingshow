//
//  QSShowTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/14/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSShowTableViewCell.h"
#import <QuartzCore/QuartzCore.h>
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSShowUtil.h"
#import "QSPeopleUtil.h"

@implementation QSShowTableViewCell
#pragma mark - Life Cycle
- (void)awakeFromNib {
    // Initialization code
    self.iconImgView.layer.cornerRadius = self.iconImgView.bounds.size.height / 2;
    self.iconImgView.layer.masksToBounds = YES;
    self.iconImgView.layer.borderColor = [UIColor whiteColor].CGColor;
    self.iconImgView.layer.borderWidth = 1.f;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
#pragma mark - Static
+ (CGFloat)getHeighWithShow:(NSDictionary*)showDict
{
    NSDictionary* coverMetadata = showDict[@"coverMetadata"];
    float iniWidth = [UIScreen mainScreen].bounds.size.width;
//    [UIDevice currentDevice].
    
    float height = 92;
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
#pragma mark - Bind
- (void)bindWithShow:(NSDictionary*)showDict
{
    //Resize
    float height = [QSShowTableViewCell getHeighWithShow:showDict];
    CGRect rect = self.imgView.frame;
    rect.size.height = height;
    self.imgView.frame = rect;
    
    rect = self.modelContainer.frame;
    rect.origin.y = height - rect.size.height;
    self.modelContainer.frame = rect;
    
    //Data Binding
    NSDictionary* modelDict = [QSShowUtil getPeopleFromShow:showDict];
    [self.imgView setImageFromURL:[QSShowUtil getCoverUrl:showDict] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"] animation:NO];
    
    self.label1.text = [QSPeopleUtil getName:modelDict];
    self.label2.text = [QSPeopleUtil getDetailDesc:modelDict];
    [self.iconImgView setImageFromURL:[QSPeopleUtil getHeadIconUrl:modelDict]];
}
@end
