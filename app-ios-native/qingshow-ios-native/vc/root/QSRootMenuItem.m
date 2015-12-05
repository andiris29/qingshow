//
//  QSRootMenuItem.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/2/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSRootMenuItem.h"
#import <QuartzCore/QuartzCore.h>
@interface QSRootMenuItem ()


@end

NSString* getTitleFromType(QSRootMenuItemType type) {
    NSArray* titleArray = @[
                            @"",
                            @"个人空间",
                            @"美搭榜单",
                            @"百搭秀场",
                            @"我的订单"];
    if ((NSUInteger)type < titleArray.count) {
        return titleArray[type];
    } else {
        return nil;
    }
}

UIImage* getIconImageFromType(QSRootMenuItemType type) {
    NSArray* titleArray = @[
                            @"",
                            @"root_menu_icon_my",
                            @"root_menu_icon_meida",
                            @"root_menu_icon_matcher",
                            @"root_menu_icon_discount"];
    if ((NSUInteger)type < titleArray.count) {
        NSString* str = titleArray[type];
        return [UIImage imageNamed:str];
    } else {
        return nil;
    }
}

UIImage* getIconHoverImageFromType(QSRootMenuItemType type) {
    NSArray* titleArray = @[
                            @"",
                            @"root_menu_icon_my_hover",
                            @"root_menu_icon_meida_hover",
                            @"root_menu_icon_matcher_hover",
                            @"root_menu_icon_discount_hover"];
    if ((NSUInteger)type < titleArray.count) {
        NSString* str = titleArray[type];
        return [UIImage imageNamed:str];
    } else {
        return nil;
    }
}




@implementation QSRootMenuItem

+ (QSRootMenuItem*)generateItemWithType:(QSRootMenuItemType)type
{

    UINib* nib = [UINib nibWithNibName:@"QSRootMenuItem" bundle:nil];
    NSArray* array = [nib instantiateWithOwner:self options:nil];
    QSRootMenuItem* item = array[0];
    item->_type = type;
    item.label.text = getTitleFromType(type);
    
    [item.button setImage:getIconImageFromType(type) forState:UIControlStateNormal];
    [item.button setImage:getIconHoverImageFromType(type) forState:UIControlStateHighlighted];
    [item.button setImage:getIconHoverImageFromType(type) forState:UIControlStateSelected];
    item.button.backgroundColor = [UIColor clearColor];
    return item;
}


#pragma mark - Life Cycle
- (void)awakeFromNib {
    self.button.layer.cornerRadius = self.button.frame.size.width / 2;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat radius = self.button.bounds.size.width / 2;
    radius = radius / sqrt(2.0) * 0.75;
    CGPoint center = self.button.center;
    center.x += radius;
    center.y -= radius;
    self.dotView.center = center;
}

#pragma mark - Init
- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}
#pragma mark - IBAction
- (IBAction)buttonPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(menuItemPressed:)]) {
        [self.delegate menuItemPressed:self];
    }
}

#pragma mark - Dot
- (void)showDot {
    self.dotView.hidden = NO;
}
- (void)hideDot {
    self.dotView.hidden = YES;
}


- (void)setHover:(BOOL)fHover {
    [self.button setSelected:fHover];
    
    if (fHover) {
        self.label.textColor = [UIColor colorWithRed:120.f/255.f green:120.f/255.f blue:120.f/255.f alpha:1.f];
    } else {
        self.label.textColor = [UIColor whiteColor];
    }
}
@end
