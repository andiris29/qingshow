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


@interface QSBigImageTableViewCell ()

@property (strong, nonatomic) QSSingleImageScrollView* singleImageScrollView;
@property (weak, nonatomic) NSDictionary* dataDict;
@end

@implementation QSBigImageTableViewCell
- (void)setType:(QSBigImageTableViewCellType)type
{
    _type = type;
    switch (_type) {
        case QSBigImageTableViewCellTypeModelEmpty: {
            self.label1.hidden = YES;
            self.label2.hidden = YES;
            self.iconImgView.hidden = YES;
            break;
        }
        default: {
            self.label1.hidden = NO;
            self.label2.hidden = NO;
            self.iconImgView.hidden = NO;
            break;
        }
    }
}
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
    if (self.singleImageScrollView) {
        self.singleImageScrollView.frame = rect;
    }
    
    rect = self.modelContainer.frame;
    rect.origin.y = height - rect.size.height;
    self.modelContainer.frame = rect;
    
    if (self.btnsContainer) {
        CGRect btnsRect = self.btnsContainer.frame;
        btnsRect.origin.y = height - rect.size.height - btnsRect.size.height - 20;
        self.btnsContainer.frame = btnsRect;
    }
}

- (void)bindWithShow:(NSDictionary*)showDict
{
    float height = [QSBigImageTableViewCell getHeightWithShow:showDict];
    [self resizeWithHeight:height];

    //Data Binding
    NSDictionary* modelDict = [QSShowUtil getPeopleFromShow:showDict];
    [self.imgView setImageFromURL:[QSShowUtil getHoriCoverUrl:showDict] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"] animation:NO];
    
    self.label1.text = [QSPeopleUtil getName:modelDict];
    self.label2.text = [QSPeopleUtil getDetailDesc:modelDict];
    [self.iconImgView setImageFromURL:[QSPeopleUtil getHeadIconUrl:modelDict]];

}
- (void)bindWithBrand:(NSDictionary*)brandDict
{
    float height = [QSBigImageTableViewCell getHeightWithBrand:brandDict];
    [self resizeWithHeight:height];
    
    self.label1.text = [QSBrandUtil getBrandName:brandDict];
    self.label2.text = [QSBrandUtil getBrandShopAddress:brandDict];
    [self.iconImgView setImageFromURL:[QSBrandUtil getBrandLogoUrl:brandDict]];
    [self.imgView setImageFromURL:[QSBrandUtil getBrandCoverUrl:brandDict]];
}

- (void)bindWithPreview:(NSDictionary*)previewDict
{
    if (!self.singleImageScrollView) {
        self.singleImageScrollView = [[QSSingleImageScrollView alloc] initWithFrame:self.imgView.frame];
        self.singleImageScrollView.pageControlOffsetY = 20.f;
        self.singleImageScrollView.delegate = self;
        self.singleImageScrollView.enableLazyLoad = YES;
        self.imgView.hidden = YES;
        [self.contentView insertSubview:self.singleImageScrollView belowSubview:self.modelContainer];
    }
    float height = [QSBigImageTableViewCell getHeightWithPreview:previewDict];
    [self resizeWithHeight:height];
    
//    self.label1.text = [QSPreviewUtil getImagesBrandDesc:previewDict atIndex:(int)self.singleImageScrollView.pageControl.currentPage];
    self.label1.text = [QSPreviewUtil getImagesDesc:previewDict atIndex:(int)self.singleImageScrollView.pageControl.currentPage];
//    self.label3.text = [QSPreviewUtil getImagesPriceDesc:previewDict atIndex:(int)self.singleImageScrollView.pageControl.currentPage];
//    self.label2.text = [QSPreviewUtil getNameDesc:previewDict];
//    self.label3.text = [QSPreviewUtil getPriceDesc:previewDict];
    
    [self.likeBtn setTitle:[QSPreviewUtil getNumLikeDesc:previewDict] forState:UIControlStateNormal];
    [self.commentBtn setTitle:[QSPreviewUtil getNumCommentDesc:previewDict] forState:UIControlStateNormal];
//    [self.imgView setImageFromURL:[QSPreviewUtil getCoverUrl:previewDict] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"] animation:NO];
    self.singleImageScrollView.imageUrlArray = [QSPreviewUtil getImagesUrl:previewDict];
    
    [self.likeBtn setHighlighted:[QSPreviewUtil getIsLike:previewDict]];
}
#pragma mark - IBAction
- (IBAction)commentBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(clickCommentBtn:)]) {
        [self.delegate clickCommentBtn:self];
    }
}
- (IBAction)likeBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(clickLikeBtn:)]) {
        [self.delegate clickLikeBtn:self];
    }
}
- (IBAction)shareBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(clickShareBtn:)]) {
        [self.delegate clickShareBtn:self];
    }
}
- (void)imageScrollView:(QSImageScrollViewBase*)view didChangeToPage:(int)page
{
    if (self.type == QSBigImageTableViewCellTypeFashion) {
        self.label1.text = [QSPreviewUtil getImagesBrandDesc:self.dataDict atIndex:page];
        self.label2.text = [QSPreviewUtil getImagesDesc:self.dataDict atIndex:page];
        self.label3.text = [QSPreviewUtil getImagesPriceDesc:self.dataDict atIndex:page];
    }
}

- (IBAction)detailBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(clickDetailBtn:)]) {
        [self.delegate clickDetailBtn:self];
    }
}
@end
