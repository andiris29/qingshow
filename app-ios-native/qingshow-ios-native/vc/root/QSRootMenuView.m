//
//  QSRootMenuView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/2/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSRootMenuView.h"

@interface QSRootMenuView ()
@property (strong, nonatomic) NSMutableArray* itemArray;
@end

@implementation QSRootMenuView
+ (QSRootMenuView*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSRootMenuView" bundle:nil];
    NSArray* array = [nib instantiateWithOwner:self options:nil];
    return array[0];
}

#pragma mark - Animation
- (void)showMenuAnimationComple:(VoidBlock)block
{
    self.transform = CGAffineTransformMakeScale(0.f, 0.f);
    self.alpha = 0.f;
    [UIView animateWithDuration:0.2 animations:^{
        self.transform = CGAffineTransformMakeScale(1.f, 1.f);
        self.alpha = 1.f;
    } completion:^(BOOL finished) {
        if (block) {
            block();
        }
    }];
}
- (void)hideMenuAnimationComple:(VoidBlock)block
{
    self.transform = CGAffineTransformMakeScale(1.f, 1.f);
    self.alpha = 1.f;
    [UIView animateWithDuration:0.2 animations:^{
        self.transform = CGAffineTransformMakeScale(0.1f, 0.1f);
        self.alpha = 0.f;
    } completion:^(BOOL finished) {
        self.transform = CGAffineTransformMakeScale(0.f, 0.f);
        if (block && finished) {
            block();
        }
    }];
}

#pragma mark - Life Cycle
- (void)awakeFromNib
{

    NSArray* typeArray = @[@1, @2, @3, @8, @9];   //1,2,3,8,9
    self.itemArray = [@[] mutableCopy];

    CGSize size = self.frame.size;

    for (int i = 0; i < 5; i++) {
        NSNumber* typeNum = typeArray[i];
        QSRootMenuItem* item = [QSRootMenuItem generateItemWithType:typeNum.intValue];
        item.delegate = self;
        [self.itemArray addObject:item];
        [self addSubview:item];
    }
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

- (void)layoutSubviews
{
    [super layoutSubviews];
    CGSize size = self.frame.size;
    
    for (int i = 0; i < 5; i++) {
        QSRootMenuItem* item = self.itemArray[i];
        item.delegate = self;
        
        float deltaY = (size.height - 2 * QSRootMenuItemHeight) / 3;
        float deltaX = (size.width - 3 * QSRootMenuItemWidth) / 4;
        int index = i;
        int column = index % 3 + 1;
        int row = i / 3 + 1;
        
        CGRect frame = item.frame;
        frame.origin.x = column * deltaX + (column - 1) * QSRootMenuItemWidth;
        frame.origin.y = row * deltaY + (row - 1) * QSRootMenuItemHeight;
        item.frame = frame;
    }
    
}

#pragma mark - QSRootMenuItemDelegate
- (void)menuItemPressed:(QSRootMenuItem*)item
{
    if ([self.delegate respondsToSelector:@selector(rootMenuItemPressedType:)]) {
        [self.delegate rootMenuItemPressedType:item.type];
    }
}


@end
