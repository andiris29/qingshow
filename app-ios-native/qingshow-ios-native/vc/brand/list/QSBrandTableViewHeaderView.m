//
//  QSBrandTableViewHeaderView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/20/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBrandTableViewHeaderView.h"
#import <QuartzCore/QuartzCore.h>

@implementation QSBrandTableViewHeaderView

#pragma mark - Static
+ (QSBrandTableViewHeaderView*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSBrandTableViewHeaderView" bundle:nil];
    return [nib instantiateWithOwner:self options:nil][0];
}

#pragma mark - Life Cycle
- (void)awakeFromNib
{
    [super awakeFromNib];
    self.onlineBtn.layer.cornerRadius = 4.f;
    self.onlineBtn.layer.masksToBounds = YES;
    self.offlineBtn.layer.cornerRadius = 4.f;
    self.offlineBtn.layer.masksToBounds = YES;
}

#pragma mark - IBAction
- (IBAction)onlineBtnPressed
{
    if ([self.delegate respondsToSelector:@selector(didClickOnline)]) {
        [self.delegate didClickOnline];
    }
}
- (IBAction)offlineBtnPressed
{
    if ([self.delegate respondsToSelector:@selector(didClickOffline)]) {
        [self.delegate didClickOffline];
    }
}
@end
