//
//  QST01ShowTradeProvider.m
//  qingshow-ios-native
//
//  Created by mhy on 15/9/6.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QST01ShowTradeProvider.h"

#import "QSTradeUtil.h"
#define T01SHOWTRADECELLHEIGHT (216)
#define SHOWTRADECELLIDENTIFIER (@"showTradeCellIdentifier")

@implementation QST01ShowTradeProvider
@synthesize delegate = _delegate;

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
    cell.delegate = self;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary *tradeDic = self.resultArray[indexPath.row];
    NSString *itemId = [QSTradeUtil getItemId:tradeDic];
    if ([self.delegate respondsToSelector:@selector(didTapTradeCell:)]) {
        [self.delegate didTapTradeCell:itemId];
    }
}

- (void)didtapHeaderInT01VC:(NSDictionary *)peopleDic
{
    if ([self.delegate respondsToSelector:@selector(didTapHeaderInT01Cell:)]) {
        [self.delegate didTapHeaderInT01Cell:peopleDic];
    }
}
@end
