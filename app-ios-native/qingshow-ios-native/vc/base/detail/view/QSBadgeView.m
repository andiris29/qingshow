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
@property (weak, nonatomic) IBOutlet UILabel *roleLabel;
@property (weak, nonatomic) IBOutlet UILabel *statusLabel;

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
    self.btnGroup = [[QSSectionButtonGroup alloc] initWithType:self.type];
    [self.sectionGroupContainer addSubview:self.btnGroup];
    [self.btnGroup setSelect:0];
    self.btnGroup.delegate = self;
}

#pragma mark - Binding
- (void)bindWithPeopleDict:(NSDictionary*)peopleDict
{
    self.nameLabel.text = peopleDict[@"name"];
#warning roles
    self.roleLabel.text = @"roles";
    
    self.statusLabel.text = [QSPeopleUtil buildModelStatusString:peopleDict];

    NSString* headPhotoPath = peopleDict[@"portrait"];
    [self.iconImageView setImageFromURL:[NSURL URLWithString:headPhotoPath]];
    
    
    NSNumber* hasFollowed = peopleDict[@"hasFollowed"];
    if (hasFollowed && hasFollowed.boolValue) {
        self.btnGroup.singleButton.textLabel.text = @"取消关注";
    } else {
        self.btnGroup.singleButton.textLabel.text = @"关注";
    }
}
- (void)bindWithBrandDict:(NSDictionary*)brandDict
{
#warning 内容未写
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
