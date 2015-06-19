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
        CGFloat x = selfFrame.size.width / (self.buttons.count + 1);
        for (NSUInteger i = 0; i < self.buttons.count; i++) {
            QSBadgeButton* btn = self.buttons[i];
            btn.center = CGPointMake( x * (i + 1), y);
            btn.bounds = CGRectMake(0, 0, 35, 52);
        }
    }
    

}

- (void)didClickBtn:(QSBadgeButton*)btn {
    for (NSUInteger i = 0; i < self.buttons.count; i++) {
        QSBadgeButton* b = self.buttons[i];
        b.hover = b == btn;
    }
    
    if ([self.delegate respondsToSelector:@selector(btnGroup:didSelectType:)]) {
        [self.delegate btnGroup:self didSelectType:btn.type];
    }
    
}


@end
