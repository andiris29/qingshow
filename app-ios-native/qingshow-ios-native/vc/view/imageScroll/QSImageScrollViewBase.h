//
//  QSSingleImageScrollView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSImageScrollViewBase : UIView<UIScrollViewDelegate>

+ (QSImageScrollViewBase*)generateView;

- (int)getViewCount;
- (UIView*)getViewForPage:(int)page;
- (void)updateImages;
@property (strong, nonatomic) NSMutableArray* imageViewArray;

@end
