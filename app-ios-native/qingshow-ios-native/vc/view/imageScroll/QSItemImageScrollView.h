//
//  QSItemImageScrollView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSImageScrollViewBase.h"
#import "QSItemContainerView.h"

@protocol QSItemImageScrollViewDelegate <QSImageScrollViewBaseDelegate>
@optional
- (void)didTapItemAtIndex:(int)index;

@end

@interface QSItemImageScrollView : QSImageScrollViewBase<QSItemContainerViewDelegate>

//@property (strong, nonatomic) NSArray* imageArray;
@property (strong, nonatomic) NSArray* imageUrlArray;

@property (assign, nonatomic) NSObject<QSItemImageScrollViewDelegate>* delegate;

@end
