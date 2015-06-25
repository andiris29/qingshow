//
//  QSS21CategorySelectorVC.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS21CategorySelectorVC.h"
#import "QSS21TableViewProvider.h"

@interface QSS21CategorySelectorVC () <QSS21TableViewProviderDelegate>

@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (strong, nonatomic) NSArray* categories;
@property (strong, nonatomic) NSArray* selectedCategories;
@property (strong , nonatomic)QSS21TableViewProvider *provider;

@end

@implementation QSS21CategorySelectorVC

#pragma mark - Init
- (instancetype)initWithCategories:(NSArray*)array selectedCategories:(NSArray*)selectedCategories {
    self = [super initWithNibName:@"QSS21CategorySelectorVC" bundle:nil];
    if (self) {
#warning self.categories是所有的category，self.selectedCategories是已经被用户选中的category
        self.categories = array;
        self.selectedCategories = selectedCategories;
    }
    return self;
}

#pragma mark - Life Cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setProvider];
    
}
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    
    [self.navigationController.navigationBar setTitleTextAttributes:@{NSFontAttributeName:NEWFONT}];
    
    self.title = @"百搭秀场";
}

#pragma mark -- bindTableView
- (void)setProvider
{
    self.provider = [[QSS21TableViewProvider alloc] init];
    self.provider.delegate = self;
    self.provider.dataArray = self.categories;
    [self.provider bindWithTableView:self.tableView];
}

#pragma mark -- 开始搭配
- (IBAction)goToMakeShow:(UIButton *)sender {
    NSArray *result = [self.provider getResultArray];
    if ([self.delegate respondsToSelector:@selector(didSelectCategories:)]) {
        [self.delegate didSelectCategories:result];
    }
    [self.navigationController popViewControllerAnimated:YES];
}

@end
