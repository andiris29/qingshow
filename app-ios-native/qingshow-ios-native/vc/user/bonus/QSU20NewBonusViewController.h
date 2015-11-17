//
//  QSU20NewBonusViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/1.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSUInteger, QSU20NewBonusViewControllerState) {
    QSU20NewBonusViewControllerStateParticipant,
    QSU20NewBonusViewControllerStateAbout
};

@interface QSU20NewBonusViewController : UIViewController

@property (strong, nonatomic) NSNumber* bonusIndex;


- (instancetype)initWithBonusIndex:(NSNumber*)bonusIndex state:(QSU20NewBonusViewControllerState)state;

@end
