//
//  QSCanvasImageView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCanvasImageView.h"

@interface QSCanvasImageView ()

@end

@implementation QSCanvasImageView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.userInteractionEnabled = YES;
        self.contentMode = UIViewContentModeScaleAspectFill;
        self.clipsToBounds = YES;
        
    }
    return self;
}





@end
