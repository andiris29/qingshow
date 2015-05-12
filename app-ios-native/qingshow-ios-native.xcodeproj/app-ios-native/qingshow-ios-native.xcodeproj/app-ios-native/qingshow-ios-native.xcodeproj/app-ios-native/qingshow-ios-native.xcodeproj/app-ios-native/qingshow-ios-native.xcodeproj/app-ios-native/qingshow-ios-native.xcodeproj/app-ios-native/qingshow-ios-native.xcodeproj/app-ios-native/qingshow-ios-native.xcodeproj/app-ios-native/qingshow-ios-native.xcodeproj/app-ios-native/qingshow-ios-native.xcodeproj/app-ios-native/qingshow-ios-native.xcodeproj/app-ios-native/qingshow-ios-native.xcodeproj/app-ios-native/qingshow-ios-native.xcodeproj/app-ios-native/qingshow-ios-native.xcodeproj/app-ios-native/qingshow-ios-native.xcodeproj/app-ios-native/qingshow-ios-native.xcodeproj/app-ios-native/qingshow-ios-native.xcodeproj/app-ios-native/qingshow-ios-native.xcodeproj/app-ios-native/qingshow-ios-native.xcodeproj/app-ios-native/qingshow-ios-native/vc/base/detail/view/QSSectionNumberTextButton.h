//
//  QSSectionButton.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSSectionButtonBase.h"

@interface QSSectionNumberTextButton : QSSectionButtonBase
@property (weak, nonatomic) IBOutlet UILabel *numberLabel;


+ (QSSectionNumberTextButton*)generateView;

@end
