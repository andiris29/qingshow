//
//  QSBadgeBtnGroup.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/19/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSBadgeButton.h"

@class QSBadgeBtnGroup;

@protocol QSBadgeBtnGroupDelegate <NSObject>

- (void)btnGroup:(QSBadgeBtnGroup*)btnGroup didSelectType:(QSBadgeButtonType)type;

@end

@interface QSBadgeBtnGroup : UIView

- (instancetype)initWithTypes:(NSArray*)array;
- (void)triggerSelectType:(QSBadgeButtonType)type;
- (QSBadgeButton*)findBtnOfType:(QSBadgeButtonType)type;
@property (weak, nonatomic) NSObject<QSBadgeBtnGroupDelegate>* delegate;

@end
