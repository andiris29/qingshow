//
//  QST01ShowTradeProvider.m
//  qingshow-ios-native
//
//  Created by mhy on 15/9/6.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QST01ShowTradeProvider.h"
#import "QST01ShowTradeCell.h"

#define T01SHOWTRADECELLHEIGHT (216)
#define SHOWTRADECELLIDENTIFIER (@"showTradeCellIdentifier")

@implementation QST01ShowTradeProvider

- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QST01ShowTradeCell" bundle:nil] forCellReuseIdentifier:SHOWTRADECELLIDENTIFIER];
}

#pragma mark - tableView
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.resultArray.count;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return T01SHOWTRADECELLHEIGHT;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QST01ShowTradeCell *cell = (QST01ShowTradeCell *)[tableView dequeueReusableCellWithIdentifier:SHOWTRADECELLIDENTIFIER forIndexPath:indexPath];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    [cell bindWithDic:self.resultArray[indexPath.row]];
    return cell;
}

@end
