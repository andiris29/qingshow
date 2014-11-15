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

@protocol QSSectionButtonGroupDelegate <NSObject>

- (void)groupButtonPressed:(int)index;
- (void)singleButtonPressed;

@end


@interface QSSectionButtonGroup : UIView

- (id)init;
@property (strong, nonatomic) NSArray* buttonGroup;
@property (strong, nonatomic) QSSectionImageTextButton* singleButton;


- (void)setTitle:(NSString*)title atIndex:(int)index;
- (void)setNumber:(NSString*)numberStr atIndex:(int)index;
- (void)setSelect:(int)index;

@property (weak, nonatomic) NSObject<QSSectionButtonGroupDelegate>* delegate;


@end
