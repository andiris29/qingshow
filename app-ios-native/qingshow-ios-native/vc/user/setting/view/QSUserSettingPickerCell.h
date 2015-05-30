//
//  QSUserSettingPickerCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSUserSettingPickerCell : UITableViewCell<UIPickerViewDelegate,UIPickerViewDataSource>
@property (weak, nonatomic) IBOutlet UILabel *typeLabel;

@property (weak, nonatomic) IBOutlet UIButton *styleBtn;

@property (weak, nonatomic) IBOutlet UIPickerView *chooseStylePickerView;

- (IBAction)changeStyleBtnPressed:(id)sender;

@property(nonatomic,assign)NSInteger row;
- (void)bindWithDic:(NSDictionary *)peopleDic;

@end
