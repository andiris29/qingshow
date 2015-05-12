//
//  QSBigImageDateView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/6/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSBigImageDateView : UIView

+ (instancetype)makeView;

@property (weak, nonatomic) IBOutlet UILabel* yearLabel;
@property (weak, nonatomic) IBOutlet UILabel* monthLabel;
@property (weak, nonatomic) IBOutlet UILabel* dayLabel;
@property (weak, nonatomic) IBOutlet UILabel* weekLabel;

- (void)bindWithDate:(NSDate*)date;
@end
