//
//  QSRootMenuItem.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/2/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#define QSRootMenuItemWidth 65.f
#define QSRootMenuItemHeight 120.f

typedef NS_ENUM(NSUInteger, QSRootMenuItemType) {
    
    QSRootMenuItemMy = 0,
    QSRootMenuItemMeida = 1,
    QSRootMenuItemMyFavor = 2,
    QSRootMenuItemSetting = 3
};


@class QSRootMenuItem;

@protocol QSRootMenuItemDelegate <NSObject>

- (void)menuItemPressed:(QSRootMenuItem*)item;

@end

@interface QSRootMenuItem : UIView

@property (strong, nonatomic) IBOutlet UIButton* button;
@property (strong, nonatomic) IBOutlet UILabel* label;
@property (readonly, nonatomic) int type;

@property (weak, nonatomic) NSObject<QSRootMenuItemDelegate>* delegate;

+ (QSRootMenuItem*)generateItemWithType:(QSRootMenuItemType)type;

- (IBAction)buttonPressed:(id)sender;


@end
