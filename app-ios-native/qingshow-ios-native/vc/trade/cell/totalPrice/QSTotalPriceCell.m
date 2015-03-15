//
//  QSTotalPriceCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTotalPriceCell.h"
#import <QuartzCore/QuartzCore.h>

@implementation QSTotalPriceCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    self.submitBtn.layer.cornerRadius = 4.f;
    self.submitBtn.layer.masksToBounds = YES;
}

- (CGFloat)getHeightWithDict:(NSDictionary*)dict
{
    return 65.f;
}
- (IBAction)submitBtnPressed:(id)sender
{
    
}
@end
