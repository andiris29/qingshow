//
//  QSCanvasImageView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSCanvasImageView : UIView
@property (strong, nonatomic) NSString* categoryId;
@property (strong, nonatomic) UIImageView* imgView;
@property (assign, nonatomic) BOOL hover;

- (BOOL)judgeIsHitRemoveButton:(CGPoint)p;

@end
