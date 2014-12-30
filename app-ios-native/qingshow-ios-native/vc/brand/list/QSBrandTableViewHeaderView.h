//
//  QSBrandTableViewHeaderView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/20/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol QSBrandTableViewHeaderViewDelegate <NSObject>

- (void)didClickOnline;
- (void)didClickOffline;

@end

@interface QSBrandTableViewHeaderView : UIView

+ (QSBrandTableViewHeaderView*)generateView;

@property (strong, nonatomic) IBOutlet UIButton* onlineBtn;
@property (strong, nonatomic) IBOutlet UIButton* offlineBtn;
@property (weak, nonatomic) NSObject<QSBrandTableViewHeaderViewDelegate>* delegate;

- (IBAction)onlineBtnPressed;
- (IBAction)offlineBtnPressed;

@end
