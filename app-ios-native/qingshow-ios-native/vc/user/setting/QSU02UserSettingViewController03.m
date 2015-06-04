//
//  QSU02UserSettingViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU02UserSettingViewController03.h"
#import "QSU04EmailViewController.h"
#import "QSU08PasswordViewController.h"
#import "QSU09OrderListViewController.h"
#import "QSU10ReceiverListViewController.h"

#import "QSU07RegisterViewController.h"

#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSUserManager.h"
#import "UIViewController+QSExtension.h"
#import "QSImageEditingViewController.h"
#import "QSPeopleUtil.h"
#import "QSDateUtil.h"
#import "UIImage+fixOrientation.h"
#import "QSRootContainerViewController.h"

#import "QSU02UserSettingImgCell.h"
#import "QSU02UserSettingInfoCell.h"
#import "QSUserSettingPickerCell.h"
#import "QSU02UserChangeDressEffectViewController.h"
#define UPLOAD_PORTRAIT 0
#define UPLOAD_BACKGROUND 1
#define PAGE_ID @"U02 - 个人设置"

#define headImgCellId (@"headImageViewCellId")
#define defaultCellId (@"defaultCellId")
#define userInfoCellID (@"userInfoCellId")

#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)

typedef NS_ENUM(NSInteger, QSU02UserSettingViewControllerSelectType) {
    QSU02UserSettingViewControllerSelectTypeNone,
    QSU02UserSettingViewControllerSelectTypeGender,
    QSU02UserSettingViewControllerSelectTypeCamera,
    QSU02UserSettingViewControllerSelectTypeHairType
};
@interface QSU02UserSettingViewController03 ()<UITableViewDataSource,UITableViewDelegate>

@property(nonatomic,strong)NSString *userId;
@property (assign, nonatomic) QSU02UserSettingViewControllerSelectType currentSelectType;
@property (strong, nonatomic) UIActionSheet* currentActionSheet;

@property (strong, nonatomic) UIBarButtonItem* menuBtn;
@property (strong, nonatomic) UIBarButtonItem* menuBtnNew;
@end

@implementation QSU02UserSettingViewController03
{
    UITableView *_tableView;
    NSMutableArray *_dataArray;
    NSMutableArray *_textFieldArray;
    NSMutableArray *_cellArray;
    @private
    long _uploadImageType;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    
    
    
    [MobClick beginLogPageView:PAGE_ID];
    
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self initNavigation];
    _cellArray = [[NSMutableArray alloc]init];
    _dataArray = [[NSMutableArray alloc]init];
    _tableView = [[UITableView alloc]initWithFrame:self.view.bounds style:UITableViewStyleGrouped];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    [self.view addSubview:_tableView];
    [self configNavBar];
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}
- (void)initNavigation {
    NSLog(@"initNavigation");
    self.navigationItem.title = @"设置";
    self.navigationItem.backBarButtonItem.title = @"";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStyleDone target:nil action:nil];
    
    [[self navigationItem] setBackBarButtonItem:backButton];
}
- (void) updatePeopleEntityViewController: (UIViewController *)vc byEntity:(NSDictionary *)entity pop:(BOOL)fPop
{
    EntitySuccessBlock success = ^(NSDictionary *people, NSDictionary *metadata){
        if (metadata[@"error"] == nil && people != nil) {
            [vc showSuccessHudWithText:@"更新成功"];
            EntitySuccessBlock successLoad = ^(NSDictionary *people, NSDictionary *metadata) {
                [_tableView reloadData];
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
#pragma mark - Action
//- (void)actionSave {
//    NSString *name = self.nameText.text;
//    NSString *length = self.lengthText.text;
//    NSString *weight = self.weightText.text;
//    
//    if (length.length != 0) {
//        length = [length stringByReplacingOccurrencesOfString:@" cm" withString:@""];
//    }
//    
//    if (weight.length != 0) {
//        weight = [weight stringByReplacingOccurrencesOfString:@" kg" withString:@""];
//    }
//    
//    [self updatePeopleEntityViewController:self byEntity:@{@"name": name, @"height": length, @"weight": weight}];
//}

- (void)actionLogout {
    NSLog(@"logout");
    VoidBlock succss = ^ {
        [QSUserManager shareUserManager].userInfo = nil;
        [QSUserManager shareUserManager].fIsLogined = NO;
        QSU07RegisterViewController *registerVC = [[QSU07RegisterViewController alloc]init];
        NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
        [ud setObject:@"YES" forKey:@"isPushFromU07"];
        [self.navigationController pushViewController:registerVC animated:YES];
    };
    
    
    [SHARE_NW_ENGINE logoutOnSucceed:succss onError:nil];
}

//#pragma mark - ImageEditing
//- (void)imageEditingUseImage:(UIImage *)image vc:(QSImageEditingViewController *)vc
//{
//    [self dismissViewControllerAnimated:YES completion:nil];
//    MBProgressHUD* hud = [self showNetworkWaitingHud];
//    // Success Handle
//    EntitySuccessBlock success = ^(NSDictionary *people, NSDictionary *metadata) {
//        [hud hide:YES];
//        if (metadata[@"error"] == nil && people != nil) {
//            [self showSuccessHudWithText:@"上传成功"];
//            // refresh local login user's data
//            [SHARE_NW_ENGINE getLoginUserOnSucced:nil onError:nil];
//            //[self refreshImage];
//        } else {
//            [self showErrorHudWithText:@"上传失败"];
//        }
//    };
//    
//    // Error Handle
//    ErrorBlock error = ^(NSError *error) {
//        [hud hide:YES];
//        [self handleError:error];
//    };
//    
//    // Convert UIImage to NSData
//    NSData *imageData = UIImageJPEGRepresentation(image, 0.5);
//    // write NSData to sandbox
//    if (_uploadImageType == UPLOAD_PORTRAIT) {
//        [SHARE_NW_ENGINE updatePortrait:imageData onSuccess:success onError:error];
//    } else {
//        [SHARE_NW_ENGINE updateBackground:imageData onSuccess:success onError:error];
//    }
//}
//- (void)cancelImageEditing:(QSImageEditingViewController *)vc
//{
//    [self dismissViewControllerAnimated:YES completion:nil];
//}

#pragma mark - UITextFieldDelegate

-(void)textFieldDidEndEditing:(UITextField *)textField {
    [self hideKeyboardAndDatePicker];
    NSString *value = textField.text;
//    if (value.length == 0) {
//        return;
//    }
    NSDictionary *currentProfile = [QSUserManager shareUserManager].userInfo;
    NSLog(@"curr = %@",currentProfile);
    if (textField.tag == 200) {
        if ([value compare:currentProfile[@"nickname"]] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self byEntity:@{@"nickname": value} pop:NO];
        }
      
    } else if (textField.tag == 201) {
//        NSDate *date = [QSDateUtil buildDateFromResponseString:(NSString *)currentProfile[@"age"]];
//        NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
//        [dateFormatter setDateFormat:@"yyyy/MM/dd"];
//        NSString* birth= [dateFormatter stringFromDate:date];
//        if ([value compare:birth] != NSOrderedSame) {
//            [self updatePeopleEntityViewController:self byEntity:@{@"birthday": value} pop:NO];
//            if (value.length != 0) {
//                [self updatePeopleEntityViewController:self byEntity:@{@"age": value} pop:NO];
//            }
//        }
        if ([value compare:[currentProfile[@"age"] stringValue]] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self byEntity:@{@"age":value} pop:NO];
        }
        
    } else if (textField.tag == 202) {
//        if (value .length != 0) {
//            value = [value stringByReplacingOccurrencesOfString:@" cm" withString:@""];
//        }
        if ([value compare:currentProfile[@"length"]] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self byEntity:@{@"height": value} pop:NO];
        }
        
    } else if (textField.tag == 203) {
//        if (value.length != 0) {
//            value = [value stringByReplacingOccurrencesOfString:@" kg" withString:@""];
//        }
        if ([value compare:[currentProfile[@"weight"] stringValue]] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self byEntity:@{@"weight": value} pop:NO];
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

//- (void)updatePeopleEntityViewController: (UIViewController *)vc byEntity:(NSDictionary *)entity pop:(BOOL)fPop
//{
//    EntitySuccessBlock success = ^(NSDictionary *people, NSDictionary *metadata){
//        if (metadata[@"error"] == nil && people != nil) {
//            [vc showSuccessHudWithText:@"更新成功"];
//            EntitySuccessBlock successLoad = ^(NSDictionary *people, NSDictionary *metadata) {
//                [self.delegate tableViewReloadDataForInfoCell];
//            };
//            [SHARE_NW_ENGINE getLoginUserOnSucced:successLoad onError:nil];
//            if (fPop) {
//                [vc.navigationController popToViewController:vc.navigationController.viewControllers[vc.navigationController.viewControllers.count - 2] animated:YES];
//            }
//            
//        } else {
//            [vc showErrorHudWithText:@"更新失败"];
//        }
//    };
//    
//    ErrorBlock error = ^(NSError *error) {
//        if (error.userInfo[@"error"] != nil) {
//            NSNumber *errorCode = (NSNumber *)error.userInfo[@"error"];
//            if (errorCode != nil) {
//                [vc showErrorHudWithText:@"更新失败，请确认输入的内容"];
//            }
//        } else {
//            [vc showErrorHudWithText:@"网络连接失败"];
//        }
//    };
//    
//    [SHARE_NW_ENGINE updatePeople:entity onSuccess:success onError:error];
//}
//
//
//
//

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

#pragma mark - U04,U05,U08's Delegate
- (void)passwordViewController:(QSU08PasswordViewController *)vc didSavingPassword:(NSString *)newPassword needCurrentPassword:(NSString *)curPasswrod {
    [self updatePeopleEntityViewController:vc byEntity:@{@"password":newPassword, @"currentPassword": curPasswrod}];
}
#pragma maek - changeDressEffectVCDelegate


- (void)emailViewController:(QSU04EmailViewController *)vc didSavingEmail:(NSString *)email {
    [self updatePeopleEntityViewController:vc byEntity:@{@"email": email}];
}
#pragma mark - UITableViewDataSource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 4;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    if (section == 0) {
        return 2;
    }
    else if(section == 1)
    {
        return 2;
    }
    else if(section == 2)
    {
        return 7;
    }
    else if (section == 3)
    {
        return 1;
    }
    return 1;
}
- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section{
    if (section == 3) {
        return 100.0f;
    }
    else
    {
        return 0.f;
    }
}
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 44.f;
}
- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    UIView *footerView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, 320, 100)];
    if (section == 3) {
        UIButton *logOutBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        logOutBtn.titleLabel.font = NEWFONT;
        [logOutBtn setTitle:@"退出登录" forState:UIControlStateNormal];
        [logOutBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [logOutBtn addTarget:self action:@selector(actionLogout) forControlEvents:UIControlEventTouchUpInside];
        [logOutBtn setBackgroundColor:[UIColor colorWithRed:128.f/255.f green:128.f/255.f blue:128.f/255.f alpha:1.f]];
        logOutBtn.frame = CGRectMake(10, 25, w-20, 50);
        logOutBtn.layer.cornerRadius = logOutBtn.bounds.size.height/8;
        logOutBtn.layer.masksToBounds = YES;
        [footerView addSubview:logOutBtn];
        
        return footerView;
    }
    else
    {
        return nil;
    }
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView *headerView = [[UIView alloc]initWithFrame:CGRectMake(0, -20, w, 44)];
    UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(8, 0, w, 44)];
    label.font = NEWFONT;
    if (section == 0) {
        label.text = @"选择图片";
        [headerView addSubview:label];
        
    }
    else if(section == 1)
    {
        label.text = @"管理";
        [headerView addSubview:label];
    }
    else if(section == 2)
    {
        label.text = @"基本信息";
        [headerView addSubview:label];
    }
    else
    {
        label.text = @"其他";
        [headerView addSubview:label];
    }
    headerView.backgroundColor = [UIColor whiteColor];
    return headerView;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary *peopleDic = [QSUserManager shareUserManager].userInfo;
    if (indexPath.section == 0) {
       
            QSU02UserSettingImgCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"QSU02UserSettingImgCell" owner:nil options:nil]lastObject];
            cell.row = indexPath.row;
            [cell imgCellBindWithDic:peopleDic];
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            return cell;
    }
    else if(indexPath.section == 1)
    {
        UITableViewCell *cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:defaultCellId];
        cell.textLabel.font = NEWFONT;
        if (indexPath.row == 1) {
            cell.textLabel.text = @"订单管理";
            cell.textLabel.frame = CGRectMake(8, 8, 50, 30);
        }
        else
        {
            cell.textLabel.text = @"收货地址管理";
        }
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        return cell;
    }
    else if(indexPath.section == 2)
    {

    
        if (indexPath.row < 4) {
            QSU02UserSettingInfoCell *cell = [[[NSBundle mainBundle]loadNibNamed:@"QSU02UserSettingInfoCell" owner:nil options:nil]lastObject];
            cell.row = indexPath.row;
            cell.infoTextField.delegate = self;
            [cell infoCellBindWithDic:peopleDic];
            cell.infoTextField.tag = 200+indexPath.row;
            [_cellArray addObject:cell];
            return cell;
        }
        else if(indexPath.row  == 4 || indexPath.row == 5)
        {
            QSUserSettingPickerCell *cell = [[[NSBundle mainBundle]loadNibNamed:@"QSUserSettingPickerCell" owner:nil options:nil]lastObject];
            cell.row = indexPath.row;
            [cell bindWithDic:peopleDic];
            return cell;
            
        }
        else
        {
            UITableViewCell *cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:@"dressEffectCellId"];
            cell.textLabel.font = NEWFONT;
            cell.textLabel.text = @"搭配效果";
            UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(100, 0, 200, 40)];
            label.text = [QSPeopleUtil getExpectationsDesc:peopleDic];
            label.textColor = [UIColor grayColor];
            [cell addSubview:label];
            return cell;
        }
       
    
    }
    else if (indexPath.section == 3)
    {
        UITableViewCell *cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:defaultCellId];
        cell.font = NEWFONT;
        cell.textLabel.text = @"更改密码";
        cell.textLabel.frame = CGRectMake(8, 8, 50, 30);
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        return cell;
    }
    return nil;
}
        
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
            if (indexPath.row == 6) {
                QSU02UserChangeDressEffectViewController *vc = [[QSU02UserChangeDressEffectViewController alloc]init];
                
                [self.navigationController pushViewController:vc animated:YES];
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





#pragma mark - loadData

- (void)didTapTableView:(UITapGestureRecognizer*)ges
{
    [self hideKeyboardAndDatePicker];
}

- (void)hideKeyboardAndDatePicker
{
    
    for (UIView* view in _textFieldArray) {
        [view resignFirstResponder];
    }
}
//- (void)refreshImage {
//    
//    NSDictionary *people = [QSUserManager shareUserManager].userInfo;
//    [self.portraitImage setImageFromURL:[QSPeopleUtil getHeadIconUrl:people]];
//    [self.backgroundImage setImageFromURL:[QSPeopleUtil getBackgroundUrl:people]];
//    /*
//     if (people[@"portrait"] != nil) {
//     NSString *portaits = people[@"portrait"];
//     [self.portraitImage setImageFromURL:[NSURL URLWithString:portaits]];
//     } else {
//     [self.portraitImage setImage:[UIImage imageNamed:@"nav_btn_account"]];
//     }
//     
//     if (people[@"background"] != nil) {
//     NSString *background = people[@"background"];
//     [self.backgroundImage setImageFromURL:[NSURL URLWithString:background]];
//     } else {
//     [self.backgroundImage setBackgroundColor:[UIColor blackColor]];
//     }
//     */
//}

#pragma mark - configureView
- (void)configNavBar
{
    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:89.f/255.f green:86.f/255.f blue:86.f/255.f alpha:1.f];
    UIImageView* titleImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 80, 40)];
    UILabel *label = [[UILabel alloc]init];
    label.text = @"个人设置";
    label.frame = CGRectMake(0, 0, 80, 40);
    [titleImageView addSubview:label];
    titleImageView.userInteractionEnabled = YES;
    UITapGestureRecognizer* tapGes = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapRootTitle)];
    tapGes.numberOfTapsRequired = 5;
    [titleImageView addGestureRecognizer:tapGes];
    
    self.navigationItem.titleView = titleImageView;
    
    
    NSDate* lastClickMenuDate = [QSUserManager shareUserManager].lastClickMenuDate;
    if (!lastClickMenuDate || [[NSDate date] timeIntervalSinceDate:lastClickMenuDate] >= 24 * 60 * 60) {
        self.navigationItem.leftBarButtonItem = self.menuBtnNew;
    } else {
        self.navigationItem.leftBarButtonItem = self.menuBtn;
    }
    
}

- (void)menuButtonPressed
{
    self.navigationItem.leftBarButtonItem = self.menuBtn;
    [QSUserManager shareUserManager].lastClickMenuDate = [NSDate date];
    if ([self.menuProvider respondsToSelector:@selector(didClickMenuBtn)]) {
        [self.menuProvider didClickMenuBtn];
    }
}

- (void)didTapRootTitle
{
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    
    [self showTextHud:[NSString stringWithFormat:@"version: %@", version]];
}


- (UIBarButtonItem*)menuBtn {
    if (!_menuBtn) {
        _menuBtn = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_menu"] style:UIBarButtonItemStylePlain target:self action:@selector(menuButtonPressed)];
    }
    return _menuBtn;
}
- (UIBarButtonItem*)menuBtnNew {
    if (!_menuBtnNew) {
        UIImageView* navBtnMenuNewImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_menu_new"]];
        UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(menuButtonPressed)];
        navBtnMenuNewImageView.userInteractionEnabled = YES;
        [navBtnMenuNewImageView addGestureRecognizer:ges];
        _menuBtnNew = [[UIBarButtonItem alloc] initWithCustomView:navBtnMenuNewImageView];;
    }
    return _menuBtnNew;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
