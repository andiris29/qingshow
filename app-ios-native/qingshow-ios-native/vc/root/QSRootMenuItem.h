//
//  QSRootMenuItem.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/2/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSRootMenuItemType.h"
#define QSRootMenuItemWidth 78.f
#define QSRootMenuItemHeight 132.f


@class QSRootMenuItem;

@protocol QSRootMenuItemDelegate <NSObject>

- (void)menuItemPressed:(QSRootMenuItem*)item;

@end

@interface QSRootMenuItem : UIView

@property (strong, nonatomic) IBOutlet UIButton* button;
@property (strong, nonatomic) IBOutlet UILabel* label;
@property (strong, nonatomic) IBOutlet UIImageView* dotView;
@property (readonly, nonatomic) int type;

@property (weak, nonatomic) NSObject<QSRootMenuItemDelegate>* delegate;

+ (QSRootMenuItem*)generateItemWithType:(QSRootMenuItemType)type;

- (IBAction)buttonPressed:(id)sender;
- (void)showDot;
- (void)hideDot;
- (void)setHover:(BOOL)fHover;
@end
