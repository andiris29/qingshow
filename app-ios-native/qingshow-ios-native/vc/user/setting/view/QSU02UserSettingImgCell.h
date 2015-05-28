//
//  QSU02UserSettingImgCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSU02UserSettingImgCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIImageView *headImgView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;

@property (nonatomic,assign)NSInteger row;
- (void)imgCellBindWithDic:(NSDictionary *)peopleDic;
@end
