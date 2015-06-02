//
//  QSS17TopShowCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/21.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSShowUtil.h"
@interface QSS17TopShowCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *dayLabel;
@property (weak, nonatomic) IBOutlet UILabel *yearLabel;
@property (weak, nonatomic) IBOutlet UILabel *weekLabel;
@property (weak, nonatomic) IBOutlet UIButton *favoBtn;
@property (weak, nonatomic) IBOutlet UILabel *favoNumLabel;
@property (weak, nonatomic) IBOutlet UIImageView *leftImgView;
@property (weak, nonatomic) IBOutlet UIImageView *rightImgView;
@property (weak, nonatomic) IBOutlet UIView *colorView;

//- (IBAction)favoBtnPressed:(UIButton *)sender;


- (void)bindWithDataDic:(NSDictionary *)fDic andAnotherDic:(NSDictionary *)sDic;
@end