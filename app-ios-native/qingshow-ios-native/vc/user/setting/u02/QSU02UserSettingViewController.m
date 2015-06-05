//
//  QSU02UserSettingViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU02UserSettingViewController.h"
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

#import "QSU02InfoBaseCell.h"
#import "QSU02ImgCell.h"
#import "QSU02ManagerCell.h"
#import "QSU02OtherCell.h"

#import "QSU02UserChangeDressEffectViewController.h"
#import "UIViewController+QSExtension.h"
#import "QSBlock.h"


#define UPLOAD_PORTRAIT 0
#define UPLOAD_BACKGROUND 1
#define PAGE_ID @"U02 - 个人设置"

#define headImgCellId (@"headImageViewCellId")
#define defaultCellId (@"defaultCellId")
#define userInfoCellID (@"userInfoCellId")

#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)

@interface QSU02UserSettingViewController ()<UITableViewDataSource,UITableViewDelegate>

@property(nonatomic,strong)NSString *userId;

@property (strong, nonatomic) UIView* footerView;


@property (strong, nonatomic) NSArray* headerViews;
@property (strong, nonatomic) NSMutableArray* cellArrays;       //[[]]
@property (strong, nonatomic) NSArray* sectionModelArray;
@property (strong, nonatomic) NSArray* rowModelArray;
@end

@implementation QSU02UserSettingViewController
{
    long _uploadImageType;
}

@synthesize headerViews = _headerViews;
#pragma mark - Getter And Setter
- (UIView*)footerView{
    if (!_footerView) {
        _footerView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, 320, 100)];
        UIButton *logOutBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        logOutBtn.titleLabel.font = NEWFONT;
        [logOutBtn setTitle:@"退出登录" forState:UIControlStateNormal];
        [logOutBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [logOutBtn addTarget:self action:@selector(actionLogout) forControlEvents:UIControlEventTouchUpInside];
        [logOutBtn setBackgroundColor:[UIColor colorWithRed:128.f/255.f green:128.f/255.f blue:128.f/255.f alpha:1.f]];
        logOutBtn.frame = CGRectMake(10, 25, w-20, 50);
        logOutBtn.layer.cornerRadius = logOutBtn.bounds.size.height/8;
        logOutBtn.layer.masksToBounds = YES;
        [_footerView addSubview:logOutBtn];
    }
    return _footerView;
}


#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self configNavigation];
    

    self.sectionModelArray = @[@(U02SectionImage),
                               @(U02SectionManager),
                               @(U02SectionInfo),
                               @(U02SectionOther)];
    self.rowModelArray = @[@[
                               @(U02SectionImageRowHead),
                               @(U02SectionImageRowBackground)
                               ],
                           @[
                               @(U02SectionManagerRowAddress),
                               @(U02SectionManagerRowOrder)
                               ],
                           @[
                               @(U02SectionInfoRowName),
                               @(U02SectionInfoRowAge),
                               @(U02SectionInfoRowHeight),
                               @(U02SectionInfoRowWeight),
                               @(U02SectionInfoRowBodyType),
                               @(U02SectionInfoRowDressStyle),
                               @(U02SectionInfoRowExpectation)
                               ],
                           @[
                               @(U02SectionOtherRowPasswd)
                               ]];
    [self configSections];
    [self configCells];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [self.tableView reloadData];
    [MobClick beginLogPageView:PAGE_ID];
    
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}



#pragma mark - Config View

- (void)configSections {
    NSMutableArray* headers = [@[] mutableCopy];
    for (NSNumber* n in self.sectionModelArray) {
        U02Section section = n.integerValue;
        UIView *headerView = [[UIView alloc]initWithFrame:CGRectMake(0, -20, w, 44)];
        UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(8, 0, w, 44)];
        label.font = NEWFONT;
        label.text = u02SectionToTitle(section);
        [headerView addSubview:label];
        headerView.backgroundColor = [UIColor whiteColor];
        [headers addObject:headerView];
    }
    self.headerViews = headers;
}

- (void)configCells {
    NSMutableArray* cellsArray = [@[] mutableCopy];
    int i = 0;
    for (NSArray* rowArray in self.rowModelArray) {
        U02Section section = ((NSNumber*)self.sectionModelArray[i]).integerValue;
        
        NSMutableArray* cells = [@[] mutableCopy];
        for (NSNumber* row in rowArray) {
            QSU02AbstractTableViewCell* cell = [QSU02AbstractTableViewCell generateCellWithSectionType:section rowType:row.integerValue];
            cell.delegate = self;
            [cells addObject:cell];
        }
        [cellsArray addObject:cells];
        i++;
    }
    self.cellArrays = cellsArray;
}


- (void)configNavigation {
    self.navigationItem.title = @"设置";
    [self hideNaviBackBtnTitle];
}

#pragma mark - TableView
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return self.cellArrays.count;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    
    NSArray* array = self.cellArrays[section];
    return array.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 44.f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    if (section == self.cellArrays.count - 1) {
        return self.footerView.bounds.size.height;
    } else {
        return 0;
    }
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    return self.headerViews[section];
}
- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    if (section == self.cellArrays.count - 1) {
        return self.footerView;
    } else {
        return nil;
    }
}

- (QSU02AbstractTableViewCell*)getCellWithIndexPath:(NSIndexPath*)indexPath {
    NSArray* array = self.cellArrays[indexPath.section];
    return array[indexPath.row];
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSU02AbstractTableViewCell* cell = [self getCellWithIndexPath:indexPath];
    NSDictionary *peopleDic = [QSUserManager shareUserManager].userInfo;
    [cell bindWithUser:peopleDic];
    return cell;
}

- (void)hideKeyboardAndDatePicker {
    
}

- (void)showOrderList{
    [self.navigationController pushViewController:[[QSU09OrderListViewController alloc] init] animated:YES];
}
- (void)showAddressList {
    [self.navigationController pushViewController:[[QSU10ReceiverListViewController alloc] init] animated:YES];
}
- (void)showExpectationVc {
    QSU02UserChangeDressEffectViewController *vc = [[QSU02UserChangeDressEffectViewController alloc]init];
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)showChangePasswordVc {
    QSU08PasswordViewController *vc = [[QSU08PasswordViewController alloc]initWithNibName:@"QSU08PasswordViewController" bundle:nil];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    QSU02AbstractTableViewCell* cell = [self getCellWithIndexPath:indexPath];
    [cell cellDidClicked];
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

- (void)updatePeopleEntityViewController:(id)vc byEntity:(id)en pop:(BOOL)f {
#warning Temp
}
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
        if ([value compare:[currentProfile[@"age"] stringValue]] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self byEntity:@{@"age":value} pop:NO];
        }
        
    } else if (textField.tag == 202) {
        if ([value compare:currentProfile[@"length"]] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self byEntity:@{@"height": value} pop:NO];
        }
        
    } else if (textField.tag == 203) {
        if ([value compare:[currentProfile[@"weight"] stringValue]] != NSOrderedSame) {
            [self updatePeopleEntityViewController:self byEntity:@{@"weight": value} pop:NO];
        }
        
    }
}

//
//
//

#pragma mark - Update Image
- (void)prompToChangeImage:(NSInteger)type {
    _uploadImageType = type;
    
    //Other Btns
    NSMutableArray* otherBtns = [@[] mutableCopy];
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypePhotoLibrary]) {
        [otherBtns addObject:@"从相册选择"];
    }
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
        [otherBtns addObject:@"使用相机拍照"];
    }


    if (otherBtns.count) {
        UIActionSheet *sheet =
        [[UIActionSheet alloc] initWithTitle:@"选择图片"
                                    delegate:self
                           cancelButtonTitle:@"取消"
                      destructiveButtonTitle:nil
                           otherButtonTitles:nil];
        for (NSString* title in otherBtns) {
            [sheet addButtonWithTitle:title];
        }
        [self hideKeyboardAndDatePicker];
        [sheet showInView:self.view];
    } else {
        [self showErrorHudWithText:@"没有权限访问相册，请再设定里允许对相册进行访问"];
    }
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    
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
}


#pragma mark - loadData

- (void)didTapTableView:(UITapGestureRecognizer*)ges
{
#warning Hide Keyboard and Picker
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

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
