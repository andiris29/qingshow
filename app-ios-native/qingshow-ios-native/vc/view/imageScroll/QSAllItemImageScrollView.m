//
//  QSAllItemImageScrollView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/17/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSAllItemImageScrollView.h"
#import "QSSingleImageScrollView.h"
#import "QSItemUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@implementation QSAllItemImageScrollView
- (id)initWithFrame:(CGRect)frame direction:(QSImageScrollViewDirection)d
{
    self = [super initWithFrame:frame direction:d];
    if (self) {
        self.pageControl.hidden = YES;
    }
    return self;
}
- (id)initWithFrame:(CGRect)frame
{
    self = [self initWithFrame:frame direction:QSImageScrollViewDirectionVer];
    if (self) {
        self.pageControl.hidden = YES;
    }
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
}
- (int)getViewCount
{
    return (int)self.itemsArray.count;
}
- (UIView*)getViewForPage:(int)imageIndex
{
    QSSingleImageScrollView* imageView = [[QSSingleImageScrollView alloc] initWithFrame:self.bounds];
    if (self.itemsArray) {
#warning 需要改成多个url
        imageView.imageUrlArray = @[[QSItemUtil getCoverUrl:self.itemsArray[imageIndex]], [QSItemUtil getCoverUrl:self.itemsArray[imageIndex]]];
    }
    imageView.translatesAutoresizingMaskIntoConstraints = YES;
    imageView.pageControl.hidden = YES;
    return imageView;
}
- (void)updateView:(UIView*)view forPage:(int)imageIndex
{
    QSSingleImageScrollView* imageView = (QSSingleImageScrollView*)view;
    if (self.itemsArray) {
#warning 需要改成多个url
        imageView.imageUrlArray = @[[QSItemUtil getCoverUrl:self.itemsArray[imageIndex]]];
    }
}

#pragma mark - Getter And Setter Method
- (void)setItemsArray:(NSArray *)itemsArray
{
    _itemsArray = itemsArray;
    [self updateImages];
}

@end
