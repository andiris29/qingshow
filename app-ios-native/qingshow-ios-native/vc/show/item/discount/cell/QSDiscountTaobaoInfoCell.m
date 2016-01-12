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
#import "NSArray+QSExtension.h"
#import "NSDictionary+QSExtension.h"
#import "UIView+QSExtension.h"

#define DELTA_X 8.f
#define DELTA_Y 5.f

#define BTN_ORIGIN_X 75.f
#define BTN_ORIGIN_Y 15.f
#define BTN_PADDING_X 5.f
#define BTN_PADDING_Y 5.f

#define ORIGIN_Y 20.f

#define LABEL_BTN_PADDING_Y 0.f
#define CELL_BOTTOM_PADDING_Y 0.f

#define LABEL_HEIGHT 13.f
#define BTN_FONT [UIFont fontWithName:@"FZLanTingHeiS-EL-GB" size:12]


#define COLOR_GRAY [UIColor colorWithRed:112.f/255.f green:112.f/255.f blue:112.f/255.f alpha:1.f]
#define COLOR_LIGHT_GRAY [UIColor colorWithRed:200.f/255.f green:200.f/255.f blue:200.f/255.f alpha:1.f]
#define COLOR_PURPLE [UIColor colorWithRed:40.f/255.f green:45.f/255.f blue:91.f/255.f alpha:1.f]

@interface QSDiscountTaobaoInfoCell ()


@property (strong, nonatomic) NSArray* compInfos;

@property (assign, nonatomic) NSInteger currentSelectIndex;
@property (strong, nonatomic) NSDictionary* itemDict;
@end

@implementation QSDiscountTaobaoInfoCell
@synthesize delegate = _delegate;
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

- (NSString*)getSelectedValue {
    if (self.currentSelectIndex >= 0 && self.currentSelectIndex < self.compInfos.count) {
        return self.compInfos[self.currentSelectIndex];
    } else {
        return nil;
    }
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
    
    
    NSArray* array = [QSItemUtil getSkuProperties:itemDict];
    NSString* str = nil;
    if (array.count > self.infoIndex) {
        str = array[self.infoIndex];
    }
    NSArray* comps = [str componentsSeparatedByString:@":"];
    self.title = comps[0];
    self.compInfos = [comps subarrayWithRange:NSMakeRange(1, comps.count - 1)];
    
    
    float baseY = 0;
    if (self.title && self.title.length) {
        self.titleLabel.hidden = NO;
        self.titleLabel.text = self.title;
    } else {
        self.titleLabel.hidden = YES;

    }
    baseY = BTN_ORIGIN_Y;
    
    NSMutableArray* btnArray = [@[] mutableCopy];
    
    float baseX = BTN_ORIGIN_X;

    float cellWidth = DISCOUNT_CELL_WIDTH;
    
    for (NSString* comp in self.compInfos) {
        UIButton* btn = [self generateBtn];
        [btn setTitle:comp forState:UIControlStateNormal];
        [QSDiscountTaobaoInfoCell setBtn:btn Hover:NO];
        
        CGSize size = [self getBtnSize:comp];
        
        if (baseX + size.width > cellWidth) {
            baseX = BTN_ORIGIN_X;
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
    
    [self updateBtnStateWithItem:itemDict selectProps:@[]];
}

- (UIButton*)generateBtn {
    UIButton* btn = [UIButton buttonWithType:UIButtonTypeCustom];
    [btn addTarget:self action:@selector(btnPressed:) forControlEvents:UIControlEventTouchUpInside];
    btn.titleLabel.font = BTN_FONT;
    [btn configBorderColor:COLOR_GRAY width:1.f cornerRadius:DISCOUNT_CELL_CORNER_RADIUS];
    
    return btn;
}
+ (void)setBtn:(UIButton*)btn Hover:(BOOL)f {
    btn.userInteractionEnabled = true;
    if (f) {
        [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        btn.backgroundColor = COLOR_PURPLE;
        btn.layer.borderColor = [UIColor clearColor].CGColor;
    } else {
        [btn setTitleColor:COLOR_GRAY forState:UIControlStateNormal];
        btn.backgroundColor = [UIColor whiteColor];
        btn.layer.borderColor = COLOR_GRAY.CGColor;
    }
}
+ (void)setBtn:(UIButton*)btn disable:(BOOL)fDisable {
    btn.userInteractionEnabled = !fDisable;
    if (fDisable) {
        [btn setTitleColor:COLOR_GRAY forState:UIControlStateNormal];
        btn.backgroundColor = COLOR_LIGHT_GRAY;
        btn.layer.borderColor = COLOR_GRAY.CGColor;
    } else {
        [btn setTitleColor:COLOR_GRAY forState:UIControlStateNormal];
        btn.backgroundColor = [UIColor whiteColor];
        btn.layer.borderColor = COLOR_GRAY.CGColor;
    }
}

- (void)btnPressed:(UIButton*)btn {
    NSInteger index = [self.btnArray indexOfObject:btn];
    self.currentSelectIndex = index == self.currentSelectIndex ? -1 : index;
    for (int i = 0; i < self.btnArray.count; i++) {
        UIButton* btn = self.btnArray[i];
        [QSDiscountTaobaoInfoCell setBtn:btn Hover:i == self.currentSelectIndex];
    }
    if ([self.delegate respondsToSelector:@selector(disCountBtnPressed:btnIndex:)]) {
        [self.delegate disCountBtnPressed:self.btnArray btnIndex:self.currentSelectIndex];
    }
}


- (CGFloat)getHeight:(NSDictionary *)itemDict {
    NSArray* array = [QSItemUtil getSkuProperties:itemDict];
    NSString* str = @"";
    if (self.infoIndex < array.count) {
        str = array[self.infoIndex];
    }

    NSArray* comps = [str componentsSeparatedByString:@":"];
    NSArray* compInfos = [comps subarrayWithRange:NSMakeRange(1, comps.count - 1)];
    CGFloat baseY = BTN_ORIGIN_Y;
    float baseX = BTN_ORIGIN_X;
    
    float cellWidth = DISCOUNT_CELL_WIDTH;
    
    CGSize oneSize = CGSizeZero;
    for (NSString* comp in compInfos) {
        CGSize size = [self getBtnSize:comp];
        
        if (baseX + size.width > cellWidth) {
            baseX = BTN_ORIGIN_X;
            baseY += size.height + DELTA_Y;
        }
        
        baseX += size.width + DELTA_X;
        
        oneSize = size;
    }
    return baseY + oneSize.height + CELL_BOTTOM_PADDING_Y;
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

- (void)updateBtnStateWithItem:(NSDictionary*)itemDict selectProps:(NSArray*)props {
    NSString* v = [self getSelectedValue];
#warning Move to Util
    v = [v stringByReplacingOccurrencesOfString:@"." withString:@""];
    props = [props filteredArrayUsingBlock:^BOOL(NSString* s) {
        return ![v isEqualToString:s];
    }];
    NSDictionary* skuTable = [QSItemUtil getSkuTable:itemDict];
    
    
    for (int i = 0; i < self.btnArray.count; i++) {
        UIButton* btn = self.btnArray[i];
        if (i == self.currentSelectIndex) {
            [QSDiscountTaobaoInfoCell setBtn:btn Hover:true];
        } else {
            NSString* comp = self.compInfos[i];
            comp = [comp stringByReplacingOccurrencesOfString:@"." withString:@""];
            NSMutableArray* curProps = [props mutableCopy];
            [curProps addObject:comp];
            NSArray* retArray = [QSItemUtil getMatchSkuKeysForItem:itemDict skuKeys:curProps];
            if (!retArray.count) {
                //没sku信息，不存在商品
                [QSDiscountTaobaoInfoCell setBtn:btn disable:true];
            } else {
                if (retArray.count > 1) {
                    int sum = 0;
                    for (NSString* skuKey in retArray) {
                        
                        NSString* v = [skuTable stringValueForKeyPath:skuKey];
                        NSArray* c = [v componentsSeparatedByString:@":"];
                        if (c.count) {
                            NSString* quantityStr = [c firstObject];
                            int n = [quantityStr intValue];
                            sum += n;
                        }
                    }
                    [QSDiscountTaobaoInfoCell setBtn:btn disable:sum == 0];
                } else {
                    NSString* skuKey = retArray[0];
                    NSString* v = [skuTable stringValueForKeyPath:skuKey];
                    
                    NSArray* c = [v componentsSeparatedByString:@":"];
                    if (c.count) {
                        NSString* quantityStr = [c firstObject];
                        int n = [quantityStr intValue];
                        if (n) {
                            [QSDiscountTaobaoInfoCell setBtn:btn disable:false];
                        } else {
                            [QSDiscountTaobaoInfoCell setBtn:btn disable:true];
                        }
                    } else {
                        //信息不对,disable
                        [QSDiscountTaobaoInfoCell setBtn:btn disable:true];
                    }
                }
            }
            
        }
    }
}
@end
