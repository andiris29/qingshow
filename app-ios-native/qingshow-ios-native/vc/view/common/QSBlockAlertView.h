//
//  QSBlockAlertView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 8/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSBlock.h"
@interface QSBlockAlertView : UIAlertView

@property (strong, nonatomic) VoidBlock succeedHandler;
@property (strong, nonatomic) VoidBlock cancelHandler;

@end
