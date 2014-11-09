//
//  QSModelBadgeView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSModelBadgeView.h"

@interface QSModelBadgeView ()

@property (strong, nonatomic) QSSectionButtonGroup* btnGroup;

@end

@implementation QSModelBadgeView

#pragma mark - Static Method
+ (QSModelBadgeView*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSModelBadgeView" bundle:nil];
    NSArray* array = [nib instantiateWithOwner:self options:nil];
    return array[0];
}

#pragma mark - Life Cycle
- (void)awakeFromNib
{
    self.btnGroup = [[QSSectionButtonGroup alloc] init];
    [self.sectionGroupContainer addSubview:self.btnGroup];
    NSArray* titleArray = @[@"搭配",@"关注",@"粉丝"];
    for (int i = 0; i < 3; i++) {
        [self.btnGroup setNumber:@(0).stringValue atIndex:i];
        [self.btnGroup setTitle:titleArray[i] atIndex:i];
    }
    [self.btnGroup setSelect:0];
    self.btnGroup.delegate = self;
}

#pragma mark - Binding
- (void)bindWithDict:(NSDictionary*)peopleDict
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

#pragma mark - QSSectionButtonGroupDelegate
- (void)groupButtonPressed:(int)index
{
    if ([self.delegate respondsToSelector:@selector(changeToSection:)]) {
        [self.delegate changeToSection:index];
    }
}
- (void)singleButtonPressed
{
    if ([self.delegate respondsToSelector:@selector(followButtonPressed)]){
        [self.delegate followButtonPressed];
    }
}

@end
