//
//  QSNewestHourHeaderView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/25.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSNewestHourTitleView : UIView

@property (weak, nonatomic) IBOutlet UILabel* timeLabel;

+ (instancetype)generateView;
- (void)bindWithDate:(NSDate*)date;
@end
