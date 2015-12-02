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
@interface QSS23MatcherPreviewViewController ()

@property (strong, nonatomic) NSArray* itemArray;
@property (strong, nonatomic) NSArray* itemRects;
@property (strong, nonatomic) UIImage* coverImage;
@property (weak, nonatomic) NSObject<QSMenuProviderDelegate>* menuProvider;


@property (strong, nonatomic) MKNetworkOperation* createMatcherOp;
@property (strong, nonatomic) MKNetworkOperation* updateCoverOp;

@end

@implementation QSS23MatcherPreviewViewController

#pragma mark - Init
- (instancetype)initWithItems:(NSArray*)items rects:(NSArray*)itemRects coverImages:(UIImage*)coverImage menuProvider:(NSObject<QSMenuProviderDelegate>*)menuProvider {
    self = [super initWithNibName:@"QSS23MatcherPreviewViewController" bundle:nil];
    if (self) {
        self.itemArray = items;
        self.itemRects = itemRects;
        self.coverImage = coverImage;
        self.menuProvider = menuProvider;
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
            
            if ([QSPeopleUtil isTalent:[QSUserManager shareUserManager].userInfo]) {
                [QSRootNotificationHelper postShowS01VcWithSegmentIndex:0];
            } else {
                [QSRootNotificationHelper postShowS01VcWithSegmentIndex:2];
            }
        } onError:^(NSError *error) {
            [hud hide:YES];
            self.updateCoverOp = nil;

            [self handleError:error];
        }];
    } onError:^(NSError *error) {
        [hud hide:YES];
        self.createMatcherOp = nil;
        [self handleError:error];
    }];
}


@end
