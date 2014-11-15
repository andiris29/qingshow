//
//  QSModelBadgeView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBadgeView.h"

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
    return array[0];
}

#pragma mark - Life Cycle
- (void)awakeFromNib
{
    self.btnGroup = [[QSSectionButtonGroup alloc] init];
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
    self.statusLabel.text = [NSString stringWithFormat:@"%@cm,%@kg", peopleDict[@"height"], peopleDict[@"weight"]];
#warning head photo
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
