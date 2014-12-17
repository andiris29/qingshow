//
//  QSSingleImageScrollView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
@class QSImageScrollViewBase;
@protocol QSImageScrollViewBaseDelegate <NSObject>
@optional
- (void)imageScrollView:(QSImageScrollViewBase*)view didChangeToPage:(int)page;

@end

typedef NS_ENUM(NSInteger, QSImageScrollViewDirection) {
QSImageScrollViewDirectionHor, QSImageScrollViewDirectionVer
};

@interface QSImageScrollViewBase : UIView<UIScrollViewDelegate>

+ (QSImageScrollViewBase*)generateView;

- (int)getViewCount;
- (UIView*)getViewForPage:(int)page;
- (void)updateImages;
@property (weak, nonatomic) NSObject<QSImageScrollViewBaseDelegate>* delegate;

@property (strong, nonatomic) IBOutlet UIPageControl* pageControl;
@property (strong, nonatomic) NSMutableArray* imageViewArray;

- (id)initWithFrame:(CGRect)frame;
- (id)initWithFrame:(CGRect)frame direction:(QSImageScrollViewDirection)d;

@end
