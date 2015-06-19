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

@property (strong , nonatomic)QSS21TableViewProvider *provider;

@end

@implementation QSS21CategorySelectorVC

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setProvider];
    
}

#pragma mark -- bindTableView
- (void)setProvider
{
    self.provider = [[QSS21TableViewProvider alloc] init];
    self.provider.delegate = self;
    [self.provider bindWithTableView:self.tableView];
}

#pragma mark -- 开始搭配
- (IBAction)goToMakeShow:(UIButton *)sender {
#warning TODO//获取provider的resultArray
    
}

@end
