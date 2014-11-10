//
//  QSItemImageScrollView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSItemImageScrollView.h"
#import "QSItemContainerView.h"


@implementation QSItemImageScrollView

- (int)getViewCount
{
    return (self.imageUrlArray.count + 2) / 3;
}
- (UIView*)getViewForPage:(int)imageIndex
{
    QSItemContainerView* imageView = [QSItemContainerView generateView];
    if (self.imageUrlArray) {
        int location = imageIndex * 3;
        int length = self.imageUrlArray.count - location < 3 ? self.imageUrlArray.count - location : 3;
        [imageView bindWithImageUrl:[self.imageUrlArray subarrayWithRange:NSMakeRange(location, length)]];
    }
    return imageView;
}
#pragma mark - Getter And Setter Method
- (void)setImageUrlArray:(NSArray *)imageUrlArray
{
    _imageUrlArray = imageUrlArray;
    [self updateImages];
}

@end
