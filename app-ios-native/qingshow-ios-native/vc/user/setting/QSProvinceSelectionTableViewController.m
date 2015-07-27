//
//  QSProvinceSelectionTableViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSProvinceSelectionTableViewController.h"
#import "UIViewController+QSExtension.h"
#import "NSDictionary+QSExtension.h"

@interface QSProvinceSelectionTableViewController ()

@property (strong, nonatomic) NSArray* nameIdListArray;
@property (strong, nonatomic) NSDictionary* idToNameDict;


@property (strong, nonatomic) NSString* provinceName;
@end

@implementation QSProvinceSelectionTableViewController

#pragma mark - Init
- (id)init
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        NSDictionary* dict = [[NSDictionary alloc] initWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"location" ofType:@"plist"]];
        [self configDatasourceWithDict:dict];
    }

    return self;
}
- (id)initWithProvinceName:(NSString*)provinceName cityListDict:(NSDictionary*)citys
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.provinceName = provinceName;
        [self configDatasourceWithDict:citys];
    }
    return self;
}
- (void)configDatasourceWithDict:(NSDictionary*)dict
{
    self.nameIdListArray = [[dict allKeys] sortedArrayUsingComparator:^NSComparisonResult(NSString* obj1, NSString* obj2) {
        return obj1.intValue < obj2.intValue;
    }];
    self.idToNameDict = dict;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    [self hideNaviBackBtnTitle];
    
    self.clearsSelectionOnViewWillAppear = NO;
    if (self.provinceName) {
        self.title = @"选择城市";
    } else {
        self.title = @"选择省份";
    }
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.nameIdListArray.count;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Identifier"];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"Identifier"];
    }
    NSString* key = self.nameIdListArray[indexPath.row];
    id value = self.idToNameDict[key];
    cell.textLabel.font = NEWFONT;
    if ([value isKindOfClass:[NSDictionary class]]) {
        NSDictionary* valueDict = value;
        cell.textLabel.text = [valueDict stringValueForKeyPath:@"name"];
    } else {
        NSString* valueStr = value;
        cell.textLabel.text = valueStr;
    }
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString* key = self.nameIdListArray[indexPath.row];
    id value = self.idToNameDict[key];
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if (self.provinceName) {
        //cidy name
        NSString* valueStr = value;
        if ([self.delegate respondsToSelector:@selector(provinceSelectionVc:didSelectionProvince:city:)]) {
            [self.delegate provinceSelectionVc:self didSelectionProvince:self.provinceName city:valueStr];
            [self.navigationController popToViewController:self.delegate animated:YES];
        }
        
    } else {
        NSDictionary* valueDict = value;
        NSString* name = [valueDict stringValueForKeyPath:@"name"];
        NSDictionary* city = [valueDict dictValueForKeyPath:@"cities"];
        QSProvinceSelectionTableViewController* vc = [[QSProvinceSelectionTableViewController alloc] initWithProvinceName:name cityListDict:city];
        vc.delegate = self.delegate;
        [self.navigationController pushViewController:vc animated:YES];
    }
}


@end
