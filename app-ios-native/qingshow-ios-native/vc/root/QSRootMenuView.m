//
//  QSRootMenuView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/2/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSRootMenuView.h"
#import "QSAppDelegate.h"
#import "UIImage+BlurryImage.h"
#import "UIView+ScreenShot.h"
#import "UINib+QSExtension.h"
#import "QSUnreadManager.h"

@interface QSRootMenuView ()
@property (strong, nonatomic) NSMutableArray* itemArray;
@property (assign, nonatomic) int currentType;
@end

@implementation QSRootMenuView
+ (QSRootMenuView*)generateView
{
    QSRootMenuView* v = [UINib generateViewWithNibName:@"QSRootMenuView"];
    CGRect rect = [UIScreen mainScreen].bounds;
    v.frame = rect;

    return v;
}

#pragma mark - Animation
- (void)showMenuAnimationComple:(VoidBlock)block
{
    [self updateItemDot];
    self.bgImageView.hidden = YES;
    UIImage *img = [((QSAppDelegate*)[UIApplication sharedApplication].delegate).window makeScreenShot];
    self.bgImageView.alpha = 0.f;
    self.bgImageView.image = [img blurryImageWithBlurLevel:5.f];
    

    self.bgImageView.hidden = NO;
    self.containerView.layer.transform = CATransform3DMakeTranslation(-self.containerView.frame.size.width, 0, 0);
    [UIView animateWithDuration:0.2 animations:^{
        self.bgImageView.alpha = 1.f;
        self.containerView.layer.transform = CATransform3DMakeTranslation(0, 0, 0);
    } completion:^(BOOL finished) {
        if (block) {
            block();
        }
    }];
}
- (void)hideMenuAnimationComple:(VoidBlock)block
{
    self.bgImageView.alpha = 1.f;
    self.containerView.layer.transform = CATransform3DMakeTranslation(0, 0, 0);

    [UIView animateWithDuration:0.2 animations:^{
        self.containerView.layer.transform = CATransform3DMakeTranslation(-self.containerView.frame.size.width, 0, 0);
        self.bgImageView.alpha = 0.f;
    } completion:^(BOOL finished) {
        if (block && finished) {
            block();
        }
    }];
}

#pragma mark - Life Cycle
- (void)awakeFromNib
{
    NSArray *typeArray = @[
                           @(QSRootMenuItemMeida),
                           @(QSRootMenuItemMatcher),
                           @(QSRootMenuItemDiscount),
                           @(QSRootMenuItemMy)];
    self.itemArray = [@[] mutableCopy];

    for (int i = 0; i < typeArray.count; i++) {
        NSNumber* typeNum = typeArray[i];
        QSRootMenuItem* item = [QSRootMenuItem generateItemWithType:typeNum.intValue];
        item.delegate = self;
        [self.itemArray addObject:item];
        [self.containerView addSubview:item];
    }
    self.currentType = -1;
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
    float deltaY = (size.height - (QSRootMenuItemHeight * self.itemArray.count)) / 2;
    float originX = (self.containerView.frame.size.width - QSRootMenuItemWidth) / 2;
    
    for (int i = 0; i < self.itemArray.count; i++) {
        QSRootMenuItem* item = self.itemArray[i];
        CGRect frame = item.frame;
        frame.origin.x = originX;
        frame.origin.y = i * QSRootMenuItemHeight + deltaY;
        frame.size.width = QSRootMenuItemWidth;
        frame.size.height = QSRootMenuItemHeight;
        item.frame = frame;
    }
    
}

#pragma mark - QSRootMenuItemDelegate
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self didTapView:nil];
}
- (void)menuItemPressed:(QSRootMenuItem*)item
{
    QSRootMenuItemType oldType = self.currentType;
    self.currentType = item.type;
    for (QSRootMenuItem* i in self.itemArray) {
        [i setHover:(i == item)];

    }
    
    if ([self.delegate respondsToSelector:@selector(rootMenuItemPressedType:oldType:)]) {
        [self.delegate rootMenuItemPressedType:item.type oldType:oldType];
    }
}


- (void)hoverItemType:(QSRootMenuItemType)type {
    QSRootMenuItem* item = [self _findItemWithType:type];;
    self.currentType = type;
    for (QSRootMenuItem* i in self.itemArray) {
        [i setHover:(i == item)];
        
    }
}
- (void)triggerItemTypePressed:(QSRootMenuItemType)type {
    QSRootMenuItem* item = [self _findItemWithType:type];;
    
    if (item) {
        [self menuItemPressed:item];
    }
}
- (IBAction)didTapView:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(rootMenuViewDidTapBlankView)]) {
        [self.delegate rootMenuViewDidTapBlankView];
    }
}


- (QSRootMenuItem*)_findItemWithType:(QSRootMenuItemType)type {
    for (QSRootMenuItem* i in self.itemArray) {
        if (i.type == type) {
            return i;
        }
    }
    return nil;
}

- (void)updateItemDot {
    for (QSRootMenuItem* i in self.itemArray) {
        if ([[QSUnreadManager getInstance] shouldShowDotAtMenuItem:i.type]) {
            [i showDot];
        } else {
            [i hideDot];
        }

    }
}
@end
