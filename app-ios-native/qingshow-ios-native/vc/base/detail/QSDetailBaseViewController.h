//
//  QSModelDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSBadgeView.h"

#import "QSShowCollectionViewProvider.h"

@interface QSDetailBaseViewController : UIViewController <QSShowProviderDelegate, QSBadgeBtnGroupDelegate>

@property (assign, nonatomic) int currentSection;
@property (strong, nonatomic) NSArray* viewArray;
@property (strong, nonatomic) QSBadgeView* badgeView;
@property (strong, nonatomic) NSDictionary* userInfo;

- (void)configContentInset;
@end
