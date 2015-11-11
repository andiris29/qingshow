//
//  QSS20MatcherViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/21/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS20MatcherViewController.h"
#import "QSS21CategorySelectorVC.h"

#import "QSAbstractRootViewController.h"
#import "QSNetworkKit.h"
#import "QSEntityUtil.h"
#import "QSCategoryUtil.h"
#import "UIViewController+QSExtension.h"
#import "UIViewController+ShowHud.h"
#import "UIView+ScreenShot.h"
#import "QSS03ShowDetailViewController.h"

#import "QSMatcherItemPageSelectionImageView.h"
#import "QSMatcherItemScrollSelectionView.h"

#import "NSArray+QSExtension.h"
#import "QSUnreadManager.h"

#import "QSPeopleUtil.h"
#import "QSUserManager.h"
@interface QSS20MatcherViewController ()

@property (strong, nonatomic) UIView<QSMatcherItemSelectionViewProtocol>* itemSelectionView;
@property (strong, nonatomic) QSMatcherCanvasView* canvasView;

@property (strong, nonatomic) NSMutableDictionary* cateIdToProvider;

@property (strong, nonatomic) NSString* selectedCateId;
@property (strong, nonatomic) NSArray* allCategories;
@property (strong, nonatomic) NSDictionary* matcherConfig;

@property (assign, nonatomic) BOOL fShouldReload;
@property (assign, nonatomic) BOOL fRemoveMenuBtn;
@end

@implementation QSS20MatcherViewController
@synthesize menuProvider = _menuProvider;
#pragma mark - Init
- (instancetype)init {
    self = [super initWithNibName:@"QSS20MatcherViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.submitButton.layer.cornerRadius = 4.f;
    self.submitButton.layer.masksToBounds = YES;
    self.categorySelectionButton.layer.cornerRadius = 4.f;
    self.categorySelectionButton.layer.masksToBounds = YES;
    // Do any additional setup after loading the view from its nib.
//    self.itemSelectionView = [QSMatcherItemPageSelectionView generateView];
    self.itemSelectionView = [QSMatcherItemScrollSelectionView generateView];
    self.itemSelectionView.frame = self.itemSelectionContainer.bounds;
    [self.itemSelectionContainer addSubview:self.itemSelectionView];
    [self.itemSelectionView reloadData];
    
    
    self.canvasView = [QSMatcherCanvasView generateView];
    self.canvasView.delegate = self;
    self.canvasView.frame = self.canvasContainer.bounds;
    [self.canvasContainer addSubview:self.canvasView];
    
    self.fShouldReload = YES;
    
    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:89.f/255.f green:86.f/255.f blue:86.f/255.f alpha:1.f];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleUnreadChange:) name:kQSUnreadChangeNotificationName object:nil];
    self.menuBtn.hidden = self.fRemoveMenuBtn;
    
    //masking tap
    QSPeopleRole r = [QSPeopleUtil getPeopleRole:[QSUserManager shareUserManager].userInfo];
    if (r == QSPeopleRoleGuest && _isGuestFirstLoad == YES) {
        self.maskingView.userInteractionEnabled = YES;
        UITapGestureRecognizer *maskingTap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(hideMaskingView:)];
        [self.maskingView addGestureRecognizer:maskingTap];
    }else{
        self.maskingView.hidden = YES;
    }
    
}

- (void)hideMaskingView:(UITapGestureRecognizer *)ges
{
    [ges.view removeFromSuperview];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self updateMenuDot];
    self.navigationController.navigationBarHidden = YES;
    if (self.fShouldReload) {
        self.fShouldReload = NO;
        [self updateCategory:@[]];
        [SHARE_NW_ENGINE matcherQueryCategoriesOnSucceed:^(NSArray *array, NSDictionary* context, NSDictionary *metadata) {
            self.allCategories = array;
            NSDictionary* matcherConfig = [QSCategoryUtil getMatcherConfig:context];
            self.matcherConfig = matcherConfig;
            
            NSMutableArray* selectedCategories = [@[] mutableCopy];
            
            
            for (NSDictionary* category in array) {
                if ([QSCategoryUtil getDefaultOnCanvas:category withMatcherConfig:matcherConfig]) {
                    [selectedCategories addObject:category];
                    
                }
                NSArray* childrens = [QSCategoryUtil getChildren:category];
                for (NSDictionary* c in childrens) {
                    if ([QSCategoryUtil getDefaultOnCanvas:c withMatcherConfig:matcherConfig]) {
                        [selectedCategories addObject:c];
                    }
                }
            }
            
            [self updateCategory:selectedCategories];
        } onError:nil];
        self.cateIdToProvider = [@{} mutableCopy];
    }
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - IBAction
- (IBAction)categorySelectedBtnPressed:(id)sender {
    NSArray* categoryArray = [[self.cateIdToProvider allValues] mapUsingBlock:^id(QSMatcherItemsProvider* p) {
        return p.categoryDict;
    }];
    QSS21CategorySelectorVC* vc = [[QSS21CategorySelectorVC alloc] initWithCategories:self.allCategories selectedCategories:categoryArray];
    vc.delegate = self;
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)menuBtnPressed:(id)sender {
    [self.menuProvider didClickMenuBtn];
}

- (IBAction)previewButtonPressed:(id)sender {
    //防止因为图标未下载完提交白图
    if (![self.canvasView checkLoadAllImages]) {
        [self showErrorHudWithText:@"请等待图片下载完成"];
    }
    
    //图片需要显示70%以上才能提交
    if (![self.canvasView checkRate:0.7f]) {
        [self showErrorHudWithText:@"图片被遮挡太多，请调整"];
        return;
    }
    
    NSArray* items = [[self.cateIdToProvider allValues] mapUsingBlock:^id(QSMatcherItemsProvider* p) {
        if (p.selectIndex < p.resultArray.count) {
            return p.resultArray[p.selectIndex];
        } else {
            return [NSNull null];
        }
        
    }];
    items = [items filteredArrayUsingBlock:^BOOL(NSDictionary* itemDict) {
        return ![QSEntityUtil checkIsNil:itemDict];
    }];
    
    if (items.count < 4) {
        [self showTextHud:@"单品太少了会不好看哦，\n点击选择分类按钮，\n上万件单品可以选哦！" afterCustomDelay:1.f];
        return;
    }
    
    
    UIImage* snapshot = [self.canvasView submitView];
    
    QSS23MatcherPreviewViewController* vc = [[QSS23MatcherPreviewViewController alloc] initWithItems:items coverImages:snapshot menuProvider:self.menuProvider];
    vc.delegate = self;
    [self.navigationController pushViewController:vc animated:YES];
}




- (void)setSelectedCateId:(NSString *)selectedCateId {
    if ([_selectedCateId isEqualToString:selectedCateId]) {
        return;
    }
    _selectedCateId = selectedCateId;
    QSMatcherItemsProvider* provider = self.cateIdToProvider[selectedCateId];
    self.itemSelectionView.datasource = provider;
    self.itemSelectionView.delegate = provider;
    self.itemSelectionView.selectIndex = provider.selectIndex;
    [self.itemSelectionView reloadData];
    if ([self.itemSelectionView respondsToSelector:@selector(showSelect:)]) {
        [self.itemSelectionView showSelect:YES];
    } else {
        [self.itemSelectionView offsetToZero:YES];
    }

}

#pragma mark canvas
- (void)canvasView:(QSMatcherCanvasView*)view didTapCategory:(NSDictionary*)categoryDict {
    self.selectedCateId = [QSEntityUtil getIdOrEmptyStr:categoryDict];
    
}
- (void)canvasView:(QSMatcherCanvasView *)view didRemoveCategory:(NSDictionary *)categoryDict {
    NSString* categoryID = [QSEntityUtil getIdOrEmptyStr:categoryDict];
    [self.cateIdToProvider removeObjectForKey:categoryID];
    if ([self.selectedCateId isEqualToString:categoryID]) {
        self.selectedCateId = nil;
    }
}

#pragma mark - Category Selection
- (void)didSelectCategories:(NSArray*)categoryArray {
    [self updateCategory:categoryArray];
}

- (void)updateCategory:(NSArray*)array{
    //Update View
    [self.canvasView bindWithCategory:array matcherConfig:self.matcherConfig];
    
    NSArray* newCategoryIds = [array mapUsingBlock:^id(NSDictionary* dict) {
        return [QSEntityUtil getIdOrEmptyStr:dict];
    }];
    NSArray* oldCategoryIds = [self.cateIdToProvider allKeys];
    //Remove Old Provider
    [oldCategoryIds enumerateObjectsUsingBlock:^(NSString* oldKey, NSUInteger idx, BOOL *stop) {
        if ([newCategoryIds indexOfObject:oldKey] == NSNotFound) {
            [self.cateIdToProvider removeObjectForKey:oldKey];
        }
    }];
    
    //Add New Provider
    for (NSDictionary* categoryDict in array) {
        __block NSString* cateId = [QSEntityUtil getIdOrEmptyStr:categoryDict];
        if ([oldCategoryIds indexOfObject:cateId] == NSNotFound) {
            QSMatcherItemsProvider* provider = [[QSMatcherItemsProvider alloc] initWithCategory:categoryDict];
            provider.delegate = self;
            self.cateIdToProvider[cateId] = provider;
            [provider reloadData];
        }
    }
    
    //Set Default Selected Provider
    if ([self.cateIdToProvider allKeys].count) {
        self.selectedCateId = [self.cateIdToProvider allKeys][0];
    }
}

#pragma mark - QSMatcherItemsProviderDelegate
- (void)matcherItemProvider:(QSMatcherItemsProvider*)provider ofCategory:(NSDictionary*)categoryDict didSelectItem:(NSDictionary*)itemDict{
    [self.canvasView setItem:itemDict forCategory:categoryDict isFirst:provider.fIsFirst];
    provider.fIsFirst = NO;
}
- (void)matcherItemProvider:(QSMatcherItemsProvider*)provider didFinishNetworkLoading:(NSDictionary*)categoryDict {
    if ([[QSEntityUtil getIdOrEmptyStr:categoryDict] isEqualToString:self.selectedCateId]) {
        [self.itemSelectionView reloadData];
    }
}

#pragma mark - QSS23MatcherPreviewViewControllerDelegate
- (void)vc:(UIViewController *)vc didCreateNewMatcher:(NSDictionary *)matcherDict {
    self.fShouldReload = YES;
}


- (void)updateMenuDot {
    if ([[QSUnreadManager getInstance] shouldShowDotAtMenu]) {
        [self.menuBtn setImage:[UIImage imageNamed:@"nav_btn_menu_new"] forState:UIControlStateNormal];
    } else {
        [self.menuBtn setImage:[UIImage imageNamed:@"nav_btn_menu"] forState:UIControlStateNormal];
    }
}
- (void)handleUnreadChange:(NSNotification*)noti {
    [self updateMenuDot];
}
- (void)hideMenuBtn {
    self.menuBtn.hidden = YES;
    self.fRemoveMenuBtn = YES;
}
@end
