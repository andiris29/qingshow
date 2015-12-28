//
//  QSSingleImageScrollView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSNumberPageControl.h"

@class QSImageScrollViewBase;
@protocol QSImageScrollViewBaseDelegate <NSObject>
@optional
- (void)imageScrollView:(QSImageScrollViewBase*)view didChangeToPage:(int)page;
- (void)imageScrollViewDidTapImgView:(QSImageScrollViewBase*)view;
@end

typedef NS_ENUM(NSInteger, QSImageScrollViewDirection) {
QSImageScrollViewDirectionHor, QSImageScrollViewDirectionVer
};

@interface QSImageScrollViewBase : UIView<UIScrollViewDelegate>

//+ (QSImageScrollViewBase*)generateView;

- (int)getViewCount;
- (UIView*)getViewForPage:(int)page;
- (void)updateImages;
@property (assign, nonatomic) float pageControlOffsetY;
@property (weak, nonatomic) NSObject<QSImageScrollViewBaseDelegate>* delegate;

@property (strong, nonatomic) QSNumberPageControl* pageControl;
@property (strong, nonatomic) NSMutableArray* imageViewArray;
@property (assign, nonatomic) BOOL enableLazyLoad;

- (id)initWithFrame:(CGRect)frame;
- (id)initWithFrame:(CGRect)frame direction:(QSImageScrollViewDirection)d;

- (void)scrollToPage:(int)page;
- (void)loadAllImages;
@end
