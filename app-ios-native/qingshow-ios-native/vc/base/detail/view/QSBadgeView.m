//
//  QSModelBadgeView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBadgeView.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import <QuartzCore/QuartzCore.h>
#import "QSPeopleUtil.h"

@interface QSBadgeView ()

@property (weak, nonatomic) IBOutlet UIImageView *backgroundImageView;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;

@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *statusLabel;

@end

@implementation QSBadgeView

#pragma mark - Static Method
+ (QSBadgeView*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSBadgeView" bundle:nil];
    NSArray* array = [nib instantiateWithOwner:self options:nil];
    QSBadgeView* v = array[0];
    [v updateView];
    return array[0];
}

#pragma mark - Life Cycle
- (void)awakeFromNib
{
    self.iconImageView.layer.cornerRadius = self.iconImageView.frame.size.height / 2;
    self.iconImageView.layer.masksToBounds = YES;
}

- (void)updateView
{
    CGRect rect = self.frame;
    rect.size.width = [UIScreen mainScreen].bounds.size.width;
    self.frame = rect;
}

#pragma mark - Binding
- (void)bindWithPeopleDict:(NSDictionary*)peopleDict
{
    self.iconImageView.layer.cornerRadius = self.iconImageView.frame.size.height / 2;
    self.iconImageView.layer.masksToBounds = YES;
    
    self.nameLabel.text = [QSPeopleUtil getNickname:peopleDict];
    NSMutableString* statusStr = [@"" mutableCopy];
    NSString* height = [QSPeopleUtil getHeight:peopleDict];
    if (height && height.length) {
        [statusStr appendFormat:@"%@cm ", height];
    }
    NSString* weight = [QSPeopleUtil getWeight:peopleDict];
    if (weight && weight.length) {
        [statusStr appendFormat:@"%@kg ", weight];
    }
    self.statusLabel.text = statusStr;
    
    
    [self.iconImageView setImageFromURL:[QSPeopleUtil getHeadIconUrl:peopleDict] placeHolderImage:[UIImage imageNamed:@"people_placehold"] animation:YES];
    [self.backgroundImageView setImageFromURL:[QSPeopleUtil getBackgroundUrl:peopleDict] placeHolderImage:nil animation:YES];
}

@end
