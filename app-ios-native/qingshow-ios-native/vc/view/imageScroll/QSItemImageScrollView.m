//
//  QSItemImageScrollView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSItemImageScrollView.h"

@implementation QSItemImageScrollView

@dynamic delegate;

- (void)awakeFromNib
{
    [super awakeFromNib];
    self.pageControl.hidden = YES;
}

- (int)getViewCount
{
    return (int)(self.imageUrlArray.count + 2) / 3;
}
- (UIView*)getViewForPage:(int)imageIndex
{
    QSItemContainerView* imageView = [QSItemContainerView generateView];
    imageView.delegate = self;
    return imageView;
}
- (void)updateView:(UIView*)view forPage:(int)imageIndex
{
    QSItemContainerView* imageView = (QSItemContainerView*)view;
    imageView.delegate = self;
    if (self.imageUrlArray) {
        int location = imageIndex * 3;
        NSInteger length = self.imageUrlArray.count - location < 3 ? self.imageUrlArray.count - location : 3;
        [imageView bindWithImageUrl:[self.imageUrlArray subarrayWithRange:NSMakeRange(location, length)]];
    }
}
#pragma mark - Getter And Setter Method
- (void)setImageUrlArray:(NSArray *)imageUrlArray
{
    _imageUrlArray = imageUrlArray;
    [self updateImages];
}

#pragma mark - QSItemContainerViewDelegate
- (void)didTapImageIndex:(int)index ofView:(QSItemContainerView*)view
{
    NSInteger viewIndex = [self.imageViewArray indexOfObject:view];
    int count = [self getViewCount];
    if (viewIndex == 0) {
        viewIndex = count - 1;
    } else if (viewIndex == self.imageViewArray.count - 1) {
        viewIndex = 0;
    } else {
        viewIndex -= 1;
    }
    NSInteger urlIndex = viewIndex * 3 + index;
    if ([self.delegate respondsToSelector:@selector(didTapItemAtIndex:)]) {
        [self.delegate didTapItemAtIndex:(int)urlIndex];
    }
}

@end
