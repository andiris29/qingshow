//
//  QSItemCollectionViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 2/1/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSItemCollectionViewCell.h"
#import "UILabelStrikeThrough.h"
#import "QSItemUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@interface QSItemCollectionViewCell ()
@property (assign, nonatomic) float priceBaseY;
@property (assign, nonatomic) float priceAfterDiscountBaseY;
@property (assign, nonatomic) float shadowImageViewBaseY;

@end

@implementation QSItemCollectionViewCell


#pragma mark - Life Cycle
- (void)awakeFromNib {
    [self baseHeightSetup];
    self.priceLabel.isWithStrikeThrough = YES;
    self.priceLabel.isNotStrikeDollor = YES;
}

#pragma mark - Binding
- (void)bindWithItem:(NSDictionary*)itemDict
{
    NSArray* urlArray = [QSItemUtil getImagesUrl:itemDict];
    if (urlArray.count) {
        NSURL* url = urlArray[0];
        [self.itemImageView setImageFromURL:url placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"] animation:NO];
    } else {
        self.itemImageView.image = nil;
    }
    
#warning TODO
    //[QSItemUtil hasDiscountInfo:itemDict]
    if (YES) {
        self.priceLabel.hidden = NO;
        self.priceAfterDiscountLabel.text = [QSItemUtil getPriceAfterDiscountDesc:itemDict];
        self.priceLabel.text = [QSItemUtil getPriceDesc:itemDict];
        [self.priceAfterDiscountLabel sizeToFit];
        [self.priceLabel sizeToFit];
    } else {
        self.priceLabel.hidden = YES;
        self.priceLabel.text = @"";
        self.priceAfterDiscountLabel.text = [QSItemUtil getPriceDesc:itemDict];
        [self.priceLabel sizeToFit];
        [self.priceAfterDiscountLabel sizeToFit];

    }
    
    [self updateLayoutWithData:itemDict];
}

#pragma mark - Layout
- (void)baseHeightSetup
{
    float baseHeight = self.itemImageView.frame.size.height;
    self.priceBaseY = self.priceLabel.frame.origin.y - baseHeight;
    self.priceAfterDiscountBaseY = self.priceAfterDiscountLabel.frame.origin.y - baseHeight;
    self.shadowImageViewBaseY = self.shadowImageView.frame.origin.y - baseHeight;
}
- (void)updateLayoutWithData:(NSDictionary*)itemDict
{
    CGFloat height = [QSItemCollectionViewCell getHeight:itemDict];
    CGRect rect = self.itemImageView.frame;
    rect.size.height = height;
    self.itemImageView.frame = rect;
    
    float width = ([UIScreen mainScreen].bounds.size.width - 4) / 2;
    float width1 = self.priceLabel.frame.size.width;
    float width2 = self.priceAfterDiscountLabel.frame.size.width;
    rect = self.priceAfterDiscountLabel.frame;
    rect.origin.x = width - width2 - 5;
    self.priceAfterDiscountLabel.frame = rect;
    
    rect = self.priceLabel.frame;
    rect.origin.x = self.priceAfterDiscountLabel.frame.origin.x - width1 - 5;
    self.priceLabel.frame = rect;
    
    [self updateViewFrame:self.priceLabel withBase:self.priceBaseY imageHeight:height];
    [self updateViewFrame:self.priceAfterDiscountLabel withBase:self.priceAfterDiscountBaseY imageHeight:height];
    [self updateViewFrame:self.shadowImageView withBase:self.shadowImageViewBaseY imageHeight:height];
}
- (void)updateViewFrame:(UIView*)view withBase:(float)base imageHeight:(float)imgHeight
{
    CGRect rect = view.frame;
    rect.origin.y = base + imgHeight;
    view.frame = rect;
}


+ (CGFloat)getHeight:(NSDictionary*)itemDict
{
    return 200;
}
+ (CGSize)getSize:(NSDictionary*)itemDict
{
    float height = [self getHeight:itemDict];
    float width = ([UIScreen mainScreen].bounds.size.width - 4) / 2;
    return CGSizeMake(width, height);
}
@end
