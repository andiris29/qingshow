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
#import "QSPreviewUtil.h"
#import "QSBrandUtil.h"
#import "QSImageNameUtil.h"
#import "QSBigImageDateView.h"
#import "QSChosenUtil.h"
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
+ (CGFloat)getHeightWithChosen:(NSDictionary*)chosen
{
    QSChosenRefType type = [QSChosenUtil getChosenRefType:chosen];
    NSDictionary* ref = [QSChosenUtil getRef:chosen];
    if (type == QSChosenRefTypeShow) {
        return [self getHeightWithShow:ref];
    } else if (type == QSChosenRefTypePreview) {
        return [self getHeightWithPreview:ref];
    } else if (type == QSChosenRefTypeItem) {
        return [self getHeightWithItem:ref];
    }
    return 0;
}

+ (CGFloat)getHeightWithImageMetadata:(NSDictionary*)coverMetadata {
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

+ (CGFloat)getHeightWithPreview:(NSDictionary*)previewDict
{
    NSDictionary* coverMetadata = nil;
    coverMetadata = [QSPreviewUtil getCoverMetadata:previewDict];
    return [self getHeightWithImageMetadata:coverMetadata];
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
    if (self.type == QSBigImageTableViewCellTypeFashion) {
        [self bindWithPreview:dict];
    } else if (self.type == QSBigImageTableViewCellChosen) {
        [self bindWithChosen:dict];
    }else {
        [self bindWithShow:dict];
    }
}
- (void)bindWithChosen:(NSDictionary*)dict
{
    QSChosenRefType type = [QSChosenUtil getChosenRefType:dict];
    NSDictionary* ref = [QSChosenUtil getRef:dict];
    if (type == QSChosenRefTypeShow) {
        [self bindWithShow:ref];
    } else if (type == QSChosenRefTypePreview) {
        [self bindWithPreview:ref];
    } else if (type == QSChosenRefTypeItem) {
        [self bindWIthItem:ref];
    }

}

- (void)bindWithShow:(NSDictionary*)showDict
{
    float height = [QSBigImageTableViewCell getHeightWithShow:showDict];
    [self resizeWithHeight:height];

    //Data Binding
    [self.imgView setImageFromURL:[QSShowUtil getHoriCoverUrl:showDict] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"] animation:NO];
#warning TODO label1, like btn
//    self.label1.text = [QSPeopleUtil getName:modelDict];
//    [self.iconImgView setImageFromURL:[QSImageNameUtil generate2xImageNameUrl:[QSPeopleUtil getHeadIconUrl:modelDict]]];

}
- (void)bindWIthItem:(NSDictionary*)itemDict
{
    float height = [QSBigImageTableViewCell getHeightWithItem:itemDict];
    [self resizeWithHeight:height];
    [self.imgView setImageFromURL:[QSItemUtil getFirstImagesUrl:itemDict] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"] animation:NO];
#warning TODO label1
    ;
}
- (void)bindWithBrand:(NSDictionary*)brandDict
{
#warning TODO remove
    float height = [QSBigImageTableViewCell getHeightWithBrand:brandDict];
    [self resizeWithHeight:height];
    
    self.label1.text = [QSBrandUtil getBrandName:brandDict];
    [self.iconImgView setImageFromURL:[QSBrandUtil getBrandLogoUrl:brandDict]];
    [self.imgView setImageFromURL:[QSImageNameUtil generate2xImageNameUrl:[QSBrandUtil getBrandCoverUrl:brandDict]]];
}

- (void)bindWithPreview:(NSDictionary*)previewDict
{
    float height = [QSBigImageTableViewCell getHeightWithPreview:previewDict];
    [self resizeWithHeight:height];
//    [self.imgView setImageFromURL:[QSImageNameUtil generate2xImageNameUrl:[QSPreviewUtil getCoverUrl:previewDict]]];
    [self.imgView setImageFromURL:[QSImageNameUtil generate2xImageNameUrl:[QSPreviewUtil getFirstImageUrl:previewDict]]];
#warning TODO label1
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
