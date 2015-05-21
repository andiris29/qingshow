//
//  QSS17ViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17ViewController.h"
#import "QSNetworkKit.h"
#import "QSS17Cell.h"

#define PAGE_ID @"美搭榜单"

@interface QSS17ViewController ()

@property(nonatomic,strong)QSTableViewBasicProvider *delegateObj;
@property(nonatomic,strong)NSDictionary *imgDic;
@property(nonatomic,strong)NSDictionary *dateDic;

@end


@implementation QSS17ViewController


- (instancetype)initWithDateDic:(NSDictionary *)dateDic imgDic:(NSDictionary *)imgDic
{
    if (self = [super initWithNibName:@"QSS17ViewController" bundle:nil]) {
        __weak QSS17ViewController *weakSelf = self;
        self.imgDic = imgDic;
        self.dateDic = dateDic;
        self.delegateObj = [[QSTableViewBasicProvider alloc]init];
        self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
            return nil;
        };
         [self.delegateObj fetchDataOfPage:1];
        
    }
    return self;
}	
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self.delegateObj bindWithTableView:self.topShowTableView];
    self.delegateObj.delegate = self;
    
    //override registerCell
    [self tableViewProviderRegisterCellWithNibName:@"QSS17" bundle:nil];
    
}

- (void)tableViewProviderRegisterCellWithNibName:(NSString *)nibName bundle:(NSBundle*)bundle
{
    
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
