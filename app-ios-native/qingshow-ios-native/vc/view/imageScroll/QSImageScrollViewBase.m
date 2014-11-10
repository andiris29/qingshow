//
//  QSSingleImageScrollView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSImageScrollViewBase.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@interface QSImageScrollViewBase ()

@property (strong, nonatomic) IBOutlet UIScrollView* scrollView;
@property (strong, nonatomic) IBOutlet UIPageControl* pageControl;

@property (strong, nonatomic) NSMutableArray* imageViewArray;

@end

@implementation QSImageScrollViewBase

#pragma mark - virtual method
- (int)getViewCount
{
    return 0;
}
- (UIView*)getViewForPage:(int)page
{
    return nil;
}
#pragma mark - Static Method
+ (QSImageScrollViewBase*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSImageScrollViewBase" bundle:nil];
    return [nib instantiateWithOwner:self options:nil][0];
}

#pragma mark - Life cycle
- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.translatesAutoresizingMaskIntoConstraints = NO;
        self.imageViewArray = [@[] mutableCopy];
        self.scrollView = [[UIScrollView alloc] initWithFrame:self.frame];
        self.scrollView.showsHorizontalScrollIndicator = NO;
        self.scrollView.showsVerticalScrollIndicator = NO;
        self.scrollView.pagingEnabled = YES;
        self.scrollView.delegate = self;
        [self addSubview:self.scrollView];
        
        self.pageControl = [[UIPageControl alloc] initWithFrame:CGRectZero];
        [self addSubview:self.pageControl];
        self.pageControl.center = CGPointMake(self.scrollView.frame.size.width / 2, self.scrollView.frame.size.height - 20);
    }
    return self;
}



#pragma mark - Layout
- (void)updateImages
{
    int imageCount = [self getViewCount];

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
        UIView* imageView = [self getViewForPage:imageIndex];
        imageView.frame = CGRectMake(i * size.width, 0, size.width, size.height);
        [self.scrollView addSubview:imageView];
        [self.imageViewArray addObject:imageView];
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
        self.scrollView.contentOffset = CGPointMake(size.width, 0);
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
    self.scrollView.contentOffset = CGPointMake(currentPage * size.width, 0);
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
