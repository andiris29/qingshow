//
//  QSSectionButtonBase.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSSectionButtonBase : UIView

+ (QSSectionButtonBase*)generateView;

@property (assign, nonatomic) BOOL selected;

@property (weak, nonatomic) IBOutlet UILabel *textLabel;

@end
