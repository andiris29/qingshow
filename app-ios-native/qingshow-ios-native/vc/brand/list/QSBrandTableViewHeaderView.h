//
//  QSBrandTableViewHeaderView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/20/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSBrandTableViewHeaderView : UIView

+ (QSBrandTableViewHeaderView*)generateView;

@property (strong, nonatomic) IBOutlet UIButton* onlineBtn;
@property (strong, nonatomic) IBOutlet UIButton* offlineBtn;

- (IBAction)onlineBtnPressed;
- (IBAction)offlineBtnPressed;

@end
