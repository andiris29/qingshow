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



- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSS17TopShowCell" bundle:nil] forCellReuseIdentifier:SS17CellId];
}
- (CGFloat)getHeight
{
//    if (w == 320 && h == 480) {
//        return h/3+10;
//    }
//    else
//    {
//    return h/3-2;
//    }
    float width = w/2+10;
    return width;
}


#pragma mark -UITableViewDataSource

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [self getHeight];
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return (self.resultArray.count + 1) / 2;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSS17TopShowCell *cell = [tableView dequeueReusableCellWithIdentifier:SS17CellId forIndexPath:indexPath];
    if (cell == nil) {
        cell = [[[NSBundle mainBundle]loadNibNamed:@"QSS17TopShowCell" owner:nil options:nil]lastObject];
    }
    cell.userInteractionEnabled = YES;
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    NSDictionary* leftDict = self.resultArray[indexPath.row * 2];
    NSDictionary* rightDict = self.resultArray.count > (indexPath.row * 2  + 1)? self.resultArray[indexPath.row * 2 + 1] : nil;
    
    [cell bindWithDataDic:leftDict andAnotherDic:rightDict];
    cell.contentView.transform  = CGAffineTransformMakeScale(320/w, 320/w);
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDate* date =[QSShowUtil getRecommendDate:self.resultArray[indexPath.row * 2]];
    if ([self.delegate respondsToSelector:@selector(didClickedDate:ofProvider:)]) {
        [self.delegate didClickedDate:date ofProvider:self];
    }
}


@end
