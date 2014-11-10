//
//  QSSingleImageScrollView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSSingleImageScrollView : UIView<UIScrollViewDelegate>

@property (strong, nonatomic) NSArray* imageArray;
@property (strong, nonatomic) NSArray* imageUrlArray;

+ (QSSingleImageScrollView*)generateView;

@end
