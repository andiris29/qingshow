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
    NSArray* titleArray = @[@"我的搭配",
                            @"美搭榜单",
                            @"我的收藏",
                            @"个人设置"];
    if ((NSUInteger)type < titleArray.count) {
        return titleArray[type];
    } else {
        return nil;
    }
}

UIImage* getIconImageFromType(QSRootMenuItemType type) {
    NSArray* titleArray = @[@"root_menu_icon_my",
                            @"root_menu_icon_meida",
                            @"root_menu_icon_myfavor",
                            @"root_menu_icon_setting"];
    if ((NSUInteger)type < titleArray.count) {
        NSString* str = titleArray[type];
        return [UIImage imageNamed:str];
    } else {
        return nil;
    }
}

UIImage* getIconHoverImageFromType(QSRootMenuItemType type) {
    NSArray* titleArray = @[@"root_menu_icon_my_hover",
                            @"root_menu_icon_meida_hover",
                            @"root_menu_icon_myfavor_hover",
                            @"root_menu_icon_setting_hover"];
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
- (void)awakeFromNib
{
    self.button.layer.cornerRadius = self.button.frame.size.width / 2;
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


@end
