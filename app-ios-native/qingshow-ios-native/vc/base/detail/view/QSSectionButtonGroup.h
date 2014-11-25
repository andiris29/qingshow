//
//  QSSectionButtonGroup.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSSectionNumberTextButton.h"
#import "QSSectionImageTextButton.h"
#import "QSSectionTextButton.h"

@protocol QSSectionButtonGroupDelegate <NSObject>

- (void)groupButtonPressed:(int)index;
- (void)singleButtonPressed;

@end

typedef NS_ENUM(NSInteger, QSSectionButtonGroupType) {
    QSSectionButtonGroupTypeImage, QSSectionButtonGroupTypeText, QSSectionButtonGroupTypeThree
};


@interface QSSectionButtonGroup : UIView

@property (strong, nonatomic) NSArray* buttonGroup;
@property (strong, nonatomic) QSSectionButtonBase* singleButton;

@property (weak, nonatomic) NSObject<QSSectionButtonGroupDelegate>* delegate;

- (id)init;
- (id)initWithType:(QSSectionButtonGroupType)type;

- (void)setTitle:(NSString*)title atIndex:(int)index;
- (void)setNumber:(NSString*)numberStr atIndex:(int)index;
- (void)setSelect:(int)index;

@end
