//
//  QSShowTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/14/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#define TEXT_COLOR_NORMAL [UIColor whiteColor]
#define TEXT_COLOR_SELECTED [UIColor redColor]

#import "QSBigImageTableViewCell.h"
#import <QuartzCore/QuartzCore.h>
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSShowUtil.h"
#import "QSPeopleUtil.h"
#import "QSImageNameUtil.h"
#import "QSBigImageDateView.h"
#import "QSItemUtil.h"


@interface QSBigImageTableViewCell ()

@property (weak, nonatomic) NSDictionary* dataDict;

@property (strong, nonatomic) QSBigImageDateView* dateView;
@end

@implementation QSBigImageTableViewCell
- (void)setType:(QSBigImageTableViewCellType)type
{
    
    _type = type;
    switch (_type) {
        case QSBigImageTableViewCellTypeModelEmpty: {
            self.label1.hidden = YES;
            self.iconImgView.hidden = YES;
            break;
        }
        default: {
            self.label1.hidden = NO;
            self.iconImgView.hidden = NO;
            break;
        }
    }

    self.iconImgView.layer.cornerRadius = self.iconImgView.bounds.size.height / 2;
    self.iconImgView.layer.masksToBounds = YES;
    self.iconImgView.layer.borderColor = [UIColor whiteColor].CGColor;
    self.iconImgView.layer.borderWidth = 1.f;

}

#pragma mark - Life Cycle
- (void)awakeFromNib {
    self.dateView = [QSBigImageDateView makeView];
    [self.dateContainer addSubview:self.dateView];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
#pragma mark - Static
+ (CGFloat)getHeightWithImageMetadata:(NSDictionary*)coverMetadata {
    return 285.f;
    float iniWidth = [UIScreen mainScreen].bounds.size.width;
    
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


+ (CGFloat)getHeightWithShow:(NSDictionary*)showDict
{
    NSDictionary* coverMetadata = nil;
    coverMetadata = showDict[@"horizontalCoverMetadata"];
    if (!coverMetadata || [coverMetadata isKindOfClass:[NSNull class]]) {
        coverMetadata = showDict[@"coverMetadata"];
    }
    return [self getHeightWithImageMetadata:coverMetadata];
}

+ (CGFloat)getHeightWithItem:(NSDictionary*)itemDict
{
    NSDictionary* coverMetadata = nil;
    coverMetadata = [QSItemUtil getImageMetadata:itemDict];
    return [self getHeightWithImageMetadata:coverMetadata];
}

+ (CGFloat)getHeightWithBrand:(NSDictionary*)brandDict
{
    NSDictionary* coverMetadata = brandDict[@"coverMetadata"];
    return [self getHeightWithImageMetadata:coverMetadata];
}

#pragma mark - Bind
- (void)bindWithDict:(NSDictionary*)dict
{
    self.dataDict = dict;

    [self bindWithShow:dict];
    
}


#pragma mark - UI
- (void)setLikeBtnHover:(BOOL)fHover
{
    if (fHover) {
        [self.likeButton setBackgroundImage:[UIImage imageNamed:@"s03_like_btn_hover"] forState:UIControlStateNormal];
        [self.likeButton setTitleColor:[UIColor redColor] forState:UIControlStateNormal];
    } else {
        [self.likeButton setBackgroundImage:[UIImage imageNamed:@"s03_like_btn"] forState:UIControlStateNormal];
        [self.likeButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    }
}

- (void)bindWithShow:(NSDictionary*)showDict
{
    float height = [QSBigImageTableViewCell getHeightWithShow:showDict];
    [self resizeWithHeight:height];

    //Data Binding
    [self.imgView setImageFromURL:[QSShowUtil getHoriCoverUrl:showDict] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"] animation:NO];
    self.label1.text = [QSShowUtil getShowDesc:showDict];
    NSDictionary* brandDict = [QSShowUtil getBrand:showDict];
    [self updateBrandLogo:brandDict];
    [self.likeButton setTitle:[QSShowUtil getNumberLikeDescription:showDict] forState:UIControlStateNormal];
    [self setLikeBtnHover:[QSShowUtil getIsLike:showDict]];
}
- (void)bindWithItem:(NSDictionary*)itemDict
{
    float height = [QSBigImageTableViewCell getHeightWithItem:itemDict];
    [self resizeWithHeight:height];
    [self.imgView setImageFromURL:[QSItemUtil getFirstImagesUrl:itemDict] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"] animation:NO];
    self.label1.text = [QSItemUtil getItemName:itemDict];
    NSURL* brandLogo = [QSItemUtil getBrandLogoUrl:itemDict];
    [self updateBrandLogo:brandLogo];
    [self.likeButton setTitle:[QSItemUtil getNumberLikeDescription:itemDict] forState:UIControlStateNormal];
    [self setLikeBtnHover:[QSItemUtil getIsLike:itemDict]];
}


#pragma mark Binding Helper
- (void)updateBrandLogo:(NSURL*)brandLogo
{
    if (brandLogo) {
        self.iconImgView.hidden = NO;
        [self.iconImgView setImageFromURL:brandLogo];
    } else {
        self.iconImgView.hidden = YES;

    }
}

- (void)resizeWithHeight:(float)height
{
    //Resize
    CGRect rect = self.imgView.frame;
    rect.size.height = height;
    self.imgView.frame = rect;
    
    rect = self.modelContainer.frame;
    rect.origin.y = height - rect.size.height;
    self.modelContainer.frame = rect;
}

#pragma mark - IBAction

- (IBAction)detailBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(clickDetailBtn:)]) {
        [self.delegate clickDetailBtn:self];
    }
}
- (IBAction)likeBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(clickLikeBtn:)]) {
        [self.delegate clickLikeBtn:self];
    }
}
@end
