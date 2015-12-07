//
//  QSCommentListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS04CommentListViewController.h"
#import "QSCommentTableViewCell.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSShowUtil.h"
#import "QSCommentUtil.h"
#import "QSEntityUtil.h"
#import "QSPeopleUtil.h"
#import "QSUserManager.h"
#import "UIViewController+QSExtension.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSImageNameUtil.h"
#define PAGE_ID @"S04 - 评论"


@interface QSS04CommentListViewController ()

@property (strong, nonatomic) IBOutlet UITableView* tableView;

@property (strong, nonatomic) NSString* showId;
@property (strong, nonatomic) NSDictionary* showDict;
@property (strong, nonatomic) NSDictionary* previewDict;
@property (strong, nonatomic) QSCommentListTableViewProvider* delegateObj;
@property (assign, nonatomic) int clickIndex;

@end

@implementation QSS04CommentListViewController

#pragma mark - Init

- (instancetype)initWithShowId:(NSString*)showId {
    self = [self initWithNibName:@"QSS04CommentListViewController" bundle:nil];
    if (self) {
        self.showId = showId;
        __weak QSS04CommentListViewController* weakSelf = self;
        
        self.delegateObj = [[QSCommentListTableViewProvider alloc] init];
        self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
            return [SHARE_NW_ENGINE getCommentsOfShowId:weakSelf.showId page:page onSucceed:succeedBlock onError:errorBlock];
        };
        [self.delegateObj fetchDataOfPage:1];
    }
    return self;
}
- (instancetype)initWithShow:(NSDictionary*)showDict
{
    self = [self initWithShowId:[QSEntityUtil getIdOrEmptyStr:showDict]];
    if (self) {
        self.showDict = showDict;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self.delegateObj bindWithTableView:self.tableView];
    self.delegateObj.delegate = self;
    self.title = @"评论";
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    self.headIcon.layer.cornerRadius = self.headIcon.frame.size.width / 2;
    self.headIcon.layer.masksToBounds = YES;
    self.textField.layer.cornerRadius = 4;
    self.textField.layer.masksToBounds = YES;
    self.sendBtn.layer.cornerRadius = 4;
    self.sendBtn.layer.masksToBounds = YES;
    
    self.clickIndex = -1;
    if ([QSPeopleUtil getHeadIconUrl:[QSUserManager shareUserManager].userInfo type:QSImageNameType100]) {
        [self.headIcon setImageFromURL:[QSPeopleUtil getHeadIconUrl:[QSUserManager shareUserManager].userInfo type:QSImageNameType100]];
    }
    else
    {
        NSURL *url = [QSImageNameUtil appendingDefaultImageUrl];
        [self.headIcon setImageFromURL:url];
    }
    
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    self.navigationItem.leftBarButtonItem = backItem;
    
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillShow:)
                                                 name:UIKeyboardWillShowNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:nil];
    [MobClick beginLogPageView:PAGE_ID];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}
- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - QSCommentListTableViewProviderDelegate
- (void)didClickComment:(NSDictionary*)commemntDict atIndex:(int)index
{
    NSString* destructiveTitle = nil;
    if ([QSPeopleUtil isPeople:[QSUserManager shareUserManager].userInfo equalToPeople:[QSCommentUtil getPeople:commemntDict]])
    {
        destructiveTitle = @"删除";
    }
    else
    {
        return;
    }
    [self.textField resignFirstResponder];
    self.clickIndex = index;
    if ([QSUserManager shareUserManager].userInfo == nil) {
         UIActionSheet* sheet = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:@"请先登录" destructiveButtonTitle:destructiveTitle otherButtonTitles:/*@"回复", @"查看个人主页",*/ nil];
        [sheet showInView:self.view];
    }
    else
    {
    UIActionSheet* sheet = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:@"取消" destructiveButtonTitle:destructiveTitle otherButtonTitles:/*@"回复", @"查看个人主页",*/ nil];
    [sheet showInView:self.view];
    }
}
- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    NSDictionary* comment = nil;
    if (self.clickIndex >= 0 && self.clickIndex < self.delegateObj.resultArray.count) {
        comment = self.delegateObj.resultArray[self.clickIndex];
    }

    //0 查看个人主页
    
    if (buttonIndex == actionSheet.destructiveButtonIndex) {
        //删除评论
        int index = self.clickIndex;
        self.clickIndex = -1;
        if (self.showDict && comment) {
            [SHARE_NW_ENGINE deleteComment:comment ofShow:self.showDict onSucceed:^{
                [self.delegateObj.resultArray removeObjectAtIndex:index];
                [self.delegateObj.view deleteRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:index inSection:0]] withRowAnimation:UITableViewRowAnimationAutomatic];
            } onError:^(NSError *error) {
                [self handleError:error];
            }];
        } else {
            [SHARE_NW_ENGINE deleteCommentId:[QSEntityUtil getIdOrEmptyStr:comment] onSucceed:^{
                [self.delegateObj.resultArray removeObjectAtIndex:index];
                [self.delegateObj.view deleteRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:index inSection:0]] withRowAnimation:UITableViewRowAnimationAutomatic];
            } onError:^(NSError *error) {
                [self handleError:error];
            }];
        }

    } else if (buttonIndex == actionSheet.cancelButtonIndex)
    {
        self.clickIndex = -1;
    }
//    else if (buttonIndex == 0) {
        //回复
//        self.textField.placeholder = [NSString stringWithFormat:@"回复 %@ :", [QSPeopleUtil getName:people]];
//        [self.textField becomeFirstResponder];
//    }
    else
    {
//        if (!people) {
//            [self showErrorHudWithText:@"系统错误"];
//        } else {
//            [self showPeopleDetailViewControl:people];
//        }
    }
}
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    [self.textField resignFirstResponder];
//    self.textField.placeholder = @"回复评论";
//    self.clickIndex = -1;
}
#pragma mark - Text Field
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if (textField.text.length == range.length && string.length == 0) {
        self.sendBtn.backgroundColor = [UIColor colorWithRed:162.f/255.f green:162.f/255.f blue:162.f/255.f alpha:0.9];
    } else {
        self.sendBtn.backgroundColor = [UIColor colorWithRed:40.f/255.f green:45.f/255.f blue:90.f/255.f alpha:0.9];
    }
    return YES;
}
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self sendBtnPressed:nil];
    return YES;
}

- (IBAction)sendBtnPressed:(id)sender {
    if (!self.textField.text.length) {
        [self showErrorHudWithText:@"请填写内容"];
    } else {
        [self.textField resignFirstResponder];
        
        NSDictionary* people = nil;
        if (self.clickIndex >= 0 && self.clickIndex < self.delegateObj.resultArray.count) {
            NSDictionary* comment = self.delegateObj.resultArray[self.clickIndex];
            people = [QSCommentUtil getPeople:comment];
        }
        __weak QSS04CommentListViewController* weakSelf = self;
        if (self.showDict) {
            [SHARE_NW_ENGINE addComment:self.textField.text onShow:self.showDict reply:people onSucceed:^{
                [weakSelf.delegateObj reloadData];
                [self showSuccessHudWithText:@"发送成功"];
                self.textField.text = @"";
            } onError:^(NSError *error) {
                [self handleError:error];
            }];
        } else {
            [SHARE_NW_ENGINE addComment:self.textField.text onShowId:self.showId reply:people onSucceed:^{
                [weakSelf.delegateObj reloadData];
                [self showSuccessHudWithText:@"发送成功"];
                self.textField.text = @"";
            } onError:^(NSError *error) {
                [self handleError:error];
            }];
        }
        self.clickIndex = -1;
    }
}

#pragma mark - Keyboard
- (void)keyboardWillShow:(NSNotification *)notif {
    CGRect keyBoardRect = [[notif.userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    
    [UIView animateWithDuration:0.5 animations:^{

        self.commentBottomConstrain.constant = keyBoardRect.size.height;
        [self.view layoutIfNeeded];
    } completion:^(BOOL finish){
    }];
    
}

- (void)keyboardWillHide:(NSNotification *)notif {

    self.textField.placeholder = @"  输入新评论";
    [UIView animateWithDuration:0.5 animations:^{
        self.commentBottomConstrain.constant = 0;
        [self.view layoutIfNeeded];
    } completion:^(BOOL finish){
        
    }];
    
}
@end
