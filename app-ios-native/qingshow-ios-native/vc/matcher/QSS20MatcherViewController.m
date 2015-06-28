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
#import "QSCommonUtil.h"
#import "UIViewController+QSExtension.h"
#import "UIViewController+ShowHud.h"
#import "UIView+ScreenShot.h"

#import "NSArray+QSExtension.h"

@interface QSS20MatcherViewController ()

@property (strong, nonatomic) QSMatcherItemSelectionView* itemSelectionView;
@property (strong, nonatomic) QSMatcherCanvasView* canvasView;

@property (strong, nonatomic) NSMutableDictionary* cateIdToProvider;

@property (strong, nonatomic) NSString* selectedCateId;
@property (strong, nonatomic) NSArray* allCategories;
@end

@implementation QSS20MatcherViewController

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
    self.submitButton.layer.cornerRadius = 2.f;
    self.submitButton.layer.masksToBounds = YES;
    self.categorySelectionButton.layer.cornerRadius = 2.f;
    self.categorySelectionButton.layer.masksToBounds = YES;
    // Do any additional setup after loading the view from its nib.
    self.itemSelectionView = [QSMatcherItemSelectionView generateView];
    self.itemSelectionView.frame = self.itemSelectionContainer.bounds;
    [self.itemSelectionContainer addSubview:self.itemSelectionView];
    [self.itemSelectionView reloadData];
    
    
    self.canvasView = [QSMatcherCanvasView generateView];
    self.canvasView.delegate = self;
    self.canvasView.frame = self.canvasContainer.bounds;
    [self.canvasContainer addSubview:self.canvasView];
    
    
    [SHARE_NW_ENGINE matcherQueryCategoriesOnSucceed:^(NSArray *array, NSDictionary *metadata) {
        self.allCategories = array;
        [self updateCategory:array];
    } onError:nil];
    self.cateIdToProvider = [@{} mutableCopy];
}



- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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

- (IBAction)submitButtonPressed:(id)sender {
    NSArray* items = [[self.cateIdToProvider allValues] mapUsingBlock:^id(QSMatcherItemsProvider* p) {
        if (p.selectIndex < p.resultArray.count) {
            return p.resultArray[p.selectIndex];
        } else {
            return [NSNull null];
        }
        
    }];
    items = [items filteredArrayUsingBlock:^BOOL(NSDictionary* itemDict) {
        return ![QSCommonUtil checkIsNil:itemDict];
    }];

    UIImage* snapshot = [self.canvasView submitView];
    [SHARE_NW_ENGINE matcherSave:items onSucceed:^(NSDictionary *dict) {
        [SHARE_NW_ENGINE matcher:dict updateCover:snapshot  onSucceed:^(NSDictionary *d) {
            [self showShowDetailViewController:d];
        } onError:^(NSError *error) {
            [self showErrorHudWithError:error];
        }];
    } onError:^(NSError *error) {
        [self showErrorHudWithError:error];
    }];
    
}




- (void)setSelectedCateId:(NSString *)selectedCateId {
    _selectedCateId = selectedCateId;
    QSMatcherItemsProvider* provider = self.cateIdToProvider[selectedCateId];
    self.itemSelectionView.datasource = provider;
    self.itemSelectionView.delegate = provider;
    self.itemSelectionView.selectIndex = provider.selectIndex;
    [self.itemSelectionView reloadData];
    [self.itemSelectionView offsetToZero:YES];
}

#pragma mark canvas
- (void)canvasView:(QSMatcherCanvasView*)view didTapCategory:(NSDictionary*)categoryDict {
    self.selectedCateId = [QSCommonUtil getIdOrEmptyStr:categoryDict];

}

#pragma mark - Category Selection
- (void)didSelectCategories:(NSArray*)categoryArray {
    [self updateCategory:categoryArray];
}

- (void)updateCategory:(NSArray*)array {
    //Update View
    [self.canvasView bindWithCategory:array];
    
    NSArray* newCategoryIds = [array mapUsingBlock:^id(NSDictionary* dict) {
        return [QSCommonUtil getIdOrEmptyStr:dict];
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
        __block NSString* cateId = [QSCommonUtil getIdOrEmptyStr:categoryDict];
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
    [self.canvasView setItem:itemDict forCategory:categoryDict];
}
- (void)matcherItemProvider:(QSMatcherItemsProvider*)provider didFinishNetworkLoading:(NSDictionary*)categoryDict {
    if ([[QSCommonUtil getIdOrEmptyStr:categoryDict] isEqualToString:self.selectedCateId]) {
        [self.itemSelectionView reloadData];
    }
}
@end
