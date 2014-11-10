//
//  QSSingleImageScrollView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSSingleImageScrollView.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@interface QSSingleImageScrollView ()

@property (strong, nonatomic) IBOutlet UIScrollView* scrollView;
@property (strong, nonatomic) IBOutlet UIPageControl* pageControl;

@property (strong, nonatomic) NSMutableArray* imageViewArray;

@end

@implementation QSSingleImageScrollView
#pragma mark - Static Method
+ (QSSingleImageScrollView*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSSingleImageScrollView" bundle:nil];
    return [nib instantiateWithOwner:self options:nil][0];
}

#pragma mark - Life cycle
- (void)awakeFromNib
{
    self.imageViewArray = [@[] mutableCopy];
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


#pragma mark - Layout
- (void)updateImages
{
    int imageCount = self.imageArray ? self.imageArray.count : self.imageUrlArray.count;
    int imageViewCount = [self resetPageAndScrollViewContentSize:imageCount];

    for (UIImageView* imageView in self.imageViewArray) {
        [imageView removeFromSuperview];
    }
    [self.imageViewArray removeAllObjects];
    
    CGSize size = self.scrollView.bounds.size;
    for (int i = 0 ; i < imageViewCount; i++) {
        int imageIndex = 0;
        if (i == 0) {
            imageIndex = imageCount - 1;
        } else if (i == imageViewCount - 1) {
            imageIndex = 0;
        } else {
            imageIndex = i - 1;
        }
        UIImageView* imageView = [[UIImageView alloc] initWithFrame:CGRectMake(i * size.width, 0, size.width, size.height)];
        [self.scrollView addSubview:imageView];
        [self.imageViewArray addObject:imageView];
        if (self.imageArray) {
            UIImage* image = self.imageArray[imageIndex];
            imageView.image = image;
        } else if (self.imageUrlArray) {
            NSURL* imageUrl = self.imageUrlArray[imageIndex];
            [imageView setImageFromURL:imageUrl];
        }
    }

    
}
- (int)resetPageAndScrollViewContentSize:(int)count
{
    int retCount = 0;
    CGSize size = self.scrollView.bounds.size;
    self.pageControl.numberOfPages = count;
    self.pageControl.currentPage = 0;
    if (count <= 1) {
        self.pageControl.hidden = YES;
        self.scrollView.contentSize = size;
        self.scrollView.scrollEnabled = NO;
        retCount = count;
        self.scrollView.contentOffset = CGPointZero;
    }
    else {
        self.pageControl.hidden = NO;
        self.scrollView.contentSize=  CGSizeMake(size.width * (count + 2), size.height);
        self.scrollView.scrollEnabled = YES;
        self.scrollView.contentOffset = CGPointMake(size.width + 1, 0);
        retCount = count + 2;
    }
    return retCount;
}


- (void)layoutSubviews
{
    [super layoutSubviews];
    self.scrollView.frame = self.frame;
}

#pragma mark - UIScrollView Delegate
- (void)updateOffsetAndPage
{
    if (self.imageViewArray.count == 1) {
        return;
    }
    
    CGPoint offset = self.scrollView.contentOffset;
    CGSize size = self.scrollView.bounds.size;

    int currentIndex = offset.x / size.width;
    int currentPage = currentIndex;
    if (currentIndex == 0) {
        currentPage = self.imageViewArray.count - 2;
    } else if (currentIndex == self.imageViewArray.count - 1) {
        currentPage = 1;
    }
    self.pageControl.currentPage = currentPage - 1;
    self.scrollView.contentOffset = CGPointMake(currentPage * size.width + 1, 0);
}


- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    [self updateOffsetAndPage];
}
- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView
                  willDecelerate:(BOOL)decelerate
{
    if (!decelerate) {
        [self updateOffsetAndPage];
    }

}
@end
