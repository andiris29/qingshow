//
//  QSTradeSelectButton.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/18/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger, QSTradeSelectButtonType) {
    QSTradeSelectButtonTypeText,
    QSTradeSelectButtonTypeImage
};

@interface QSTradeSelectButton : UIControl

@property (assign, nonatomic) BOOL isSelected;
@property (assign, nonatomic) QSTradeSelectButtonType type;
@property (strong, nonatomic) NSString* text;
@property (strong, nonatomic) NSURL* imageUrl;
- (id)init;


+ (CGFloat)getWidthWithText:(NSString*)text;

@end
