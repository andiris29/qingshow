//
//  QSU02UserSettingViewController.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSU04EmailViewController.h"
#import "QSU08PasswordViewController.h"
#import "QSImageEditingViewController.h"
#import "QSU02UserSettingInfoCell.h"
@protocol QSMenuProviderDelegate;

#define CODE_TYPE_GENDER @"gender"
#define CODE_TYPE_HAIR @"hairTypes"
#define GENDER_LIST @"男性", @"女性"
#define HAIR_LIST @"所有", @"长发", @"超长发", @"中长发",@"短发"
@interface QSU02UserSettingViewController03 : UIViewController<QSU04EmailViewControllerDelegate, QSU08PasswordViewControllerDelegate, UIActionSheetDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UIScrollViewDelegate, QSImageEditingViewControllerDelegate, UITextFieldDelegate,QSU02SettingInfoCellDelegate>


@property (weak, nonatomic) NSObject<QSMenuProviderDelegate>* menuProvider;

@end
