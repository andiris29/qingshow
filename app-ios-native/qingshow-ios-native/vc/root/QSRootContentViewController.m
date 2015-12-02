//
//  QSRootContentViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/20/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSRootContentViewController.h"
#import "QSRootContainerViewController.h"
#import "QSUserManager.h"
#import "UIViewController+ShowHud.h"
#import "QSUnreadManager.h"
#import "QSRootNotificationHelper.h"

@interface QSRootContentViewController ()
@property (strong, nonatomic) UIBarButtonItem* menuBtn;
@property (strong, nonatomic) UIBarButtonItem* menuBtnNew;
@end

@implementation QSRootContentViewController

@synthesize menuProvider = _menuProvider;

#pragma mark - Getter And Setter
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

#pragma mark - Life Cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    if ([self.navigationController respondsToSelector:@selector(interactivePopGestureRecognizer)]) {
        self.navigationController.interactivePopGestureRecognizer.enabled = NO;
    }
    [self configNavBar];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleUnreadChange:) name:kQSUnreadChangeNotificationName object:nil];

}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self updateMenuDot];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - Configure View
- (void)configNavBar
{
    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:89.f/255.f green:86.f/255.f blue:86.f/255.f alpha:1.f];
    UINavigationBar* navBar = self.navigationController.navigationBar;
    UITapGestureRecognizer* tapGes = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapRootTitle)];
    self.showVersionTapGesture = tapGes;
    tapGes.numberOfTapsRequired = 5;
    [navBar addGestureRecognizer:tapGes];
    
    self.navigationItem.leftBarButtonItem = self.menuBtn;
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}

- (void)menuButtonPressed
{
    [QSRootNotificationHelper postShowRootMenuNoti];
}
- (void)didTapRootTitle
{
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    NSString* v = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleVersion"];
    
    [self showTextHud:[NSString stringWithFormat:@"version: %@ - %@", version, v]];
}

- (void)updateMenuDot {
    if ([[QSUnreadManager getInstance] shouldShowDotAtMenu]) {
        self.navigationItem.leftBarButtonItem = self.menuBtnNew;
    } else {
        self.navigationItem.leftBarButtonItem = self.menuBtn;
    }
}
- (void)handleUnreadChange:(NSNotification*)noti {
    [self updateMenuDot];
}

@end
