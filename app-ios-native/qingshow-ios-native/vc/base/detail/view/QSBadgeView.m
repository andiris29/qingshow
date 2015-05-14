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
#import "QSBrandUtil.h"

@interface QSBadgeView ()

@property (weak, nonatomic) IBOutlet UIImageView *backgroundImageView;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;

@property (weak, nonatomic) IBOutlet UILabel *nameLabel;

@property (weak, nonatomic) IBOutlet UIView *sectionGroupContainer;

@end

@implementation QSBadgeView

#pragma mark - Static Method
+ (QSBadgeView*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSBadgeView" bundle:nil];
    NSArray* array = [nib instantiateWithOwner:self options:nil];
    QSBadgeView* v = array[0];
    v.type = QSSectionButtonGroupTypeImage;
    [v updateView];
    return array[0];
}
+ (QSBadgeView*)generateViewWithType:(QSSectionButtonGroupType)type
{

    UINib* nib = [UINib nibWithNibName:@"QSBadgeView" bundle:nil];
    NSArray* array = [nib instantiateWithOwner:self options:nil];
    QSBadgeView* v = array[0];
    v.type = type;
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
    self.btnGroup = [[QSSectionButtonGroup alloc] initWithType:self.type];
    [self.sectionGroupContainer addSubview:self.btnGroup];
    [self.btnGroup setSelect:0];
    self.btnGroup.delegate = self;
}

#pragma mark - Binding
- (void)bindWithPeopleDict:(NSDictionary*)peopleDict
{
    self.iconImageView.layer.cornerRadius = self.iconImageView.frame.size.height / 2;
    self.iconImageView.layer.masksToBounds = YES;
    
    self.nameLabel.text = [QSPeopleUtil getNickname:peopleDict];
    
    [self.iconImageView setImageFromURL:[QSPeopleUtil getHeadIconUrl:peopleDict] placeHolderImage:[UIImage imageNamed:@"people_placehold"] animation:YES];
    [self.backgroundImageView setImageFromURL:[QSPeopleUtil getBackgroundUrl:peopleDict] placeHolderImage:nil animation:YES];
    
    if ([self.btnGroup.singleButton isKindOfClass:[QSSectionFollowButton class]]) {
        QSSectionFollowButton* f = (QSSectionFollowButton*)self.btnGroup.singleButton;
        [f setFollowed:[QSPeopleUtil getPeopleIsFollowed:peopleDict]];
    }
}
- (void)bindWithBrandDict:(NSDictionary*)brandDict
{
    self.iconImageView.layer.cornerRadius = 0;
    self.iconImageView.layer.masksToBounds = YES;
    
    [self.iconImageView setImageFromURL:[QSBrandUtil getBrandLogoUrl:brandDict]];
    [self.backgroundImageView setImageFromURL:[QSBrandUtil getBrandBgUrl:brandDict] placeHolderImage:nil animation:YES];
    self.nameLabel.text = [QSBrandUtil getBrandName:brandDict];
    if ([self.btnGroup.singleButton isKindOfClass:[QSSectionFollowButton class]]) {
        QSSectionFollowButton* f = (QSSectionFollowButton*)self.btnGroup.singleButton;
        
        [f setFollowed:[QSBrandUtil getHasFollowBrand:brandDict]];
    }
}

#pragma mark - QSSectionButtonGroupDelegate
- (void)groupButtonPressed:(int)index
{
    if ([self.delegate respondsToSelector:@selector(changeToSection:)]) {
        [self.delegate changeToSection:index];
    }
}
- (void)singleButtonPressed
{
    if ([self.delegate respondsToSelector:@selector(singleButtonPressed)]){
        [self.delegate singleButtonPressed];
    }
}

@end
