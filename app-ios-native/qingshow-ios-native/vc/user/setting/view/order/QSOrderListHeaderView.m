//
//  QSOrderListHeaderView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSOrderListHeaderView.h"

@implementation QSOrderListHeaderView
- (IBAction)changeSegmentValue:(id)sender {
    UISegmentedControl *seg = sender;
    [self.delegate changeValueOfSegment:seg.selectedSegmentIndex];
}

+ (instancetype)makeView
{
    UINib* nib = [UINib nibWithNibName:@"QSOrderListHeaderView" bundle:nil];
    return [nib instantiateWithOwner:self options:nil][0];
}

- (void)awakeFromNib {
    
}
- (IBAction)didTapPhone:(UITapGestureRecognizer*)ges {
    if (!self.label2.text.length) {
        return;
    }
    if ([self.delegate respondsToSelector:@selector(didTapPhone:)]) {
        [self.delegate didTapPhone:self.label2.text];
    }
}

@end
