//
//  QSU02UserSettingViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/8.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU02UserSettingViewController.h"
#import "QSU04EmailViewController.h"
#import "QSU05HairGenderTableViewController.h"
#import "QSU08PasswordViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSUserManager.h"
#import "UIViewController+QSExtension.h"
#import "QSImageEditingViewController.h"

#define UPLOAD_PORTRAIT 0
#define UPLOAD_BACKGROUND 1

typedef NS_ENUM(NSInteger, QSU02UserSettingViewControllerSelectType) {
    QSU02UserSettingViewControllerSelectTypeNone,
    QSU02UserSettingViewControllerSelectTypeGender,
    QSU02UserSettingViewControllerSelectTypeCamera
};

@interface QSU02UserSettingViewController ()

@property (assign, nonatomic) QSU02UserSettingViewControllerSelectType currentSelectType;
@property (strong, nonatomic) UIActionSheet* currentActionSheet;
@end

@implementation QSU02UserSettingViewController {
    
@private
    long _uploadImageType;
}

#pragma mark - private value

//UIDatePicker *datePicker;

#pragma mark - Method

- (void)awakeFromNib {
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initNavigation];
    self.currentActionSheet = QSU02UserSettingViewControllerSelectTypeNone;
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [self loadUserSetting];
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UITableViewDelegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [self hideKeyboardAndDatePicker];

    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSDictionary *people = [QSUserManager shareUserManager].userInfo;
    switch (indexPath.section) {
        case 0:
            // 选择section
            _uploadImageType = indexPath.row;
            if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
                UIActionSheet *sheet = [[UIActionSheet alloc] initWithTitle:@"选择图片"
                                                                   delegate:self
                                                          cancelButtonTitle:@"取消"
                                                     destructiveButtonTitle:nil
                                                          otherButtonTitles:@"从相册选择", @"使用相机拍照", nil];
                sheet.tag = 255;
                [self hideKeyboardAndDatePicker];
                [sheet showInView:self.view];
                self.currentActionSheet = sheet;
                self.currentSelectType = QSU02UserSettingViewControllerSelectTypeCamera;
                
            } else if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypePhotoLibrary]) {
                UIActionSheet *sheet = [[UIActionSheet alloc] initWithTitle:@"选择图片"
                                                                   delegate:self
                                                          cancelButtonTitle:@"取消"
                                                     destructiveButtonTitle:nil
                                                          otherButtonTitles:@"从相册选择", nil];
                sheet.tag = 255;
                [self hideKeyboardAndDatePicker];
                [sheet showInView:self.view];
                self.currentActionSheet = sheet;
                self.currentSelectType = QSU02UserSettingViewControllerSelectTypeCamera;
            } else {
                [self showErrorHudWithText:@"没有权限访问相册，请再设定里允许对相册进行访问"];
            }
            break;
        case 1:
            // 基本section
            if (indexPath.row == 1) {
                // GOTO Gender
                self.currentActionSheet = [[UIActionSheet alloc] initWithTitle:@"性别" delegate:self cancelButtonTitle:@"取消" destructiveButtonTitle:nil otherButtonTitles:@"男",@"女", nil];
                self.currentSelectType = QSU02UserSettingViewControllerSelectTypeGender;
                [self hideKeyboardAndDatePicker];
                [self.currentActionSheet showInView:self.view];
//                NSLog(@"GOTO Gender");
//                QSU05HairGenderTableViewController *vc = [[QSU05HairGenderTableViewController alloc]
//                                                          initWithNibName:@"QSU05HairGenderTableViewController"
//                                                                   bundle:nil];
//                
//                if (people[@"gender"] != nil) {
//                    NSArray *codes = [NSArray arrayWithObject:people[@"gender"]];
//                    vc.selectCodes = codes;
//                }
//                vc.codeTable = GENDER_LIST;
//                vc.delegate = self;
//                vc.codeType = CODE_TYPE_GENDER;
//                [self.navigationController pushViewController:vc animated:YES];
            } else if (indexPath.row == 2) {
                // 选择生日
            } else if (indexPath.row == 5) {
                // GOTO HairType
                NSLog(@"GOTO HairType");
                QSU05HairGenderTableViewController *vc = [[QSU05HairGenderTableViewController alloc]
                                                          initWithNibName:@"QSU05HairGenderTableViewController"
                                                                   bundle:nil];
                
                vc.codeTable = HAIR_LIST;
                vc.delegate = self;
                vc.codeType = CODE_TYPE_HAIR;
                if (people[@"hairTypes"] != nil) {
                    vc.selectCodes = people[@"hairTypes"];
                }
                [self.navigationController pushViewController:vc animated:YES];
            }
            
            break;
        case 2:
            // 其他section
            if (indexPath.row == 0) {
                // Change Password
                QSU08PasswordViewController *vc = [[QSU08PasswordViewController alloc]initWithNibName:@"QSU08PasswordViewController" bundle:nil];
                vc.delegate = self;
                [self.navigationController pushViewController:vc animated:YES];
//            } else if (indexPath.row == 1) {
//                // Change Email
//                QSU04EmailViewController *vc = [[QSU04EmailViewController alloc]initWithNibName:@"QSU04EmailViewController" bundle:nil];
//                vc.delegate = self;
//                [self.navigationController pushViewController:vc animated:YES];
            } else {
                NSLog(@"Nothing");
            }
            break;
        default:
            break;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    if (section == 2) {
        return 100.0f;
    }
    
    return [super tableView:tableView heightForFooterInSection:section];
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    if (section == 2) {
        UIView *footerView=[[UIView alloc]initWithFrame:CGRectMake(0, 0, 320, 100)];
        UIButton *addcharity=[UIButton buttonWithType:UIButtonTypeCustom];
        [addcharity setTitle:@"退出登陆" forState:UIControlStateNormal];
        [addcharity setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [addcharity addTarget:self action:@selector(actionLogout) forControlEvents:UIControlEventTouchUpInside];
        //[addcharity setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];//set the color this is may be different for iOS 7
        [addcharity setBackgroundColor:[UIColor colorWithRed:252.f/255.f green:103.f/255.f blue:105.f/255.f alpha:1.f]];
        addcharity.frame=CGRectMake(10, 25, 300, 50); //set some large width to ur title
        addcharity.layer.cornerRadius = addcharity.frame.size.height / 8;
        addcharity.layer.masksToBounds = YES;
        [footerView addSubview:addcharity];
        return footerView;
    }
    return [super tableView:tableView viewForFooterInSection:section];
}

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    
    if (self.currentSelectType == QSU02UserSettingViewControllerSelectTypeCamera) {
        if (buttonIndex != actionSheet.cancelButtonIndex) {
            //没有Camera时,cancelButtonIndex为1
            UIImagePickerController *imagePickerController = [[UIImagePickerController alloc] init];
            imagePickerController.delegate = self;
            if (buttonIndex == 0) {
                imagePickerController.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
            } else if (buttonIndex == 1) {
                
                imagePickerController.sourceType = UIImagePickerControllerSourceTypeCamera;
            }
            imagePickerController.allowsEditing = NO;
            [self presentViewController:imagePickerController animated:YES completion:^{}];
        }
    } else if (self.currentSelectType == QSU02UserSettingViewControllerSelectTypeGender) {
        if (buttonIndex != actionSheet.cancelButtonIndex) {
            [self updatePeopleEntityViewController:self byEntity:@{@"gender" : @(buttonIndex)} pop:NO];
        }
    }
    
    
    self.currentSelectType = QSU02UserSettingViewControllerSelectTypeNone;
    self.currentActionSheet = nil;
    

}

#pragma mark -  UIImagePickerControllerDelegate

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
//    [picker dismissViewControllerAnimated:YES completion:^{}];

    // Get Original Image from PhotoLibrary
    UIImage *image = [info objectForKey:UIImagePickerControllerEditedImage];
    if (!image) {
        image = [info objectForKeyedSubscript:UIImagePickerControllerOriginalImage];
    }
    
    QSImageEditingViewController* vc = nil;
    if (_uploadImageType == UPLOAD_PORTRAIT) {
        vc = [[QSImageEditingViewController alloc] initWithType:QSImageEditingViewControllerTypeHead image:image];
    } else {
        vc = [[QSImageEditingViewController alloc] initWithType:QSImageEditingViewControllerTypeBg image:image];
    }
    vc.delegate = self;
    [picker presentViewController:vc animated:YES completion:nil];
    
}

#pragma mark - ImageEditing
- (void)imageEditingUseImage:(UIImage *)image vc:(QSImageEditingViewController *)vc
{
    [self dismissViewControllerAnimated:YES completion:nil];
    MBProgressHUD* hud = [self showNetworkWaitingHud];
    // Success Handle
    EntitySuccessBlock success = ^(NSDictionary *people, NSDictionary *metadata) {
        [hud hide:YES];
        if (metadata[@"error"] == nil && people != nil) {
            [self showSuccessHudWithText:@"上传成功"];
            // refresh local login user's data
            [SHARE_NW_ENGINE getLoginUserOnSucced:nil onError:nil];
            [self refreshImage];
            //[self.navigationController popToViewController:self.navigationController.viewControllers[self.navigationController.viewControllers.count - 2] animated:YES];
        } else {
            [self showErrorHudWithText:@"上传失败"];
        }
    };
    
    // Error Handle
    ErrorBlock error = ^(NSError *error) {
        [hud hide:YES];
        [self handleError:error];
    };
    
    // Convert UIImage to NSData
    NSData *imageData = UIImageJPEGRepresentation(image, 0.5);
    // write NSData to sandbox
    //    NSString *fullPath = [[NSHomeDirectory() stringByAppendingPathComponent:@"Documents"] stringByAppendingPathComponent:@"uploadImage"];
    //    [imageData writeToFile:fullPath atomically:NO];
    if (_uploadImageType == UPLOAD_PORTRAIT) {
        [SHARE_NW_ENGINE updatePortrait:imageData onSuccess:success onError:error];
    } else {
        [SHARE_NW_ENGINE updateBackground:imageData onSuccess:success onError:error];
    }
}
- (void)cancelImageEditing:(QSImageEditingViewController *)vc
{
    [vc dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - Private Method

- (void)initNavigation {
    NSLog(@"initNavigation");
    self.navigationItem.title = @"设置";
    self.navigationItem.backBarButtonItem.title = @"";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
    UIBarButtonItem *btnSave = [[UIBarButtonItem alloc]initWithTitle:@"保存"
                                                               style:UIBarButtonItemStylePlain
                                                              target:self
                                                              action:@selector(actionSave)];
    
    [[self navigationItem] setRightBarButtonItem:btnSave];
}

- (void)loadUserSetting {
    
    NSDictionary *people = [QSUserManager shareUserManager].userInfo;
    self.nameText.text = (NSString *)people[@"name"];
    self.lengthText.text = [(NSNumber *)people[@"height"] stringValue];
    self.weightText.text = [(NSNumber *)people[@"weight"] stringValue];
    if (self.lengthText.text.length != 0) {
        self.lengthText.text = [NSString stringWithFormat:@"%@ cm", self.lengthText.text];
    }
    if (self.weightText.text.length != 0) {
        self.weightText.text = [NSString stringWithFormat:@"%@ kg", self.weightText.text];
    }
    if (people[@"birthtime"] == nil) {
        self.birthdayText.text = @"";
    } else {
        self.birthdayText.text = (NSString *)people[@"birthtime"];
    }
    
    self.portraitImage.layer.cornerRadius = self.portraitImage.frame.size.height / 2;
    self.portraitImage.layer.masksToBounds = YES;
    
    self.backgroundImage.layer.cornerRadius = self.backgroundImage.frame.size.height / 2;
    self.backgroundImage.layer.masksToBounds = YES;
    
    // Get Portrait & Backgrund's Image
    [self refreshImage];
    
    self.brandText.text = (NSString *)people[@"brand"];
}

- (void)updateBirthDayLabel:(NSDate *)birthDay {
    NSDateFormatter *formatter = [[NSDateFormatter alloc]init];
    [formatter setDateFormat:@"yyyy/MM/dd"];
    self.birthdayText.text = [formatter stringFromDate:birthDay];
}

// Update Peoples
- (void) updatePeopleEntityViewController: (UIViewController *)vc byEntity:(NSDictionary *)entity pop:(BOOL)fPop
{
    EntitySuccessBlock success = ^(NSDictionary *people, NSDictionary *metadata){
        if (metadata[@"error"] == nil && people != nil) {
            [vc showSuccessHudWithText:@"更新成功"];
            [SHARE_NW_ENGINE getLoginUserOnSucced:nil onError:nil];
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

- (void)refreshImage {
    
    NSDictionary *people = [QSUserManager shareUserManager].userInfo;
    if (people[@"portrait"] != nil) {
        NSString *portaits = people[@"portrait"];
        [self.portraitImage setImageFromURL:[NSURL URLWithString:portaits]];
    } else {
        [self.portraitImage setImage:[UIImage imageNamed:@"nav_btn_account"]];
    }
    
    if (people[@"background"] != nil) {
        NSString *background = people[@"background"];
        [self.backgroundImage setImageFromURL:[NSURL URLWithString:background]];
    } else {
        [self.backgroundImage setBackgroundColor:[UIColor blackColor]];
    }
}

#pragma mark - Action
- (void)actionSave {
    NSString *name = self.nameText.text;
    NSString *birthDay = self.birthdayText.text;
    NSString *length = self.lengthText.text;
    NSString *weight = self.weightText.text;
    NSString *brand = self.brandText.text;
    
    if (length.length != 0) {
        length = [length stringByReplacingOccurrencesOfString:@" cm" withString:@""];
    }
    
    if (weight.length != 0) {
        weight = [weight stringByReplacingOccurrencesOfString:@" kg" withString:@""];
    }
    
    [self updatePeopleEntityViewController:self byEntity:@{@"name": name, @"birthtime": birthDay, @"height": length, @"weight": weight, @"brand": brand}];
}

- (void)changeDate:(id)sender {
    UIDatePicker *datePicker = (UIDatePicker *)self.birthdayText.inputView;
    [self updateBirthDayLabel:datePicker.date];
}

- (void)actionLogout {
    NSLog(@"logout");
    VoidBlock succss = ^ {
        [QSUserManager shareUserManager].userInfo = nil;
        [QSUserManager shareUserManager].fIsLogined = NO;
        [self.navigationController popToRootViewControllerAnimated:YES];
    };
    [SHARE_NW_ENGINE logoutOnSucceed:succss onError:nil];
}

#pragma mark - U04,U05,U08's Delegate
- (void)passwordViewController:(QSU08PasswordViewController *)vc didSavingPassword:(NSString *)newPassword needCurrentPassword:(NSString *)curPasswrod {
    [self updatePeopleEntityViewController:vc byEntity:@{@"password":newPassword, @"currentPassword": curPasswrod}];
}

- (void)emailViewController:(QSU04EmailViewController *)vc didSavingEmail:(NSString *)email {
    [self updatePeopleEntityViewController:vc byEntity:@{@"email": email}];
}

- (void)codeUpdateViewController:(QSU05HairGenderTableViewController *)vc forCodeType:(NSString *)codeType bySelectedCode:(NSArray *)codes {
    if ([codeType compare:CODE_TYPE_GENDER] == NSOrderedSame) {
        // Update Gender
        [self updatePeopleEntityViewController:vc byEntity:@{vc.codeType: codes[0]}];
    } else {
        // Update HairType
        NSMutableString *hairTypes = [[NSMutableString alloc]init];
        for (int i = 0; i < codes.count; i++) {
            [hairTypes appendString:[NSString stringWithFormat:@"%@", codes[i]]];
            if (i < (codes.count - 1)) {
                [hairTypes appendString:@","];
            }
        }
        [self updatePeopleEntityViewController:vc byEntity:@{vc.codeType: hairTypes}];
    }
}

#pragma mark - Action

- (IBAction)lengthEditingDidBegin:(id)sender {
    if (self.lengthText.text.length == 0) {
        return;
    }
    self.lengthText.text = [self.lengthText.text stringByReplacingOccurrencesOfString:@" cm" withString:@""];
}

- (IBAction)lengthEditingDidEnd:(id)sender {
    if (self.lengthText.text.length == 0) {
        return;
    }
    self.lengthText.text = [NSString stringWithFormat:@"%@ cm", self.lengthText.text];
}

- (IBAction)weightEditingDidBegin:(id)sender {
    if (self.weightText.text.length == 0) {
        return;
    }
    self.weightText.text = [self.weightText.text stringByReplacingOccurrencesOfString:@" kg" withString:@""];
}

- (IBAction)weightEditingDidEnd:(id)sender {
    if (self.weightText.text.length == 0) {
        return;
    }
    self.weightText.text = [NSString stringWithFormat:@"%@ kg", self.weightText.text];
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    UIDatePicker *datePicker = [[UIDatePicker alloc] init];
    datePicker.datePickerMode = UIDatePickerModeDate;
    [datePicker setLocale:[NSLocale currentLocale]];
    if (self.birthdayText.text.length == 0) {
        [datePicker setDate:[NSDate date]];
    } else {
        NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
        [dateFormat setDateFormat:@"yyyy/MM/dd"];
        NSDate *date = [dateFormat dateFromString:self.birthdayText.text];
        [datePicker setDate:date];
    }
    [datePicker addTarget:self action:@selector(changeDate:) forControlEvents:UIControlEventValueChanged];
    
    if (textField.tag == 11) {
        self.birthdayText.inputView = datePicker;
        [self updateBirthDayLabel:datePicker.date];
    }
    
}

- (void)hideKeyboardAndDatePicker
{
    NSArray* a = @[self.birthdayText, self.nameText, self.lengthText, self.weightText];
    for (UIView* view in a) {
        [view resignFirstResponder];
    }
}
- (void)didTapTableView:(UITapGestureRecognizer*)ges
{
    [self hideKeyboardAndDatePicker];
}
@end
