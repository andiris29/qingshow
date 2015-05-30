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
            self.infoTextField.text = [NSString stringWithFormat:@"%@ 岁",[QSPeopleUtil getAge:peopleDic]];
        }

    }
    else if(self.row == 2)
    {
        self.typeLabel.text = @"身高";
        self.infoTextField.placeholder = @"请填写身高";
        if ([QSPeopleUtil getHeight:peopleDic]) {
            self.infoTextField.text = [NSString stringWithFormat:@"%@ cm",[QSPeopleUtil getHeight:peopleDic]];
        }

    }
    else if (self.row == 3)
    {
        self.typeLabel.text = @"体重";
        self.infoTextField.placeholder = @"请输入体重";
        if ([QSPeopleUtil getWeight:peopleDic]) {
            self.infoTextField.text = [NSString stringWithFormat:@"%@ kg",[QSPeopleUtil getWeight:peopleDic]];
        }

    }
    
}
#warning expected upload to Server
#pragma mark - UITextFieldDelegate
-(void)textFieldDidEndEditing:(UITextField *)textField {
    [self hideKeyboardAndDatePicker];
    NSString *value = textField.text;
    if (value.length == 0) {
        return;
    }
    NSDictionary *currentProfile = [QSUserManager shareUserManager].userInfo;
    if (self.row == 0) {
        if ([value compare:currentProfile[@"nickname"]] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self.superVC byEntity:@{@"nickname": value} pop:NO];
        }
    } else if (self.row == 1) {
        NSDate *date = [QSDateUtil buildDateFromResponseString:(NSString *)currentProfile[@"age"]];
        NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"yyyy/MM/dd"];
        NSString* birth= [dateFormatter stringFromDate:date];
        if ([value compare:birth] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self.superVC byEntity:@{@"birthday": value} pop:NO];
            if (value.length != 0) {
                [self updatePeopleEntityViewController:self.superVC byEntity:@{@"age": value} pop:NO];
            }
        }
    } else if (self.row == 2) {
        if (value .length != 0) {
            value = [value stringByReplacingOccurrencesOfString:@" cm" withString:@""];
        }
        if ([value compare:currentProfile[@"length"]] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self.superVC byEntity:@{@"height": value} pop:NO];
        }
    } else if (self.row == 3) {
        if (value.length != 0) {
            value = [value stringByReplacingOccurrencesOfString:@" kg" withString:@""];
        }
        if ([value compare:currentProfile[@"weight"]] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self.superVC byEntity:@{@"weight": value} pop:NO];
        }
    }
    //else if (textField == self.bodyTpye) {
//        if ([value compare:currentProfile[@"bodyType"]] != NSOrderedSame) {
//            [self updatePeopleEntityViewController:self byEntity:@{@"bodyType": value} pop:NO];
//        } else if (textField == self.dressTpye){
//            if ([value compare:currentProfile[@"dressStyle"]] != NSOrderedSame) {
//                [self updatePeopleEntityViewController:self byEntity:@{@"dressStyle": value} pop:NO];
//            } else if (textField == self.expectationTpye){
//                //                if ([value compare:currentProfile[@""]]) {
//                //                    <#statements#>
//                //                }
//            }
//        }
//    }
    
}

- (void)updatePeopleEntityViewController: (UIViewController *)vc byEntity:(NSDictionary *)entity pop:(BOOL)fPop
{
    EntitySuccessBlock success = ^(NSDictionary *people, NSDictionary *metadata){
        if (metadata[@"error"] == nil && people != nil) {
            [vc showSuccessHudWithText:@"更新成功"];
            EntitySuccessBlock successLoad = ^(NSDictionary *people, NSDictionary *metadata) {
                [self.delegate tableViewReloadDataForInfoCell];
            };
            [SHARE_NW_ENGINE getLoginUserOnSucced:successLoad onError:nil];
            if (fPop) {
                [vc.navigationController popToViewController:vc.navigationController.viewControllers[vc.navigationController.viewControllers.count - 2] animated:YES];
            }
            
        } else {
            [vc showErrorHudWithText:@"更新失败"];
        }
    };
    
    ErrorBlock error = ^(NSError *error) {
        if (error.userInfo[@"error"] != nil) {
            NSNumber *errorCode = (NSNumber *)error.userInfo[@"error"];
            if (errorCode != nil) {
                [vc showErrorHudWithText:@"更新失败，请确认输入的内容"];
            }
        } else {
            [vc showErrorHudWithText:@"网络连接失败"];
        }
    };
    
    [SHARE_NW_ENGINE updatePeople:entity onSuccess:success onError:error];
}

- (void) updatePeopleEntityViewController: (UIViewController *)vc byEntity:(NSDictionary *)entity {
    [self updatePeopleEntityViewController:vc byEntity:entity pop:YES];
}

- (void)hideKeyboardAndDatePicker
{
    
    [self.infoTextField resignFirstResponder];
}

@end
