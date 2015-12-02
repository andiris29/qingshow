//
//  QSPnsHandler.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/26.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@class QSRootContainerViewController;

@interface QSPnsHandler : NSObject <UIAlertViewDelegate>

@property (weak, nonatomic) QSRootContainerViewController* rootVc;
- (instancetype)initWithRootVc:(QSRootContainerViewController*)rootVc;

@end
