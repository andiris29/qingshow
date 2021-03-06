//
//  QSTradeSelectButton.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/18/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTradeSelectButton.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSLayoutUtil.h"
#import <QuartzCore/QuartzCore.h>

#define COLOR_PURPLE [UIColor colorWithRed:169.f/255.f green:26.f/255.f blue:78.f/255.f alpha:1.f]
#define COLOR_GRAY [UIColor colorWithRed:202.f/255.f green:202.f/255.f blue:202.f/255.f alpha:1.f]
#define COLOR_DARK_GRAY [UIColor colorWithRed:27.f/255.f green:27.f/255.f blue:27.f/255.f alpha:1.f]
#define TEXT_FONT [UIFont systemFontOfSize:15.f]
#define PADDING_WIDTH 5.f
#define MIN_WIDTH 40.f

@interface QSTradeSelectButton ()

@property (strong, nonatomic) UILabel* label;
@property (strong, nonatomic) UIImageView* imageView;
@property (strong, nonatomic) UIImageView* checkMarkImgView; //TODO
@end

@implementation QSTradeSelectButton

- (id)init
{
    
    self = [super initWithFrame:CGRectMake(0, 0, 40, 27)];
    if (self) {
        self.label = [[UILabel alloc] initWithFrame:self.bounds];
        self.label.textAlignment = NSTextAlignmentCenter;
        self.label.font = NEWFONT;
        [self addSubview:self.label];
        
        self.imageView = [[UIImageView alloc] initWithFrame:self.bounds];
        [self addSubview:self.imageView];
        
        self.layer.masksToBounds = YES;
        self.layer.cornerRadius = 6.f;
        self.layer.borderWidth = 1.f;
        
        self.isSelected = NO;
        self.userInteractionEnabled = YES;
        UIGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didClick)];
        [self addGestureRecognizer:ges];
        
        self.checkMarkImgView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"create_trade_btn_checkmark"]];
        [self addSubview:self.checkMarkImgView];
        [self.checkMarkImgView sizeToFit];
        self.checkMarkImgView.hidden = YES;
    }
    return self;
}

- (void)didClick
{
    [self sendActionsForControlEvents:UIControlEventTouchUpInside];
}
- (void)setEnabled:(BOOL)enabled
{
    [super setEnabled:enabled];
    [self updateColor];
}

#pragma mark - Layout
- (void)layoutSubviews
{
    self.label.frame = self.bounds;
    self.imageView.frame = self.bounds;
    CGRect rect = self.checkMarkImgView.frame;
    rect.origin.x = self.frame.size.width - rect.size.width;
    rect.origin.y = self.frame.size.height - rect.size.height;
    self.checkMarkImgView.frame = rect;
}
#pragma mark - Getter And Setter
- (void)updateColor
{
    if (!self.enabled) {
        self.backgroundColor = [UIColor colorWithRed:0.9 green:0.9 blue:0.9 alpha:1.f];
        return;
    }
    if (_type == QSTradeSelectButtonTypeText) {
        self.checkMarkImgView.hidden = YES;
        if (self.isSelected) {
            
            self.backgroundColor = COLOR_PURPLE;
            self.layer.borderColor = COLOR_PURPLE.CGColor;
            self.label.textColor = [UIColor whiteColor];
        } else {
            self.backgroundColor = [UIColor whiteColor];
            self.layer.borderColor = COLOR_GRAY.CGColor;
            self.label.textColor = COLOR_DARK_GRAY;
        }
    } else if (_type == QSTradeSelectButtonTypeImage) {
        if (self.isSelected) {
            self.layer.borderColor = COLOR_PURPLE.CGColor;
            self.checkMarkImgView.hidden = NO;
        } else {
            self.layer.borderColor = [UIColor clearColor].CGColor;
            self.checkMarkImgView.hidden = YES;
        }

        self.backgroundColor = [UIColor clearColor];
    }
}
- (void)setType:(QSTradeSelectButtonType)type
{
    _type = type;
    [self updateColor];
}

- (void)setIsSelected:(BOOL)isSelected
{
    _isSelected = isSelected;
    [self updateColor];
}

- (void)setText:(NSString*)text
{
    _text = text;
    self.type = QSTradeSelectButtonTypeText;
    self.label.text = text;
    CGRect rect = self.frame;
    rect.size.width = [QSTradeSelectButton getWidthWithText:_text];
    self.frame = rect;
}
- (void)setImageUrl:(NSURL*)imageUrl
{
    _imageUrl = imageUrl;
    self.type = QSTradeSelectButtonTypeImage;
    [self.imageView setImageFromURL:imageUrl];
    
    CGRect rect = self.frame;
    rect.size = [QSTradeSelectButton getSizeOfImgBtn];
    self.frame = rect;
}


+ (CGFloat)getWidthWithText:(NSString*)text
{
    float baseHeigh = 30.f;
    float baseWidth = [QSLayoutUtil sizeForString:text withMaxWidth:INFINITY height:baseHeigh font:TEXT_FONT].width;
    float width = baseWidth + 2 * PADDING_WIDTH;
    return width < MIN_WIDTH ? MIN_WIDTH : width;
}
+ (CGSize)getSizeOfImgBtn
{
    return CGSizeMake(38.f, 38.f);
}
@end
