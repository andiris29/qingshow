//
//  QSItemImageTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 1/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSItemImageTableViewCell.h"

#import "QSItemUtil.h"

@interface QSItemImageTableViewCell ()

@property (strong, nonatomic) QSSingleImageScrollView* imageScrollView;
@property (weak, nonatomic) NSDictionary* itemDict;
@end

@implementation QSItemImageTableViewCell

#pragma mark - Life Cycle
- (void)awakeFromNib {
    // Initialization code
    [self baseYsetup];
    self.imageScrollView = [[QSSingleImageScrollView alloc] initWithFrame:self.imageContainerView.bounds direction:QSImageScrollViewDirectionHor];
    self.imageScrollView.pageControlOffsetY = 60.f;
    self.imageScrollView.delegate = self;
    [self.imageContainerView addSubview:self.imageScrollView];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
#pragma mark - Bind
- (void)bindWithItem:(NSDictionary*)itemDict
{
    self.itemDict = itemDict;
    float height = [QSItemImageTableViewCell getHeightWithItem:itemDict];
    [self resizeWithHeight:height];
    self.imageScrollView.imageUrlArray = [QSItemUtil getImagesUrl:itemDict];
    
//    self.imageScrollView.imageUrlArray = @[[QSItemUtil getCoverUrl:itemDict],[QSItemUtil getCoverUrl:itemDict]];
//    self.nameLabel.text = [QSItemUtil getItemDescription:itemDict];
    self.nameLabel.text = [QSItemUtil getImageDesc:itemDict atIndex:(int)self.imageScrollView.pageControl.currentPage];
    self.priceLabel.text = [QSItemUtil getPrice:itemDict];
    self.shopBtn.hidden = [QSItemUtil getShopUrl:itemDict] == nil;
    
}
- (void)baseYsetup
{
    
}
- (void)resizeWithHeight:(float)height
{
    float y = height - self.infoContainerView.frame.size.height;
    CGRect rect = self.infoContainerView.frame;
    rect.origin.y = y;
    self.infoContainerView.frame = rect;
    
    rect = self.imageContainerView.frame;
    rect.size.height = height;
    self.imageContainerView.frame = rect;
    self.imageScrollView.frame = self.imageContainerView.bounds;
}

#pragma mark - Static
+ (CGFloat)getHeightWithItem:(NSDictionary*)itemDict
{
    NSDictionary* coverMetadata = nil;
    coverMetadata = itemDict[@"imageMetadata"];
    
    float iniWidth = [UIScreen mainScreen].bounds.size.width;
    
    float height = iniWidth;
    float width = iniWidth;

    if (coverMetadata && coverMetadata[@"height"]) {
        height = ((NSNumber*)coverMetadata[@"height"]).floatValue;
    }
    if (coverMetadata && coverMetadata[@"width"]) {
        width = ((NSNumber*)coverMetadata[@"width"]).floatValue;
    }
    height = height * iniWidth / width;
    return height;
}

- (IBAction)shopBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickShopBtn:)]) {
        [self.delegate didClickShopBtn:self];
    }
}
- (void)imageScrollView:(QSImageScrollViewBase*)view didChangeToPage:(int)page
{
    self.nameLabel.text = [QSItemUtil getImageDesc:self.itemDict atIndex:page];
}
@end
