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
    if (self.rowType == U02SectionInfoRowName) {
        self.infoTextField.keyboardType = UIKeyboardTypeDefault;
        
    } else {
        self.infoTextField.keyboardType = UIKeyboardTypeNumberPad;
        
    }
    
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
            break;
        }
        case U02SectionInfoRowWeight: {
            self.infoTextField.placeholder = @"请输入体重";
            if ([QSPeopleUtil getWeight:peopleDict]) {
                self.infoTextField.text = [NSString stringWithFormat:@"%@",[QSPeopleUtil getWeight:peopleDict]];
            }
            break;
        }
        case U02SectionInfoRowBust:{
            self.infoTextField.placeholder = @"请输入胸围";
            if ([QSPeopleUtil getBust:peopleDict]) {
                self.infoTextField.text = [NSString stringWithFormat:@"%@",[QSPeopleUtil getBust:peopleDict]];
            }
            break;
        }
        case U02SectionInfoRowShouler:{
            self.infoTextField.placeholder = @"请输入肩宽";
            if ([QSPeopleUtil getShoulder:peopleDict]) {
                self.infoTextField.text = [NSString stringWithFormat:@"%@",[QSPeopleUtil getShoulder:peopleDict]];
            }
            break;
        }
        case U02SectionInfoRowWaist:{
            self.infoTextField.placeholder = @"请输入腰围";
            if ([QSPeopleUtil getWaist:peopleDict]) {
                self.infoTextField.text = [NSString stringWithFormat:@"%@",[QSPeopleUtil getWaist:peopleDict]];
            }
            break;
        }
        case U02SectionInfoRowHips:{
            self.infoTextField.placeholder = @"请输入臀围";
            if ([QSPeopleUtil getHips:peopleDict]) {
                self.infoTextField.text = [NSString stringWithFormat:@"%@",[QSPeopleUtil getHips:peopleDict]];
            }
            break;
        }



        default:{
            break;
        }
    }
}

-(void)textFieldDidEndEditing:(UITextField *)textField {
    NSString* key = nil;
    NSString* value = self.infoTextField.text;
    
    switch (self.rowType) {
        case U02SectionInfoRowName: {
            key = @"nickname";
            break;
        }
        case U02SectionInfoRowAge: {
            key = @"age";
            break;
        }
        case U02SectionInfoRowHeight: {
            key = @"height";
            break;
        }
        case U02SectionInfoRowWeight: {
            key = @"weight";
            break;
        }
        case U02SectionInfoRowBust: {
            key = @"measureInfo.bust";
            break;
        }
        case U02SectionInfoRowShouler: {
            key = @"measureInfo.shoulder";
            break;
        }
        case U02SectionInfoRowWaist: {
            key = @"measureInfo.waist";
            break;
        }
        case U02SectionInfoRowHips: {
            key = @"measureInfo.hips";
            break;
        }
        default:{
            break;
        }
    }
    [self.delegate updateUserInfoKey:key value:value];
}

- (void)resignKeyboardAndPicker {
    [self.infoTextField resignFirstResponder];
}
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self.infoTextField resignFirstResponder];
    return YES;
}
@end
