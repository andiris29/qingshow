//
//  QSRootMenuView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/2/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSRootMenuItem.h"
#import "QSBlock.h"
@protocol QSRootMenuViewDelegate <NSObject>

- (void)rootMenuItemPressedType:(int)type;

@end

@interface QSRootMenuView : UIView<QSRootMenuItemDelegate>

+ (QSRootMenuView*)generateView;

- (void)showMenuAnimationComple:(VoidBlock)block;
- (void)hideMenuAnimationComple:(VoidBlock)block;

@property (weak, nonatomic) NSObject<QSRootMenuViewDelegate>* delegate;

@end
