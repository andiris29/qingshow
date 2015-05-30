//
//  QSGlobalFirstLaunchViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSGlobalFirstLaunchViewController : UIViewController

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *dateLabel;
@property (weak, nonatomic) IBOutlet UILabel *noLabel;

- (instancetype)init;

- (IBAction)closeBtnPressed:(id)sender;
@end
