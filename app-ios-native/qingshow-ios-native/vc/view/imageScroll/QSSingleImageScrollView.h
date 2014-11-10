//
//  QSSingleImageScrollView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSImageScrollViewBase.h"

@interface QSSingleImageScrollView : QSImageScrollViewBase

@property (strong, nonatomic) NSArray* imageArray;
@property (strong, nonatomic) NSArray* imageUrlArray;

@end
