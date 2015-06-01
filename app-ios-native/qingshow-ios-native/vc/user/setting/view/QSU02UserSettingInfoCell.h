//
//  QSU02UserSettingInfoCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface QSU02UserSettingInfoCell : UITableViewCell<UITextFieldDelegate>
@property (weak, nonatomic) IBOutlet UILabel *typeLabel;
@property (weak, nonatomic) IBOutlet UITextField *infoTextField;

@property (nonatomic,assign)NSInteger row;
@property (nonatomic,strong)UIViewController *superVC;

- (void)infoCellBindWithDic:(NSDictionary *)peopleDic;

@end
