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
    imageView.contentMode = UIViewContentModeScaleAspectFill;
    imageView.clipsToBounds = YES;
    return imageView;
}
- (void)updateView:(UIView*)view forPage:(int)imageIndex
{
    UIImageView* imageView = (UIImageView*)view;
    if (self.imageArray) {
        UIImage* image = self.imageArray[imageIndex];
        imageView.image = image;
    } else if (self.imageUrlArray) {
        id imageInfo = self.imageUrlArray[imageIndex];
        if ([imageInfo isKindOfClass:[NSURL class]]) {
            NSURL* imageUrl = (NSURL*)imageInfo;
            [imageView setImageFromURL:imageUrl];
        } else if ([imageInfo isKindOfClass:[UIImage class]]){
            imageView.image = (UIImage*)imageInfo;
        }
    }
}
- (void)emptyView:(UIView*)view forPage:(int)page
{
    UIImageView* imageView = (UIImageView*)view;
    imageView.image = nil;
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
