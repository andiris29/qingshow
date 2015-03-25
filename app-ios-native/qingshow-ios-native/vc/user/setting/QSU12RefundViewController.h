//
//  QSU12RefundViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/14/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSU12RefundViewController : UIViewController

- (instancetype)initWithDict:(NSDictionary*)orderDict;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *widthCon;

@end
