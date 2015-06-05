//
//  QSU02UserSettingInfoCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU02InfoTextCell.h"
#import "QSPeopleUtil.h"
#import "QSUserManager.h"
#import "QSNetworkKit.h"
#import "QSDateUtil.h"
#import "UIViewController+ShowHud.h"
#import "QSBlock.h"
#import "UINib+QSExtension.h"

@implementation QSU02InfoTextCell

- (void)awakeFromNib {
    // Initialization code
    _infoTextField.textColor = [UIColor grayColor];
    _infoTextField.delegate = self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)bindWithUser:(NSDictionary *)peopleDict
{

    self.typeLabel.text = u02InfoTypeToTitle(self.rowType);
    switch (self.rowType) {
        case U02SectionInfoRowName: {
            self.infoTextField.placeholder = @"请填写昵称";
            if ([QSPeopleUtil getNickname:peopleDict]) {
                self.infoTextField.text = [QSPeopleUtil getNickname:peopleDict];
            }
            break;
        }
        case U02SectionInfoRowAge: {
            self.infoTextField.placeholder = @"请输入年龄";
            if ([QSPeopleUtil getAge:peopleDict]) {
                self.infoTextField.text = [QSPeopleUtil getAge:peopleDict];
            }
            break;
        }
        case U02SectionInfoRowHeight: {
            self.infoTextField.placeholder = @"请填写身高";
            if ([QSPeopleUtil getHeight:peopleDict]) {
                self.infoTextField.text = [NSString stringWithFormat:@"%@",[QSPeopleUtil getHeight:peopleDict]];
            }
        }
        case U02SectionInfoRowWeight: {
            self.infoTextField.placeholder = @"请输入体重";
            if ([QSPeopleUtil getWeight:peopleDict]) {
                self.infoTextField.text = [NSString stringWithFormat:@"%@",[QSPeopleUtil getWeight:peopleDict]];
            }
        }
        default:
            break;
    }
}
@end
