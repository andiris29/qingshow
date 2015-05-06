//
//  QSShowTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/14/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBigImageTableViewCell.h"
#import <QuartzCore/QuartzCore.h>
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSShowUtil.h"
#import "QSPeopleUtil.h"
#import "QSPreviewUtil.h"
#import "QSBrandUtil.h"
#import "QSImageNameUtil.h"
#import "QSBigImageDateView.h"


@interface QSBigImageTableViewCell ()

#warning TODO remove single image scroll view
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
    if (_type == QSBigImageTableViewCellTypeBrand) {
        self.iconImgView.layer.cornerRadius = 0.f;
        self.iconImgView.layer.masksToBounds = YES;
        self.iconImgView.layer.borderColor = [UIColor clearColor].CGColor;
        self.iconImgView.layer.borderWidth = 0.f;
    } else {
        self.iconImgView.layer.cornerRadius = self.iconImgView.bounds.size.height / 2;
        self.iconImgView.layer.masksToBounds = YES;
        self.iconImgView.layer.borderColor = [UIColor whiteColor].CGColor;
        self.iconImgView.layer.borderWidth = 1.f;
    }
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
+ (CGFloat)getHeightWithPreview:(NSDictionary*)previewDict
{
    NSDictionary* coverMetadata = nil;
    coverMetadata = [QSPreviewUtil getCoverMetadata:previewDict];
    
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
+ (CGFloat)getHeightWithBrand:(NSDictionary*)brandDict
{
    NSDictionary* coverMetadata = brandDict[@"coverMetadata"];
    float iniWidth = [UIScreen mainScreen].bounds.size.width;
    
    float height = iniWidth;
    float width = iniWidth;
    //1:1
    if (coverMetadata && coverMetadata[@"height"]) {
        height = ((NSNumber*)coverMetadata[@"height"]).floatValue;
    }
    if (coverMetadata && coverMetadata[@"width"]) {
        width = ((NSNumber*)coverMetadata[@"width"]).floatValue;
    }
    height = height * iniWidth / width;
    return height;
    
//    return [UIScreen mainScreen].bounds.size.width;
}

#pragma mark - Bind
- (void)bindWithDict:(NSDictionary*)dict
{
    self.dataDict = dict;
    if (self.type == QSBigImageTableViewCellTypeBrand) {
        [self bindWithBrand:dict];
    } else if (self.type == QSBigImageTableViewCellTypeFashion) {
        [self bindWithPreview:dict];
    } else {
        [self bindWithShow:dict];
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

- (void)bindWithShow:(NSDictionary*)showDict
{
    float height = [QSBigImageTableViewCell getHeightWithShow:showDict];
    [self resizeWithHeight:height];

    //Data Binding
    NSDictionary* modelDict = [QSShowUtil getPeopleFromShow:showDict];
    [self.imgView setImageFromURL:[QSShowUtil getHoriCoverUrl:showDict] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"] animation:NO];
    
    self.label1.text = [QSPeopleUtil getName:modelDict];
    [self.iconImgView setImageFromURL:[QSImageNameUtil generate2xImageNameUrl:[QSPeopleUtil getHeadIconUrl:modelDict]]];

}
- (void)bindWithBrand:(NSDictionary*)brandDict
{
    float height = [QSBigImageTableViewCell getHeightWithBrand:brandDict];
    [self resizeWithHeight:height];
    
    self.label1.text = [QSBrandUtil getBrandName:brandDict];
    [self.iconImgView setImageFromURL:[QSBrandUtil getBrandLogoUrl:brandDict]];
    [self.imgView setImageFromURL:[QSImageNameUtil generate2xImageNameUrl:[QSBrandUtil getBrandCoverUrl:brandDict]]];
}

- (void)bindWithPreview:(NSDictionary*)previewDict
{
#warning TODO adjust binding for new preview layout
    float height = [QSBigImageTableViewCell getHeightWithPreview:previewDict];
    [self resizeWithHeight:height];
    
}
#pragma mark - IBAction

//- (IBAction)detailBtnPressed:(id)sender
//{
//    if ([self.delegate respondsToSelector:@selector(clickDetailBtn:)]) {
//        [self.delegate clickDetailBtn:self];
//    }
//}

@end
