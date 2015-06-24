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

@interface QSS20MatcherViewController ()

@property (strong, nonatomic) QSMatcherItemSelectionView* itemSelectionView;
@property (strong, nonatomic) QSMatcherCanvasView* canvasView;
@property (strong, nonatomic) NSArray* categoryArray;

@property (strong, nonatomic) NSMutableDictionary* cateIdToItems;
@property (strong, nonatomic) NSString* selectedCateId;
@property (strong, nonatomic) NSMutableDictionary* cateIdToSelectedItemIndex;
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
    // Do any additional setup after loading the view from its nib.
    self.itemSelectionView = [QSMatcherItemSelectionView generateView];
    self.itemSelectionView.frame = self.itemSelectionContainer.bounds;
    [self.itemSelectionContainer addSubview:self.itemSelectionView];
    self.itemSelectionView.datasource = self;
    self.itemSelectionView.delegate = self;
    [self.itemSelectionView reloadData];
    
    
    self.canvasView = [QSMatcherCanvasView generateView];
    self.canvasView.delegate = self;
    self.canvasView.frame = self.canvasContainer.bounds;
    [self.canvasContainer addSubview:self.canvasView];
    
    
    [SHARE_NW_ENGINE matcherQueryCategoriesOnSucceed:^(NSArray *array, NSDictionary *metadata) {
        self.categoryArray = array;
        [self.canvasView bindWithCategory:self.categoryArray];
        
        if (self.categoryArray.count) {
            NSDictionary* c = self.categoryArray[0];
            self.selectedCateId = [QSCommonUtil getIdOrEmptyStr:c];
        }
        for (NSDictionary* categoryDict in self.categoryArray) {
            __block NSString* cateId = [QSCommonUtil getIdOrEmptyStr:categoryDict];
            [SHARE_NW_ENGINE matcherQueryItemsCategory:categoryDict page:1 onSucceed:^(NSArray *array, NSDictionary *metadata) {
                if (array.count) {
                    [self.canvasView setItem:array[0] forCategory:categoryDict];
                }

                [self.cateIdToItems setValue:array forKey:cateId];
                [self.cateIdToSelectedItemIndex setValue:@0 forKey:cateId];
                if (self.selectedCateId == cateId) {
                    [self.itemSelectionView reloadData];
                }
            } onError:nil];
        }
        
        
    } onError:nil];
    self.cateIdToItems = [@{} mutableCopy];
    self.cateIdToSelectedItemIndex = [@{} mutableCopy];
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
    [self.navigationController pushViewController:[[QSS21CategorySelectorVC alloc] initWithCategories:self.categoryArray] animated:YES];
}

- (IBAction)menuBtnPressed:(id)sender {
    [self.menuProvider didClickMenuBtn];
}

- (IBAction)submitButtonPressed:(id)sender {
    NSMutableArray* items = [@[] mutableCopy];
    for (NSDictionary* cateDict in self.categoryArray) {
        NSString* cateId =[QSCommonUtil getIdOrEmptyStr:cateDict];
        NSNumber* index = self.cateIdToSelectedItemIndex[cateId];
        NSArray* itemArray = self.cateIdToItems[cateId];
        NSDictionary* item = itemArray[index.intValue];
        [items addObject:item];
    }
    UIImage* snapshot = [self.canvasView makeScreenShot];
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



#pragma mark - QSMatcherItemSelectionView
- (NSUInteger)numberOfItemInSelectionView:(QSMatcherItemSelectionView*)view {
    NSArray* cs = self.cateIdToItems[self.selectedCateId];
    return cs.count;
}
- (NSDictionary*)selectionView:(QSMatcherItemSelectionView*)view itemDictAtIndex:(NSUInteger)index {
    NSArray* cs = self.cateIdToItems[self.selectedCateId];
    return cs[index];
}

- (void)selectionView:(QSMatcherItemSelectionView*)view didSelectItemAtIndex:(NSUInteger)index {
    NSLog(@"%d", index);
    NSArray* cs = self.cateIdToItems[self.selectedCateId];
    NSDictionary* item = cs[index];
    [self.canvasView setItem:item forCategoryId:self.selectedCateId];
    [self.cateIdToSelectedItemIndex setValue:@(index) forKey:self.selectedCateId];
}

#pragma mark canvas
- (void)canvasView:(QSMatcherCanvasView*)view didTapCategory:(NSDictionary*)categoryDict {
    self.selectedCateId = [QSCommonUtil getIdOrEmptyStr:categoryDict];
    [self.itemSelectionView reloadData];
}
@end
