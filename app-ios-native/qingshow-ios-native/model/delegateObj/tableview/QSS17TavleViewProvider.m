//
//  QSS17TavleViewProvider.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/25.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17TavleViewProvider.h"
#import "QSShowUtil.h"

#define SS17CellId @"QSS17TopShowCellId"
#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)

@implementation QSS17TavleViewProvider
@dynamic delegate;


- (id)initWithArray:(NSArray *)array
{
    if (self = [super initWithCellNib:[UINib nibWithNibName:@"QSS17TopShowCell" bundle:nil] identifier:SS17CellId]) {
        self.dataArray = array;
    }
    return self;
}


- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSS17TopShowCell" bundle:nil] forCellReuseIdentifier:SS17CellId];
}
- (CGFloat)getHeight
{
    if (w == 320) {
        if (h == 568) {
            return 568/3;
        }
        return 180;
    }
    else if(w == 375)
    {
        return 667/3;
    }
    else
    {
        return 736/3;
    }
}


#pragma mark -UITableViewDataSource

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [self getHeight];
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (self.dataArray.count) {
        return self.dataArray.count;
    }
    else
    {
        return 1;
    }
//    return self.dataArray.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSS17TopShowCell *cell = [tableView dequeueReusableCellWithIdentifier:SS17CellId forIndexPath:indexPath];
    if (cell == nil) {
        cell = [[[NSBundle mainBundle]loadNibNamed:@"QSS17TopShowCell" owner:nil options:nil]lastObject];
    }
    cell.userInteractionEnabled = YES;
    //cell.selectionStyle = UITableViewCellSelectionStyleNone;
    //查看网络返回的数据
    NSLog(@"datadic = %@",self.dataArray);
    
    [cell bindWithDataDic:[_dataArray firstObject][indexPath.row*2] andAnotherDic:nil];
    
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self.delegate tableViewCellDidClicked:indexPath.row];
}




@end
