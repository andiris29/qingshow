//
//  QSNumberPageControl.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/17/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSNumberPageControl : UIView

- (instancetype)init;

@property (assign, nonatomic) int currentPage;
@property (assign, nonatomic) int numberOfPages;

@end
