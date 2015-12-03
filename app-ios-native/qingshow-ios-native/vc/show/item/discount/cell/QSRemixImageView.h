//
//  QSRemixImageView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/1.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSRemixImageView : UIView
@property (strong, nonatomic) UIImageView* imageView;
@property (strong, nonatomic) NSDictionary* userInfo;
@property (assign, nonatomic) BOOL isSelected;
@end
