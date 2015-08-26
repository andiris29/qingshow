//
//  QSDiscountTaobaoInfoCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSDiscountTaobaoInfoCell.h"
#import "UINib+QSExtension.h"
#import "QSLayoutUtil.h"
#import "QSItemUtil.h"

#define DELTA_X 8.f
#define DELTA_Y 5.f

#define BTN_PADDING_X 5.f
#define BTN_PADDING_Y 5.f

#define ORIGIN_Y 8.f
#define LABEL_HEIGHT 13.f
#define BTN_FONT [UIFont fontWithName:@"FZLanTingHeiS-EL-GB" size:12]


#define COLOR_GRAY [UIColor colorWithRed:112.f/255.f green:112.f/255.f blue:112.f/255.f alpha:1.f]
#define COLOR_PINK [UIColor colorWithRed:240.f/255.f green:149.f/255.f blue:164.f/255.f alpha:1.f]

@interface QSDiscountTaobaoInfoCell ()

@property (strong, nonatomic) NSString* title;
@property (strong, nonatomic) NSArray* compInfos;
@property (strong, nonatomic) NSArray* btnArray;

@property (assign, nonatomic) NSInteger currentSelectIndex;
@property (strong, nonatomic) NSDictionary* itemDict;
@end

@implementation QSDiscountTaobaoInfoCell
- (BOOL)checkComplete {
    if (self.currentSelectIndex < 0) {
        return NO;
    } else {
        return YES;
    }
}

- (NSString*)getResult {
    NSMutableString* str = [@"" mutableCopy];
    if (self.title && self.title.length) {
        [str appendString:self.title];
    }
    [str appendString:@":"];
    if (self.currentSelectIndex >= 0 && self.currentSelectIndex < self.compInfos.count) {
        [str appendString:self.compInfos[self.currentSelectIndex]];
    }
    return str;
}

- (void)awakeFromNib {
    // Initialization code
    [super awakeFromNib];
    
    self.currentSelectIndex = -1;
    
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

+ (instancetype)generateCell {
    return [UINib generateViewWithNibName:@"QSDiscountTaobaoInfoCell"];
}


- (void)bindWithData:(NSDictionary *)itemDict {
    if (self.itemDict == itemDict) {
        return;
    }
    for (UIButton* btn in self.btnArray) {
        [btn removeFromSuperview];
    }
    self.btnArray = nil;
    
    self.itemDict = itemDict;
    
    NSString* str = [QSItemUtil getSkuProperties:itemDict][self.infoIndex];
    NSArray* comps = [str componentsSeparatedByString:@":"];
    self.title = comps[0];
    self.compInfos = [comps subarrayWithRange:NSMakeRange(1, comps.count - 1)];
    
    
    float baseY = 0;
    if (self.title && self.title.length) {
        self.titleLabel.hidden = NO;
        self.titleLabel.text = self.title;
        baseY = self.titleLabel.frame.origin.y + self.titleLabel.frame.size.height + ORIGIN_Y;
    } else {
        self.titleLabel.hidden = YES;
        baseY = ORIGIN_Y;
    }
    
    NSMutableArray* btnArray = [@[] mutableCopy];
    
    float baseX = 0;

    float cellWidth = DISCOUNT_CELL_WIDTH;
    
    for (NSString* comp in self.compInfos) {
        UIButton* btn = [self generateBtn];
        [btn setTitle:comp forState:UIControlStateNormal];
        [QSDiscountTaobaoInfoCell setBtn:btn Hover:NO];
        
        CGSize size = [self getBtnSize:comp];
        
        if (baseX + size.width > cellWidth) {
            baseX = 0;
            baseY += size.height + DELTA_Y;
        }
        
        CGRect rect = CGRectZero;
        rect.origin.x = baseX;
        rect.origin.y = baseY;
        rect.size = size;
        btn.frame = rect;
        
        baseX += size.width + DELTA_X;
        
        [btnArray addObject:btn];
        [self addSubview:btn];
        
    }
    
    
    self.btnArray = btnArray;
    
    CGRect lineRect = self.lineView.frame;
    float height = [self getHeight:itemDict];
    lineRect.origin.y = height - lineRect.size.height;
    self.lineView.frame = lineRect;
}

- (UIButton*)generateBtn {
    UIButton* btn = [UIButton buttonWithType:UIButtonTypeCustom];
    [btn addTarget:self action:@selector(btnPressed:) forControlEvents:UIControlEventTouchUpInside];
    btn.titleLabel.font = BTN_FONT;
    btn.layer.masksToBounds = YES;
    btn.layer.borderWidth = 1.;
    btn.layer.cornerRadius = DISCOUNT_CELL_CORNER_RADIUS;
    return btn;
}
+ (void)setBtn:(UIButton*)btn Hover:(BOOL)f {
    if (f) {
        [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        btn.backgroundColor = COLOR_PINK;
        btn.layer.borderColor = [UIColor clearColor].CGColor;
    } else {
        [btn setTitleColor:COLOR_GRAY forState:UIControlStateNormal];
        btn.backgroundColor = [UIColor whiteColor];
        btn.layer.borderColor = COLOR_GRAY.CGColor;
    }
}
- (void)btnPressed:(UIButton*)btn {
    self.currentSelectIndex = [self.btnArray indexOfObject:btn];
    for (int i = 0; i < self.btnArray.count; i++) {
        UIButton* btn = self.btnArray[i];
        [QSDiscountTaobaoInfoCell setBtn:btn Hover:i == self.currentSelectIndex];
    }
}


- (CGFloat)getHeight:(NSDictionary *)itemDict {
    NSString* str = [QSItemUtil getSkuProperties:itemDict][self.infoIndex];
    NSArray* comps = [str componentsSeparatedByString:@":"];
    NSString* title = comps[0];
    NSArray* compInfos = [comps subarrayWithRange:NSMakeRange(1, comps.count - 1)];
    float baseY = 0;
    if (title && title.length) {
        baseY = ORIGIN_Y + LABEL_HEIGHT + ORIGIN_Y;
    } else {
        baseY = ORIGIN_Y;
    }
    float baseX = 0;
    
    float cellWidth = DISCOUNT_CELL_WIDTH;
    
    CGSize oneSize = CGSizeZero;
    for (NSString* comp in compInfos) {
        CGSize size = [self getBtnSize:comp];
        
        if (baseX + size.width > cellWidth) {
            baseX = 0;
            baseY += size.height + DELTA_Y;
        }
        
        baseX += size.width + DELTA_X;
        
        oneSize = size;
    }
    return baseY + oneSize.height + ORIGIN_Y;
}

- (CGSize)getBtnSize:(NSString*)str {
    CGSize size = [QSLayoutUtil sizeForString:str withMaxWidth:INFINITY height:INFINITY font:BTN_FONT];
    size.width += 2 * BTN_PADDING_X;
    size.height += 2 * BTN_PADDING_Y;
    if (size.width < 35.f) {
        size.width = 35.f;
    }
    return size;
}
@end
