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

@interface QSS20MatcherViewController ()

@property (strong, nonatomic) QSMatcherItemSelectionView* itemSelectionView;

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
    [self.navigationController pushViewController:[[QSS21CategorySelectorVC alloc] init] animated:YES];
}

- (IBAction)menuBtnPressed:(id)sender {
    [self.menuProvider didClickMenuBtn];
}



#pragma mark - QSMatcherItemSelectionView
- (NSUInteger)numberOfItemInSelectionView:(QSMatcherItemSelectionView*)view {
    return 7;
}
- (NSDictionary*)selectionView:(QSMatcherItemSelectionView*)view itemDictAtIndex:(NSUInteger)index {
    
    NSArray* array = @[@{}, @{}, @{}, @{}, @{}];
    
    return array[index % array.count];
}
@end
