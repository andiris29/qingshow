//
//  QSS03ItemListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS07ItemListViewController.h"
#import "QSItemListHeaderView.h"
#import "QSShowUtil.h"
#import "QSItemUtil.h"
#import "QSCategoryUtil.h"
#import "QSNetworkKit.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "UIViewController+QSExtension.h"
#import "QSItemListCell.h"
#import "QSCategoryManager.h"

#define QSItemListCellID @"QSItemListCellID"

#define PAGE_ID @"S07 - 搭配清单"


//#import "QSS03ItemShopDetailViewController.h"

@interface QSS07ItemListViewController ()
@property (strong, nonatomic) NSArray* itemArray;
@property (strong, nonatomic) NSMutableArray *orderdArray;

@end

@implementation QSS07ItemListViewController

#pragma mark - Init Method
- (id)initWithShow:(NSDictionary*)showDict
{
    self = [super initWithNibName:@"QSS07ItemListViewController" bundle:nil];
    if (self) {
        self.showDict = showDict;
        self.itemArray = [QSShowUtil getItems:showDict];
         self.orderdArray  = [self refreshItemArray:self.itemArray];
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    UIView* headerView = [QSItemListHeaderView generateView];
    
    self.tableView.tableHeaderView = headerView;
    [self.tableView registerNib:[UINib nibWithNibName:@"QSItemListCell" bundle:nil] forCellReuseIdentifier:QSItemListCellID];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;

    [SHARE_NW_ENGINE queryShowDetail:self.showDict onSucceed:^(NSDictionary * dict) {
        self.showDict = dict;
        [self.tableView reloadData];
    } onError:^(NSError *error) {
        
    }];
    self.view.alpha = 0.9f;
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    if([UIScreen mainScreen].bounds.size.width == 414)
    {
        self.tableView.tableHeaderView.transform = CGAffineTransformMakeScale(1.2, 1.2);
    }
   
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [MobClick beginLogPageView:PAGE_ID];
    
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)closeBtnPressed:(id)sender {

    if ([self.delegate respondsToSelector:@selector(didClickItemListCloseBtn:)]) {
        [self.delegate didClickItemListCloseBtn:self.showBackBtn];
    }
}

#pragma mark - UITableView Datasource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.orderdArray.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    QSItemListCell *cell = [tableView dequeueReusableCellWithIdentifier:QSItemListCellID forIndexPath:indexPath];
    [cell bindWithDic:self.orderdArray[indexPath.row]];

    if ([UIScreen mainScreen].bounds.size.width == 414) {
        cell.contentView.transform = CGAffineTransformMakeScale(1.2, 1.2);
    }
    return cell;
}
#pragma mark - 数组排序
- (NSMutableArray *)refreshItemArray:(NSArray *)itemArray
{
        NSMutableArray *array = [NSMutableArray arrayWithArray:itemArray];
    
        
        for (int i = 0; i < array.count; i ++) {
            
            for (int j = i+1; j < array.count; j ++) {
                int orderI = [self getOrderFromDic:array[i]];
                int orderJ = [self getOrderFromDic:array[j]];
                if (orderI > orderJ) {
                    id dic = array[j];
                    array[j] = array[i];
                    array[i] = dic;
                    dic = nil;
                }
            }
        }
        return array;


}
- (int)getOrderFromDic:(NSDictionary *)itemDict
{
    NSDictionary* categoryDict = [QSItemUtil getCategoryRef:itemDict];
    if (categoryDict) {
        return [QSCategoryUtil getOrder:categoryDict].intValue;
    } else {
        NSString* categoryId = [QSItemUtil getCategoryStr:itemDict];
        QSCategoryManager *manager = [QSCategoryManager getInstance];
        NSDictionary *dict =  [manager findCategoryOfId:categoryId];
        return [QSCategoryUtil getOrder:dict].intValue;
    }
    return 0;
}

#pragma mark - UITableView Delegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSDictionary* itemDict = self.orderdArray[indexPath.row];
    if (itemDict) {
        [self showItemDetailViewController:itemDict];
    }
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [UIScreen mainScreen].bounds.size.width/32*5+5;
}

@end
