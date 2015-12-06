//
//  QSS23MatcherPreviewViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/7/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS23MatcherPreviewViewController.h"
#import "QSNetworkKit.h"
#import "QSS03ShowDetailViewController.h"
#import "UIViewController+ShowHud.h"
#import "QSUserManager.h"
#import "UIViewController+QSExtension.h"
#import "QSPeopleUtil.h"
#import "QSRootNotificationHelper.h"
#import "NSDictionary+QSExtension.h"
#import "QSBlockAlertView.h"
#import "QSError.h"

@interface QSS23MatcherPreviewViewController ()

@property (strong, nonatomic) NSArray* itemArray;
@property (strong, nonatomic) NSArray* itemRects;
@property (strong, nonatomic) UIImage* coverImage;

@property (strong, nonatomic) MKNetworkOperation* createMatcherOp;
@property (strong, nonatomic) MKNetworkOperation* updateCoverOp;
@property (strong, nonatomic) QSBlockAlertView* alertView;
@end

@implementation QSS23MatcherPreviewViewController

#pragma mark - Init
- (instancetype)initWithItems:(NSArray*)items rects:(NSArray*)itemRects coverImages:(UIImage*)coverImage{
    self = [super initWithNibName:@"QSS23MatcherPreviewViewController" bundle:nil];
    if (self) {
        self.itemArray = items;
        self.itemRects = itemRects;
        self.coverImage = coverImage;
    }
    return self;
}
#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.imgView.image = self.coverImage;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)backBtnPressed:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}

- (IBAction)submitBtnPressed:(id)sender {
    if (self.createMatcherOp || self.updateCoverOp) {
        //防止重复发请求
        return;
    }
    
    MBProgressHUD* hud = [self showNetworkWaitingHud];
    
    self.createMatcherOp =
    [SHARE_NW_ENGINE matcherSave:self.itemArray itemRects:self.itemRects onSucceed:^() {
        self.createMatcherOp = nil;
        self.updateCoverOp =
        [SHARE_NW_ENGINE matcherUpdateCover:self.coverImage  onSucceed:^(NSDictionary *d) {
            self.updateCoverOp = nil;
            
            if ([self.delegate respondsToSelector:@selector(vc:didCreateNewMatcher:)]) {
                [self.delegate vc:self didCreateNewMatcher:d];
            }
            
            if ([QSUserManager shareUserManager].fShouldShowLoginGuideAfterCreateMatcher) {
                [QSRootNotificationHelper postScheduleToShowLoginGuideNoti];
                [QSUserManager shareUserManager].fShouldShowLoginGuideAfterCreateMatcher = NO;
            }
            [hud hide:YES];
            
            [QSRootNotificationHelper postShowLatestS24VcNoti];
        } onError:^(NSError *error) {
            [hud hide:YES];
            self.updateCoverOp = nil;

            [self handleError:error];
        }];
    } onError:^(NSError *error) {
        [hud hide:YES];
        self.createMatcherOp = nil;
        
        if ([error isKindOfClass:[QSError class]] && error.code == 1043) {
            if (self.alertView) {
                return;
            }
            
            NSDictionary* metadata = error.userInfo;
            NSNumber* limitCount = [metadata numberValueForKeyPath:@"limitMessage"];
            if (!limitCount) {
                limitCount = @2;
            }
            
            NSString* msg = [NSString stringWithFormat:@"亲~ 每小时搭%@套就可以咯，要保护眼睛哦~\n下个小时再来搭吧", limitCount];
            QSBlockAlertView* alertView = [[QSBlockAlertView alloc] initWithTitle:@"" message:msg delegate:self cancelButtonTitle:@"确定" otherButtonTitles:nil];
            self.alertView = alertView;
            alertView.succeedHandler = ^(){
                [QSRootNotificationHelper postShowRootContentTypeNoti:QSRootMenuItemMeida];
                self.alertView = nil;
            };
            alertView.cancelHandler = ^(){
                [QSRootNotificationHelper postShowRootContentTypeNoti:QSRootMenuItemMeida];
                self.alertView = nil;
            };
            [alertView show];
        } else {
            [self handleError:error];
        }
    }];
}


#pragma mark - AlertView Delegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    if ([alertView isKindOfClass:[QSBlockAlertView class]]) {
        QSBlockAlertView* blockAlertView = (QSBlockAlertView*)alertView;
        if (buttonIndex == blockAlertView.cancelButtonIndex) {
            if (blockAlertView.cancelHandler) {
                blockAlertView.cancelHandler();
            }
        } else {
            if (blockAlertView.succeedHandler) {
                blockAlertView.succeedHandler();
            }
        }
    }
}
@end
