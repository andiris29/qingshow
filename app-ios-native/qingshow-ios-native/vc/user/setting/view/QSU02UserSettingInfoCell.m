//
//  QSU02UserSettingInfoCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU02UserSettingInfoCell.h"
#import "QSPeopleUtil.h"
#import "QSUserManager.h"
#import "QSNetworkKit.h"
#import "QSDateUtil.h"
#import "UIViewController+ShowHud.h"
#import "QSBlock.h"

@implementation QSU02UserSettingInfoCell

- (void)awakeFromNib {
    // Initialization code
    _infoTextField.textColor = [UIColor grayColor];
    _infoTextField.delegate = self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
- (void)infoCellBindWithDic:(NSDictionary *)peopleDic
{
    if (self.row == 0) {
        self.typeLabel.text = @"昵称";
        self.infoTextField.placeholder = @"请填写昵称";
        if ([QSPeopleUtil getNickname:peopleDic]) {
            self.infoTextField.text = [QSPeopleUtil getNickname:peopleDic];
        }
        
    }
    else if (self.row == 1)
    {
        self.typeLabel.text = @"年龄";
        self.infoTextField.placeholder = @"请输入年龄";
        if ([QSPeopleUtil getAge:peopleDic]) {
            self.infoTextField.text = [NSString stringWithFormat:@"%@",[QSPeopleUtil getAge:peopleDic]];
        }

    }
    else if(self.row == 2)
    {
        self.typeLabel.text = @"身高";
        self.infoTextField.placeholder = @"请填写身高";
        if ([QSPeopleUtil getHeight:peopleDic]) {
            self.infoTextField.text = [NSString stringWithFormat:@"%@",[QSPeopleUtil getHeight:peopleDic]];
        }

    }
    else if (self.row == 3)
    {
        self.typeLabel.text = @"体重";
        self.infoTextField.placeholder = @"请输入体重";
        if ([QSPeopleUtil getWeight:peopleDic]) {
            self.infoTextField.text = [NSString stringWithFormat:@"%@",[QSPeopleUtil getWeight:peopleDic]];
        }

    }
    
}


@end
