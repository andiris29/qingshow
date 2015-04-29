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

@implementation QSRootMenuItem

+ (QSRootMenuItem*)generateItemWithType:(int)type
{
//    NSArray* textArray = @[
//                           @"闪点推荐",//1
//                           @"美搭榜单",//2
//                           @"人气用户",//3
//                           @"百里挑衣",//4
//                           @"时尚包包",//5
//                           @"百搭配饰",//6
//                           @"个性潮鞋",//7
//                           @"潮流时尚",//8
//                           @"品牌商户",//9
//                           ];
    NSArray* textArray = @[
                           @"个人",   //1 （第一个）
                           @"时尚情报",//2 （第四个）
                           @"人气用户",//3
                           @"百里挑衣",//4
                           @"时尚包包",//5
                           @"百搭配饰",//6
                           @"个性潮鞋",//7
                           @"潮流单品",//8 （第三个）
                           @"美搭榜单",//9 （第二个）
                           ];

//    NSArray* colorArray =
//    @
//    [
//     [UIColor colorWithRed:244.f/255.f green:75.f/255.f blue:15.f/255.f alpha:1.f],
//     [UIColor colorWithRed:255.f/255.f green:54.f/255.f blue:121.f/255.f alpha:1.f],
//     [UIColor colorWithRed:254.f/255.f green:170.f/255.f blue:56.f/255.f alpha:1.f],
//     [UIColor colorWithRed:255.f/255.f green:59.f/255.f blue:68.f/255.f alpha:1.f],
//     [UIColor colorWithRed:61.f/255.f green:188.f/255.f blue:235.f/255.f alpha:1.f],
//     [UIColor colorWithRed:110.f/255.f green:109.f/255.f blue:211.f/255.f alpha:1.f],
//     [UIColor colorWithRed:72.f/255.f green:207.f/255.f blue:174.f/255.f alpha:1.f],
//     [UIColor colorWithRed:244.f/255.f green:104.f/255.f blue:153.f/255.f alpha:1.f],
//     [UIColor colorWithRed:124.f/255.f green:204.f/255.f blue:20.f/255.f alpha:1.f],
//    ];
    UINib* nib = [UINib nibWithNibName:@"QSRootMenuItem" bundle:nil];
    NSArray* array = [nib instantiateWithOwner:self options:nil];
    QSRootMenuItem* item = array[0];
    item->_type = type;
    item.label.text = textArray[type - 1];
    
    [item.button setImage:[UIImage imageNamed:[NSString stringWithFormat:@"root_menu_icon0%d",type]] forState:UIControlStateNormal];
//    item.button.backgroundColor = colorArray[type - 1];
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
