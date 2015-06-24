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
@property (strong , nonatomic)QSS21TableViewProvider *provider;

@end

@implementation QSS21CategorySelectorVC

#pragma mark - Init
- (instancetype)initWithCategories:(NSArray*)array {
    self = [super initWithNibName:@"QSS21CategorySelectorVC" bundle:nil];
    if (self) {
#warning TODO @lsy 这个界面不发送网络请求，直接根据传进来的categories创建tableView
        self.categories = array;
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
#warning TODO//获取provider的resultArray
    NSArray *result = [self.provider getResultArray];
    NSLog(@"%@",result);
}

@end
