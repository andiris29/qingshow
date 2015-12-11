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

- (void)rootMenuItemPressedType:(QSRootMenuItemType)type oldType:(QSRootMenuItemType)oldType;
- (void)rootMenuViewDidTapBlankView;

@end

@interface QSRootMenuView : UIView<QSRootMenuItemDelegate>

+ (QSRootMenuView*)generateView;

- (void)showMenuAnimationComple:(VoidBlock)block;
- (void)hideMenuAnimationComple:(VoidBlock)block;

@property (weak, nonatomic) NSObject<QSRootMenuViewDelegate>* delegate;

@property (weak, nonatomic) IBOutlet UIImageView* bgImageView;
@property (weak, nonatomic) IBOutlet UIView* containerView;
- (IBAction)didTapView:(id)sender;

- (void)triggerItemTypePressed:(QSRootMenuItemType)type;
- (void)hoverItemType:(QSRootMenuItemType)type;
- (void)updateItemDot;

@end
