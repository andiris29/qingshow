//
//  QSU14FavoriteViewController.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSRootContentViewController.h"
#import "QSU14FavoTableViewProvider.h"

@interface QSU14FavoriteViewController : QSRootContentViewController <QSU14FavoTableViewProviderDelegate>

- (instancetype)init;

@end
