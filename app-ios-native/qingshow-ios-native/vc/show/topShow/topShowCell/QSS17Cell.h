//
//  QSS17Cell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSS17Cell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIImageView *leftImgView;
@property (weak, nonatomic) IBOutlet UIImageView *rightImgView;
@property (weak, nonatomic) IBOutlet UILabel *dayLabel;
@property (weak, nonatomic) IBOutlet UILabel *yearLabel;
@property (weak, nonatomic) IBOutlet UILabel *weekLabel;
@property (weak, nonatomic) IBOutlet UIButton *favoBtn;
@property (weak, nonatomic) IBOutlet UILabel *favoCountLabel;

@property(assign,nonatomic)BOOL isFavoBtnPressed;

- (IBAction)pressFavoBtn:(id)sender;

- (void)bindWithDic:(NSDictionary *)topShowDic;

@end
