//
//  QSBadgeBtnGroup.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/19/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSBadgeBtnGroup.h"


@interface QSBadgeBtnGroup ()

@property (strong, nonatomic) NSArray* types;
@property (strong, nonatomic) NSArray* buttons;

@end

@implementation QSBadgeBtnGroup

#pragma mark - Init
- (instancetype)initWithTypes:(NSArray*)array {
    self = [super init];
    if (self) {
        self.types = array;
        NSMutableArray* btnArray = [@[] mutableCopy];
        for (NSNumber* type in array) {
            QSBadgeButton* btn = [QSBadgeButton generateBtnWithType:type.integerValue];
            [btn addTarget:self action:@selector(didClickBtn:) forControlEvents:UIControlEventTouchUpInside];
            
            [self addSubview:btn];
            [btnArray addObject:btn];
        }
        self.buttons = btnArray;
        self.userInteractionEnabled = YES;
    }
    
    return self;
}

#pragma mark - Life Cycle
- (void)layoutSubviews {
    [super layoutSubviews];
    if (self.buttons.count) {
        CGRect selfFrame = self.frame;
        CGFloat y = selfFrame.size.height / 2;
        float btnWidth = 35.f;
        float leftBorder = 15.f;
        CGFloat x = (selfFrame.size.width - 2 * leftBorder - self.buttons.count * btnWidth) / (self.buttons.count - 1);
        for (NSUInteger i = 0; i < self.buttons.count; i++) {
            QSBadgeButton* btn = self.buttons[i];
            btn.center = CGPointMake( (x + btnWidth) * i + btnWidth / 2 + leftBorder, y);
            btn.bounds = CGRectMake(0, 0, 35, 52);
        }
    }
    

}

- (void)didClickBtn:(QSBadgeButton*)btn {
    if (btn.hover) {
        return;
    }
    for (NSUInteger i = 0; i < self.buttons.count; i++) {
        QSBadgeButton* b = self.buttons[i];
        b.hover = b == btn;
    }
    
    if ([self.delegate respondsToSelector:@selector(btnGroup:didSelectType:)]) {
        [self.delegate btnGroup:self didSelectType:btn.type];
    }
    
}


- (void)triggerSelectType:(QSBadgeButtonType)type {
    QSBadgeButton* btn = [self findBtnOfType:type];;
    [self didClickBtn:btn];

}


- (QSBadgeButton*)findBtnOfType:(QSBadgeButtonType)type {
    NSUInteger index = 0;
    for (index = 0; index < self.types.count; index++) {
        NSNumber* n = self.types[index];
        if (n.integerValue == type) {
            break;
        }
    }
    if (index != self.types.count) {
        QSBadgeButton* btn = self.buttons[index];
        return btn;
    }
    return nil;
}

@end
