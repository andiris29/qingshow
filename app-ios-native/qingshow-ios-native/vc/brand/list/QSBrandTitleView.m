//
//  QSBrandTitleView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/20/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBrandTitleView.h"

@implementation QSBrandTitleView

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

+ (QSBrandTitleView*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSBrandTitleView" bundle:nil];
    return [nib instantiateWithOwner:self options:nil][0];
}
@end
