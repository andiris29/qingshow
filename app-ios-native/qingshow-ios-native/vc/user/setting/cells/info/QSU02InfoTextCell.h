//
//  QSU02UserSettingInfoCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSU02InfoBaseCell.h"

@interface QSU02InfoTextCell : QSU02InfoBaseCell<UITextFieldDelegate>

@property (weak, nonatomic) IBOutlet UILabel *typeLabel;
@property (weak, nonatomic) IBOutlet UITextField *infoTextField;

@end
