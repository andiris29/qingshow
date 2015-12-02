//
//  QSU02UserSettingViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU02UserSettingViewController.h"
#import "QSU08PasswordViewController.h"
#import "QSU09TradeListViewController.h"
#import "QSU10ReceiverListViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSUserManager.h"
#import "UIViewController+QSExtension.h"
#import "QSImageEditingViewController.h"
#import "QSPeopleUtil.h"
#import "QSDateUtil.h"
#import "UIImage+fixOrientation.h"
#import "QSRootContainerViewController.h"
#import "QSRootNotificationHelper.h"

#import "QSU02InfoBaseCell.h"
#import "QSU02ImgCell.h"
#import "QSU02ManagerCell.h"
#import "QSU02OtherCell.h"

#import "QSU02UserChangeDressEffectViewController.h"
#import "UIViewController+QSExtension.h"
#import "QSBlock.h"




#define PAGE_ID @"U02 - 个人设置"

#define headImgCellId (@"headImageViewCellId")
#define defaultCellId (@"defaultCellId")
#define userInfoCellID (@"userInfoCellId")

#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)

typedef BOOL (^U02CellBlock)(QSU02AbstractTableViewCell* cell);

@interface QSU02UserSettingViewController ()<UITableViewDataSource,UITableViewDelegate>

@property(nonatomic,strong)NSString *userId;

@property (strong, nonatomic) UIView* footerView;


@property (strong, nonatomic) NSArray* headerViews;
@property (strong, nonatomic) NSMutableArray* cellArrays;       //[[]]
@property (strong, nonatomic) NSArray* sectionModelArray;
@property (strong, nonatomic) NSArray* rowModelArray;


@property (strong, nonatomic) QSSinglePickerProvider* bodyTypePickerProvider;
@property (strong, nonatomic) QSSinglePickerProvider* dressStyleProvider;
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
        
        CGRect frame = CGRectMake(20, _footerView.frame.origin.y+1, [UIScreen mainScreen].bounds.size.width, 1);
        UIView *view = [[UIView alloc]init];
        view.backgroundColor = [UIColor colorWithWhite:0.856 alpha:1.000];
        view.frame = frame;
        [_footerView addSubview:view];
        
        if ([QSPeopleUtil getPeopleRole:[QSUserManager shareUserManager].userInfo] == QSPeopleRoleGuest) {
            UIButton *loginBtn = [UIButton buttonWithType:UIButtonTypeCustom];
            loginBtn.titleLabel.font = NEWFONT;
            [loginBtn setTitle:@"登录" forState:UIControlStateNormal];
            [loginBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
            [loginBtn addTarget:self action:@selector(actionLogin) forControlEvents:UIControlEventTouchUpInside];
            [loginBtn setBackgroundColor:[UIColor colorWithRed:128.f/255.f green:128.f/255.f blue:128.f/255.f alpha:1.f]];
            loginBtn.frame = CGRectMake(10, 25, w-20, 50);
            loginBtn.layer.cornerRadius = loginBtn.bounds.size.height/8;
            loginBtn.layer.masksToBounds = YES;
            [_footerView addSubview:loginBtn];
        } else {
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
                               @(U02SectionOther),
                               @(U02SectionInfo)];
    [self getCellArrayWithPeople];
    [self configSections];
    [self configCells];
    self.tableView.tableFooterView = self.footerView;
    [self pickerProviderInit];

//    [self.tableView addGestureRecognizer: [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapBlank:)]];
}

- (void)u02sectionAndCellRefresh
{
    self.sectionModelArray = @[@(U02SectionImage),
                               @(U02SectionManager),
                               @(U02SectionOther),
                               @(U02SectionInfo)];
    [self getCellArrayWithPeople];
    [self configSections];
    [self configCells];

}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [self refreshData];
    [MobClick beginLogPageView:PAGE_ID];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleKeyboardWillShowNotification:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleKeyboardWillHideNotification:) name:UIKeyboardWillHideNotification object:nil];
    
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {

}

#pragma mark - Config View

- (void)getCellArrayWithPeople
{
    NSDictionary *peopleDic = [QSUserManager shareUserManager].userInfo;
    
    self.rowModelArray = @[@[
                               @(U02SectionImageRowHead),
                               @(U02SectionImageRowBackground)
                               ],
                           @[@(U02SectionManagerRowAddress)],
                           @[@(U02SectionOtherRowPasswd)],
                           @[
                               @(U02SectionInfoRowName),
                               @(U02SectionInfoRowAge),
                               @(U02SectionInfoRowHeight),
                               @(U02SectionInfoRowWeight),
                               @(U02SectionInfoRowBust),
                               @(U02SectionInfoRowShouler),
                               @(U02SectionInfoRowWaist),
                               @(U02SectionInfoRowHips),
                               @(U02SectionInfoRowBodyType),
                               @(U02SectionInfoRowDressStyle),
                               @(U02SectionInfoRowExpectation)
                               ],
                           ];
    

}
- (void)configSections {
    NSMutableArray* headers = [@[] mutableCopy];
    for (NSNumber* n in self.sectionModelArray) {
        U02Section section = n.integerValue;
        UIView *headerView = [[UIView alloc]initWithFrame:CGRectMake(0, -20, w, 55)];
        UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(8, 0, w, 55)];
        label.font = NEWFONT;
        label.text = u02SectionToTitle(section);
        [headerView addSubview:label];
        
        CGRect frame = CGRectMake(0, 0, w, 1);
        UIImageView *view = [[UIImageView alloc]init];
        view.backgroundColor = [UIColor colorWithWhite:0.886 alpha:1.000];
        view.frame = frame;
        [headerView addSubview:view];
        CGRect frame01 = view.frame;
        frame01.origin.y = 55;
        UIImageView *view01 = [[UIImageView alloc]initWithFrame:frame01];
        view01.backgroundColor = view.backgroundColor;
        [headerView addSubview:view01];
        
        
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
    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:89.f/255.f green:86.f/255.f blue:86.f/255.f alpha:1.f];
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];
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
    if (section == 2) {
        return 0.f;
    }
    return 55.f;
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    return self.headerViews[section];
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

#pragma mark - Action
- (void)showAddressList {
    [self hideKeyboardAndPicker];
    [self.navigationController pushViewController:[[QSU10ReceiverListViewController alloc] init] animated:YES];
}
- (void)showExpectationVc {
    [self hideKeyboardAndPicker];
    QSU02UserChangeDressEffectViewController *vc = [[QSU02UserChangeDressEffectViewController alloc]init];
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)showChangePasswordVc {
    [self hideKeyboardAndPicker];
    QSU08PasswordViewController *vc = [[QSU08PasswordViewController alloc]initWithNibName:@"QSU08PasswordViewController" bundle:nil];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {

    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    QSU02AbstractTableViewCell* cell = [self getCellWithIndexPath:indexPath];
    if (![cell cellDidClicked]) {
        [self hideKeyboardAndPicker];
    }
}

- (void)actionLogout {
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:nil message:@"确认退出？" delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确定", nil];
    alert.delegate = self;
    [alert show];
    
}
- (void)actionLogin {
    [QSRootNotificationHelper postShowLoginPrompNoti];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1) {
        [QSUserManager shareUserManager].userInfo = nil;
        [QSUserManager shareUserManager].fIsLogined = NO;
        CATransition* tran = [[CATransition alloc] init];
        tran.type = kCATransitionFade;
        tran.duration = 0.5f;
        [self.navigationController.parentViewController.view.layer addAnimation:tran forKey:@"tran"];
        [QSRootNotificationHelper postShowS01VcWithSegmentIndex:0];
        
        [SHARE_NW_ENGINE logoutOnSucceed:nil onError:nil];
    }
}
#pragma mark - Text Field
- (void)updateUserInfoKey:(NSString*)key value:(NSString*)value{
    if (!key || !value || !key.length || !value.length) {
        return;
    }
    NSDictionary* peopleDict = [QSUserManager shareUserManager].userInfo;
    if ([value isEqualToString:peopleDict[key]]) {
        return;
    }
    [SHARE_NW_ENGINE updatePeople:@{key : value} onSuccess:nil onError:nil];
}


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
        [self hideKeyboardAndPicker];
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
        if (buttonIndex == 1) {
            imagePickerController.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        } else if (buttonIndex == 2) {
            imagePickerController.sourceType = UIImagePickerControllerSourceTypeCamera;
        }
        imagePickerController.allowsEditing = NO;
        [self presentViewController:imagePickerController animated:YES completion:^{}];
    }
}

#pragma mark - Image Picker
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
    if (_uploadImageType == U02SectionImageRowHead) {
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
        [self showSuccessHudWithText:@"上传成功"];
        [self.tableView reloadData];
    };
    
    // Error Handle
    ErrorBlock error = ^(NSError *error) {
        [hud hide:YES];
        [self handleError:error];
    };
    
    // Convert UIImage to NSData
    NSData *imageData = UIImageJPEGRepresentation(image, 0.5);
    // write NSData to sandbox
    if (_uploadImageType == U02SectionImageRowHead) {
        [SHARE_NW_ENGINE updatePortrait:imageData onSuccess:success onError:error];
    } else {
        [SHARE_NW_ENGINE updateBackground:imageData onSuccess:success onError:error];
    }
}
- (void)cancelImageEditing:(QSImageEditingViewController *)vc
{
    [self dismissViewControllerAnimated:YES completion:nil];
}


#pragma mark - Helper
- (void)visitAllCell:(U02CellBlock)cellBlock {
    for (NSArray* array in self.cellArrays) {
        for (QSU02AbstractTableViewCell* cell in array) {
            if (!cellBlock(cell)) {
                return;
            }
        }
    }
}

- (void)refreshData {
    [SHARE_NW_ENGINE getLoginUserOnSucced:^(NSDictionary *data, NSDictionary *metadata) {
        [self u02sectionAndCellRefresh];
        _footerView = nil;
        self.tableView.tableFooterView = [self footerView];
         [self.tableView reloadData];
    } onError:nil];
}

#pragma mark - Keyboard
- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
//    [self hideKeyboardAndPicker];
}

- (void)hideKeyboard {
    [self visitAllCell:^BOOL(QSU02AbstractTableViewCell *cell) {
        [cell resignKeyboardAndPicker];
        return YES;
    }];
}
- (void)hideKeyboardAndPicker {
    [self hideKeyboard];
    [self hidePicker];
}

#pragma mark - Picker
- (void)pickerProviderInit {
    self.bodyTypePickerProvider = [[QSSinglePickerProvider alloc] initWithDataArray:@[@"A型", @"H型", @"V型", @"X型"]];
    self.bodyTypePickerProvider.delegate = self;
    self.dressStyleProvider = [[QSSinglePickerProvider alloc] initWithDataArray:@[@"日韩系", @"欧美系"]];
    self.dressStyleProvider.delegate = self;
}

- (void)showPickerWithType:(NSInteger)type {
    NSDictionary* dict = [QSUserManager shareUserManager].userInfo;
    if (type == U02SectionInfoRowDressStyle) {
        [self.dressStyleProvider bindPicker:self.picker];
        [self.dressStyleProvider selectData:[QSPeopleUtil getDressStyleDesc:dict]];
        
        [self showPicker];
    } else if (type == U02SectionInfoRowBodyType) {
        [self.bodyTypePickerProvider bindPicker:self.picker];
        [self.bodyTypePickerProvider selectData:[QSPeopleUtil getBodyTypeDesc:dict]];
        
        [self showPicker];
    }
}
- (void)showPicker {

    if (!self.picker.hidden){
        return;
    }
    self.picker.hidden = NO;
    [self hideKeyboard];
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromTop;
    tran.duration = 0.2f;
    [self.picker.layer addAnimation:tran forKey:@"ShowAnimation"];
    UIEdgeInsets inset = self.tableView.contentInset;
    float delta = 250.f - inset.bottom;
    
    inset.bottom = 250.f;
    self.tableView.contentInset = inset;
    CGPoint p = self.tableView.contentOffset;
    if (delta > 0) {
        p.y += self.picker.bounds.size.height;
    }

    [self.tableView setContentOffset:p animated:YES];
}
- (void)hidePicker {
    if (self.picker.hidden) {
        return;
    }
    [self hidePickerWithoutInset];
    UIEdgeInsets inset = self.tableView.contentInset;
    inset.bottom = 0.f;
    self.tableView.contentInset = inset;
}
- (void)hidePickerWithoutInset {
    if (self.picker.hidden) {
        return;
    }
    self.picker.hidden = YES;
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromBottom;
    tran.duration = 0.2f;
    [self.picker.layer addAnimation:tran forKey:@"ShowAnimation"];
}

- (void)provider:(QSSinglePickerProvider*)provider didSelectRow:(int)row value:(NSString*)value {
    NSString* key = nil;
    if (provider == self.bodyTypePickerProvider) {
        key = @"bodyType";
    } else if (provider == self.dressStyleProvider) {
        key = @"dressStyle";
    }
    if (!key) {
        return;
    }
    
    [SHARE_NW_ENGINE updatePeople:@{key : @(row)} onSuccess:^(NSDictionary *data, NSDictionary *metadata) {
        [self refreshData];
    } onError:nil];
    
}
#pragma mark - Handle Keyboard
- (void)handleKeyboardWillShowNotification:(NSNotification*)noti {
    [self hidePickerWithoutInset];
    UIEdgeInsets inset = self.tableView.contentInset;
    inset.bottom = 250.f;
    self.tableView.contentInset = inset;
}
- (void)handleKeyboardWillHideNotification:(NSNotification*)onti {
    if (self.picker.hidden) {
        UIEdgeInsets inset = self.tableView.contentInset;
        inset.bottom = 0.f;
        self.tableView.contentInset = inset;
    }

}
//- (void)didTapBlank:(UITapGestureRecognizer*)ges {
//    CGPoint p = [ges locationInView:ges.view];
//    p.y += self.tableView.contentOffset.y;
//    NSIndexPath* path = [self.tableView indexPathForRowAtPoint:p];
//    [self hideKeyboardAndPicker];
//}

@end
