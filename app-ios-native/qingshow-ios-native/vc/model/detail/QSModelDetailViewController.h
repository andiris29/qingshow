//
//  QSModelDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSModelDetailViewController : UIViewController
@property (weak, nonatomic) IBOutlet UIView *badgeContainer;
@property (weak, nonatomic) IBOutlet UIView *contentContainer;

- (id)initWithModel:(NSDictionary*)peopleDict;

@end
