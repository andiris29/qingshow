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

@interface QSRootMenuView ()
@property (strong, nonatomic) NSMutableArray* itemArray;
@end

@implementation QSRootMenuView
+ (QSRootMenuView*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSRootMenuView" bundle:nil];
    NSArray* array = [nib instantiateWithOwner:self options:nil];
    QSRootMenuView* v = array[0];
    CGRect rect = [UIScreen mainScreen].bounds;
    v.frame = rect;
    v.bgImageView.backgroundColor = [UIColor lightGrayColor];
//    v.bgImageView.layer.transform = CATransform3DMakeScale(1.03, 1.02, 0);
    return v;
}

#pragma mark - Animation
- (void)showMenuAnimationComple:(VoidBlock)block
{
    self.bgImageView.hidden = YES;
    UIImage *img = [((QSAppDelegate*)[UIApplication sharedApplication].delegate).window makeScreenShow];
    self.bgImageView.image = [img blurryImageWithBlurLevel:5.f];
    
    self.bgImageView.alpha = 0.f;
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

    NSArray* typeArray = @[@1, @2, @3, @8, @9];   //1,2,3,8,9
    self.itemArray = [@[] mutableCopy];

    for (int i = 0; i < 5; i++) {
        NSNumber* typeNum = typeArray[i];
        QSRootMenuItem* item = [QSRootMenuItem generateItemWithType:typeNum.intValue];
        item.delegate = self;
        [self.itemArray addObject:item];
        [self.containerView addSubview:item];
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
    float deltaY = (size.height - (QSRootMenuItemHeight * self.itemArray.count)) / 2;
    float originX = (self.containerView.frame.size.width - QSRootMenuItemWidth) / 2;
    
    for (int i = 0; i < 5; i++) {
        QSRootMenuItem* item = self.itemArray[i];
        
        CGRect frame = item.frame;
        frame.origin.x = originX;
        frame.origin.y = i * QSRootMenuItemHeight + deltaY;
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
    if ([self.delegate respondsToSelector:@selector(rootMenuItemPressedType:)]) {
        [self.delegate rootMenuItemPressedType:item.type];
    }
}
- (IBAction)didTapView:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(rootMenuViewDidTapBlankView)]) {
        [self.delegate rootMenuViewDidTapBlankView];
    }
}

@end
