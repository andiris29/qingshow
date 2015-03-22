//
//  QSU02UserSettingViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/8.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU02UserSettingViewController.h"
#import "QSU04EmailViewController.h"
#import "QSU08PasswordViewController.h"
#import "QSU09OrderListViewController.h"
#import "QSU10ReceiverListViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSUserManager.h"
#import "UIViewController+QSExtension.h"
#import "QSImageEditingViewController.h"
#import "QSPeopleUtil.h"
#import "QSDateUtil.h"
#import "UIImage+fixOrientation.h"
#define UPLOAD_PORTRAIT 0
#define UPLOAD_BACKGROUND 1
#define PAGE_ID @"U02 - 个人设置"

typedef NS_ENUM(NSInteger, QSU02UserSettingViewControllerSelectType) {
    QSU02UserSettingViewControllerSelectTypeNone,
    QSU02UserSettingViewControllerSelectTypeGender,
    QSU02UserSettingViewControllerSelectTypeCamera,
    QSU02UserSettingViewControllerSelectTypeHairType
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
    self.birthdayText.delegate = self;
    self.nameText.delegate = self;
    self.lengthText.delegate = self;
    self.weightText.delegate = self;
    self.brandText.delegate = self;
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [self loadUserSetting];
    [MobClick beginLogPageView:PAGE_ID];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UITextFieldDelegate
-(void)textFieldDidEndEditing:(UITextField *)textField {
    [self hideKeyboardAndDatePicker];
    NSString *value = textField.text;
    if (value.length == 0) {
        return;
    }
    NSDictionary *currentProfile = [QSUserManager shareUserManager].userInfo;
    if (textField == self.nameText) {
        if ([value compare:currentProfile[@"name"]] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self byEntity:@{@"name": value} pop:NO];
        }
    } else if (textField == self.birthdayText) {
        NSDate *date = [QSDateUtil buildDateFromResponseString:(NSString *)currentProfile[@"birthday"]];
        NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"yyyy/MM/dd"];
        NSString* birth= [dateFormatter stringFromDate:date];
        if ([value compare:birth] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self byEntity:@{@"birthday": value} pop:NO];
        }
    } else if (textField == self.lengthText) {
        if (value .length != 0) {
            value = [value stringByReplacingOccurrencesOfString:@" cm" withString:@""];
        }
        if ([value compare:currentProfile[@"length"]] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self byEntity:@{@"height": value} pop:NO];
        }
    } else if (textField == self.weightText) {
        if (value.length != 0) {
            value = [value stringByReplacingOccurrencesOfString:@" kg" withString:@""];
        }
        if ([value compare:currentProfile[@"weight"]] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self byEntity:@{@"weight": value} pop:NO];
        }
    } else if (textField == self.brandText) {
        if ([value compare:currentProfile[@"favoriteBrand"]] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self byEntity:@{@"favoriteBrand": value} pop:NO];
        }
    }
}

#pragma mark - UITableViewDelegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [self hideKeyboardAndDatePicker];

    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    switch (indexPath.section) {
        case 0:
        {
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
        }
        case 1:
        {
            UIViewController* vc = nil;
            if (indexPath.row == 0) {
                vc = [[QSU09OrderListViewController alloc] init];
                [self.navigationController pushViewController:vc animated:YES];
            } else if (indexPath.row == 1) {
                vc = [[QSU10ReceiverListViewController alloc] init];
                [self.navigationController pushViewController:vc animated:YES];
            }
            break;
        }
        case 2:
        {
            // 基本section
            if (indexPath.row == 1) {
                // GOTO Gender
                self.currentActionSheet = [[UIActionSheet alloc] initWithTitle:@"性别" delegate:self cancelButtonTitle:@"取消" destructiveButtonTitle:nil otherButtonTitles:GENDER_LIST, nil];
                self.currentSelectType = QSU02UserSettingViewControllerSelectTypeGender;
                [self hideKeyboardAndDatePicker];
                [self.currentActionSheet showInView:self.view];
            } else if (indexPath.row == 2) {
                // 选择生日
            } else if (indexPath.row == 5) {
                // GOTO HairType
                self.currentActionSheet = [[UIActionSheet alloc]initWithTitle:@"发型" delegate:self cancelButtonTitle:@"取消" destructiveButtonTitle:nil otherButtonTitles:HAIR_LIST, nil];
                self.currentSelectType = QSU02UserSettingViewControllerSelectTypeHairType;
                [self hideKeyboardAndDatePicker];
                [self.currentActionSheet showInView:self.view];
            }
            
            break;
        }
        case 3:
        {
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
        }
        default:
        {
            break;
        }
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    if (section == 3) {
        return 100.0f;
    }
    
    return [super tableView:tableView heightForFooterInSection:section];
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    if (section == 3) {
        UIView *footerView=[[UIView alloc]initWithFrame:CGRectMake(0, 0, 320, 100)];
        UIButton *addcharity=[UIButton buttonWithType:UIButtonTypeCustom];
        [addcharity setTitle:@"退出登陆" forState:UIControlStateNormal];
        [addcharity setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [addcharity addTarget:self action:@selector(actionLogout) forControlEvents:UIControlEventTouchUpInside];
        //[addcharity setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];//set the color this is may be different for iOS 7
        [addcharity setBackgroundColor:[UIColor colorWithRed:128.f/255.f green:128.f/255.f blue:128.f/255.f alpha:1.f]];
        CGRect screenBound = [[UIScreen mainScreen] bounds];
        CGSize screenSize = screenBound.size;
        CGFloat screenWidth = screenSize.width;
        addcharity.frame=CGRectMake(10, 25, screenWidth - 20 , 50); //set some large width to ur title
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
    } else if (self.currentSelectType == QSU02UserSettingViewControllerSelectTypeHairType) {
        if (buttonIndex != actionSheet.cancelButtonIndex) {
            [self updatePeopleEntityViewController:self byEntity:@{@"hairType": @(buttonIndex)} pop:NO];
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
    
    image = [image fixOrientation];
    
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
    if (_uploadImageType == UPLOAD_PORTRAIT) {
        [SHARE_NW_ENGINE updatePortrait:imageData onSuccess:success onError:error];
    } else {
        [SHARE_NW_ENGINE updateBackground:imageData onSuccess:success onError:error];
    }
}
- (void)cancelImageEditing:(QSImageEditingViewController *)vc
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - Private Method

- (void)initNavigation {
    NSLog(@"initNavigation");
    self.navigationItem.title = @"设置";
    self.navigationItem.backBarButtonItem.title = @"";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStyleDone target:nil action:nil];
    
    [[self navigationItem] setBackBarButtonItem:backButton];
}

- (void)loadUserSetting {
    
    NSDictionary *people = [QSUserManager shareUserManager].userInfo;
    self.nameText.text = (NSString *)people[@"name"];
    
    // TODO Unify these npe check to QSPeopleUtil?
    self.lengthText.text = [QSPeopleUtil getHeight:people];
    if (self.lengthText.text.length != 0) {
        self.lengthText.text = [NSString stringWithFormat:@"%@ cm", self.lengthText.text];
    }
    self.weightText.text = [QSPeopleUtil getWeight:people];
    if (self.weightText.text.length != 0) {
        self.weightText.text = [NSString stringWithFormat:@"%@ kg", self.weightText.text];
    }
    if (people[@"birthday"] == nil) {
        self.birthdayText.text = @"";
    } else {
        [self updateBirthDayLabel:[QSDateUtil buildDateFromResponseString:(NSString *)people[@"birthday"]]];
    }
    
    self.portraitImage.layer.cornerRadius = self.portraitImage.frame.size.height / 2;
    self.portraitImage.layer.masksToBounds = YES;
    
    self.backgroundImage.layer.cornerRadius = self.backgroundImage.frame.size.height / 2;
    self.backgroundImage.layer.masksToBounds = YES;
    
    // Get Portrait & Backgrund's Image
    [self refreshImage];
    
    self.shoeSizeLabel.text = [QSPeopleUtil getShoeSizeDesc:people];
    self.clothingSizeLabel.text = [QSPeopleUtil getClothingSizeDesc:people];
    self.brandText.text = (NSString *)people[@"favoriteBrand"];
    
    self.genderLabel.text = [QSPeopleUtil getGenderDesc:people];
    self.hairTypeLabel.text = [QSPeopleUtil getHairTypeDesc:people];
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
            EntitySuccessBlock successLoad = ^(NSDictionary *people, NSDictionary *metadata) {
                [self loadUserSetting];
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

- (void)refreshImage {
    
    NSDictionary *people = [QSUserManager shareUserManager].userInfo;
    [self.portraitImage setImageFromURL:[QSPeopleUtil getHeadIconUrl:people]];
    [self.backgroundImage setImageFromURL:[QSPeopleUtil getBackgroundUrl:people]];
    /*
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
     */
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
    if (textField != self.birthdayText) {
        return;
    }
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
    NSArray* a = @[self.birthdayText, self.nameText, self.lengthText, self.weightText, self.brandText];
    for (UIView* view in a) {
        [view resignFirstResponder];
    }
}

- (void)didTapTableView:(UITapGestureRecognizer*)ges
{
    [self hideKeyboardAndDatePicker];
}
@end
