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
@property (assign, nonatomic) QSImageScrollViewDirection direction;

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
- (void)updateView:(UIView*)view forPage:(int)page
{
}
- (void)emptyView:(UIView*)view forPage:(int)page
{

}
#pragma mark - Static Method
+ (QSImageScrollViewBase*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSImageScrollViewBase" bundle:nil];
    return [nib instantiateWithOwner:self options:nil][0];
}

#pragma mark - Life cycle
- (id)initWithFrame:(CGRect)frame direction:(QSImageScrollViewDirection)d {
    self = [super initWithFrame:frame];
    if (self) {
        self.enableLazyLoad = NO;
        self.direction = d;
        
        self.imageViewArray = [@[] mutableCopy];
        self.scrollView = [[UIScrollView alloc] initWithFrame:self.bounds];
        self.scrollView.showsHorizontalScrollIndicator = NO;
        self.scrollView.showsVerticalScrollIndicator = NO;
        self.scrollView.pagingEnabled = YES;
        self.scrollView.delegate = self;
        [self addSubview:self.scrollView];
        
        self.pageControl = [[QSNumberPageControl alloc] init];
        [self addSubview:self.pageControl];
    }
    return self;
}
- (id)initWithFrame:(CGRect)frame
{
    return [self initWithFrame:frame direction:QSImageScrollViewDirectionHor];
}

#pragma mark - Layout
- (void)updateImages
{
    [self updateImagesWithLazyLoad:self.enableLazyLoad];
}
- (void)updateImagesWithLazyLoad:(BOOL)isLazyLoad
{
    int imageCount = [self getViewCount];

    int imageViewCount = [self resetPageAndScrollViewContentSize:imageCount];
    
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
        
        UIView* imageView = nil;
        
        if (self.imageViewArray.count > i) {
            imageView = self.imageViewArray[i];

        } else {
            imageView = [self getViewForPage:imageIndex];
            imageView.userInteractionEnabled = YES;
            UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(_didTapImgView:)];
            [imageView addGestureRecognizer:ges];
            [self.scrollView addSubview:imageView];
            [self.imageViewArray addObject:imageView];
        }
        if (!isLazyLoad || imageIndex == self.pageControl.currentPage) {
            [self updateView:imageView forPage:imageIndex];
        } else {
            [self emptyView:imageView forPage:imageIndex];
        }
        
        if (self.direction == QSImageScrollViewDirectionHor) {
            imageView.frame = CGRectMake(i * size.width, 0, size.width, size.height);
        } else {
            imageView.frame = CGRectMake(0, i * size.height, size.width, size.height);
        }

    }
    while (self.imageViewArray.count > imageViewCount) {
        UIView* v = [self.imageViewArray lastObject];
        [v removeFromSuperview];
        [self.imageViewArray removeLastObject];
    }
    self.scrollView.contentInset = UIEdgeInsetsZero;
}

- (int)resetPageAndScrollViewContentSize:(int)count
{
    int retCount = 0;
    CGSize size = self.scrollView.bounds.size;
    self.pageControl.numberOfPages = count;
    self.scrollView.contentInset = UIEdgeInsetsZero;
    if (count <= 1) {
        self.pageControl.hidden = YES;
        self.scrollView.contentSize = size;
        self.scrollView.scrollEnabled = NO;
        retCount = count;
        self.scrollView.contentOffset = CGPointZero;
    }
    else {
        self.pageControl.hidden = NO;
        self.scrollView.scrollEnabled = YES;
        if (self.direction == QSImageScrollViewDirectionHor) {
            self.scrollView.contentSize=  CGSizeMake(size.width * (count + 2), size.height);
            self.scrollView.contentInset = UIEdgeInsetsZero;
            if (self.scrollView.contentOffset.x < size.width || self.scrollView.contentOffset.x > size.width * (count + 1))
            {
                self.scrollView.contentOffset = CGPointMake(size.width, 0);
            }
        } else {
            self.scrollView.contentSize=  CGSizeMake(size.width, size.height * (count + 2));
            if (self.scrollView.contentOffset.y < size.height || self.scrollView.contentOffset.y > size.height * (count + 1))
            {
                self.scrollView.contentOffset = CGPointMake(0, size.height);
            }
        }
        retCount = count + 2;
    }
    [self updateOffsetAndPage];
    self.scrollView.contentInset = UIEdgeInsetsZero;
    return retCount;
}


- (void)layoutSubviews
{
    [super layoutSubviews];
    self.scrollView.frame = self.bounds;
    [self updateImages];

   
    self.pageControl.center = CGPointMake(self.scrollView.frame.size.width / 2, self.scrollView.frame.size.height - 105 - self.pageControlOffsetY);
    self.scrollView.contentInset = UIEdgeInsetsZero;
}

#pragma mark - UIScrollView Delegate
- (void)updateOffsetAndPage
{
    if (self.imageViewArray.count == 1) {
        return;
    }
    
    CGPoint offset = self.scrollView.contentOffset;
    CGSize size = self.scrollView.bounds.size;
    int currentIndex = 0;
    if (self.direction == QSImageScrollViewDirectionHor) {
        currentIndex = offset.x / size.width;
    } else {
        currentIndex = offset.y / size.height;
    }
    
    int currentPage = currentIndex;
    if (currentIndex == 0) {
        currentPage = (int)(self.imageViewArray.count - 2);
    } else if (currentIndex == self.imageViewArray.count - 1) {
        currentPage = 1;
    }
    self.pageControl.currentPage = currentPage - 1;
    
    if (self.enableLazyLoad && self.imageViewArray.count > currentPage) {
        UIView* imgView = self.imageViewArray[currentIndex];
        [self updateView:imgView forPage:(int)self.pageControl.currentPage];
    }
    
    if ([self.delegate respondsToSelector:@selector(imageScrollView:didChangeToPage:)]) {
        [self.delegate imageScrollView:self didChangeToPage:(int)self.pageControl.currentPage];
    }
    self.scrollView.contentInset = UIEdgeInsetsZero;
    if (self.direction == QSImageScrollViewDirectionHor) {
        self.scrollView.contentOffset = CGPointMake(currentPage * size.width, 0);
    } else {
        self.scrollView.contentOffset = CGPointMake(0, currentPage * size.height);
    }
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
- (void)scrollToPage:(int)page
{
    int currentPage = page + 1;
    CGSize size = self.scrollView.bounds.size;
    self.pageControl.currentPage = page;
    
    if (self.direction == QSImageScrollViewDirectionHor) {
        self.scrollView.contentOffset = CGPointMake(currentPage * size.width, 0);
    } else {
        self.scrollView.contentOffset = CGPointMake(0, currentPage * size.height);
    }
    
}

- (void)loadAllImages
{
    [self updateImagesWithLazyLoad:NO];
}

- (void)_didTapImgView:(UITapGestureRecognizer*)ges {
    if ([self.delegate respondsToSelector:@selector(imageScrollViewDidTapImgView:)]) {
        [self.delegate imageScrollViewDidTapImgView:self];
    }
}
@end
