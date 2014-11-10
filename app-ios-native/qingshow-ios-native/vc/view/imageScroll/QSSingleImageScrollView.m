//
//  QSSingleImageScrollView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSSingleImageScrollView.h"
#import "UIImageView+MKNetworkKitAdditions.h"
@implementation QSSingleImageScrollView
- (int)getViewCount
{
    int imageCount = self.imageArray ? self.imageArray.count : self.imageUrlArray.count;
    return imageCount;
}
- (UIView*)getViewForPage:(int)imageIndex
{
    UIImageView* imageView = [[UIImageView alloc] init];
    if (self.imageArray) {
        UIImage* image = self.imageArray[imageIndex];
        imageView.image = image;
    } else if (self.imageUrlArray) {
        NSURL* imageUrl = self.imageUrlArray[imageIndex];
        [imageView setImageFromURL:imageUrl];
    }
    return imageView;
}
#pragma mark - Getter And Setter Method
- (void)setImageArray:(NSArray *)imageArray
{
    _imageArray = imageArray;
    _imageUrlArray = nil;
    [self updateImages];
}

- (void)setImageUrlArray:(NSArray *)imageUrlArray
{
    _imageUrlArray = imageUrlArray;
    _imageArray = nil;
    [self updateImages];
}

@end
