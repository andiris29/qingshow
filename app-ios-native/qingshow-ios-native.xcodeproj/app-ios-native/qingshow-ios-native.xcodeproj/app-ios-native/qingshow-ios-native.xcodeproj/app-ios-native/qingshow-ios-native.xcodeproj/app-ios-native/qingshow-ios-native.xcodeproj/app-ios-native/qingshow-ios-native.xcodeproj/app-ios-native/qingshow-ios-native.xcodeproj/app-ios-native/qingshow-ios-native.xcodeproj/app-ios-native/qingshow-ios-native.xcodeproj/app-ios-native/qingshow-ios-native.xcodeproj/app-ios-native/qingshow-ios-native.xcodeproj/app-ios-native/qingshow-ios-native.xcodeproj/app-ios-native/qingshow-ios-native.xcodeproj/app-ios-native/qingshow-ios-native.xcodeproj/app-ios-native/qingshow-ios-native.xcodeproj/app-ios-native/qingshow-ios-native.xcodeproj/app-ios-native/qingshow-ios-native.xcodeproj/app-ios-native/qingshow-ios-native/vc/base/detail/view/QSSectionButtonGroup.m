//
//  QSSectionButtonGroup.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSSectionButtonGroup.h"

@interface QSSectionButtonGroup ()
- (void)configView;

@property (assign, nonatomic) QSSectionButtonGroupType type;
@property (strong, nonatomic) NSMutableArray* splitterArray;
@property (assign, nonatomic) int btnCount;
@end

@implementation QSSectionButtonGroup

#pragma mark - Init Method
- (id)init
{
    return [self initWithType:QSSectionButtonGroupTypeImage];
}

- (id)initWithType:(QSSectionButtonGroupType)type
{
#warning only U01 exist
    if (type == QSSectionButtonGroupTypeBrand) {
        self = [self initWithType:type btnCount:4];
    } else if (type == QSSectionButtonGroupTypeU01) {
        self = [self initWithType:type btnCount:2];
    }
    else {
        self = [self initWithType:type btnCount:3];
    }

    if (self) {
        
    }
    return self;

}
- (id)initWithType:(QSSectionButtonGroupType)type btnCount:(int)count
{
    self = [self initWithFrame:CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, 45)];
    if (self) {
        self.type = type;
        self.btnCount = count;
        [self configView];
        [self updateLayout];
    }
    return self;
}

#pragma mark - Layout
- (void)configView
{
    NSMutableArray* a = [@[] mutableCopy];
    for (int i = 0; i < self.btnCount; i++) {
        if (self.type == QSSectionButtonGroupTypeU01) {
            [a addObject:[QSSectionTextButton generateView]];
        } else {
            [a addObject:[QSSectionNumberTextButton generateView]];
        }

    }
    self.buttonGroup = a;
    
    if (self.type == QSSectionButtonGroupTypeImage || self.type == QSSectionButtonGroupTypeBrand) {
        self.singleButton = [QSSectionFollowButton generateView];
    } else  if (self.type == QSSectionButtonGroupTypeText){
        self.singleButton = [QSSectionTextButton generateView];
    }

    for (QSSectionButtonBase* btn in self.buttonGroup){
        UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(groupButtonPressed:)];
        [btn addGestureRecognizer:ges];
        [self addSubview:btn];
    }
    if (self.singleButton) {
        UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(singleButtonPressed:)];
        [self.singleButton addGestureRecognizer:ges];
        [self addSubview:self.singleButton];
    }
    self.splitterArray = [@[] mutableCopy];
    for (int i = 0; i < self.btnCount; i++) {
        UIView* splitter = [[UIView alloc] init];
        splitter.backgroundColor = [UIColor whiteColor];
        [self addSubview:splitter];
        [self.splitterArray addObject:splitter];
    }

}
- (void)layoutSubviews
{
    [super layoutSubviews];
    [self updateLayout];
}
- (void)updateLayout
{
    float singleBtnWidthRate = 1.5f;
    if (self.buttonGroup.count == 3) {
        singleBtnWidthRate = 1.2f;
    }
    CGSize size = self.frame.size;
    
    float width = 0;
    UIView* lastSplitter  = [self.splitterArray lastObject];
    [lastSplitter removeFromSuperview];
    if (self.singleButton) {
        width = size.width / (self.buttonGroup.count + singleBtnWidthRate);  //single button占singleBtnWidthRate倍宽度
        [self addSubview:lastSplitter];
    } else {
        width = size.width / self.buttonGroup.count;
    }

    float height = size.height;
    for (int i = 0; i < self.buttonGroup.count; i++) {
        QSSectionButtonBase* btn = self.buttonGroup[i];
        btn.frame = CGRectMake(i * width, 0, width, height);
        UIView* splitter = self.splitterArray[i];
        splitter.frame = CGRectMake((i + 1) * width - 1,0, 1, height);
    }
    self.singleButton.frame = CGRectMake(self.buttonGroup.count * width, 0, width * singleBtnWidthRate, height);
}
#pragma mark - Gesture
- (void)groupButtonPressed:(UIGestureRecognizer*)ges
{
    int i = 0;
    int index = 0;
    for (QSSectionButtonBase* btn in self.buttonGroup)
    {
        btn.selected = ges.view == btn;
        if (btn.selected) {
            index = i;
        }
        if (btn.selected && [self.delegate respondsToSelector:@selector(groupButtonPressed:)]) {
            [self.delegate groupButtonPressed:i];
        }
        ++i;
    }
    [self setSelect:index];
}

- (void)singleButtonPressed:(UIGestureRecognizer*)ges
{
    if ([self.delegate respondsToSelector:@selector(singleButtonPressed)])
    {
        [self.delegate singleButtonPressed];
    }
}
#pragma mark - Config Text
- (void)setTitle:(NSString*)title atIndex:(int)index
{
    if (index < self.buttonGroup.count) {
        QSSectionButtonBase* btn = self.buttonGroup[index];
        btn.textLabel.text = title;
    }
    else
    {
        self.singleButton.textLabel.text = title;
    }
}

- (void)setNumber:(NSString*)numberStr atIndex:(int)index
{
    QSSectionNumberTextButton* btn = self.buttonGroup[index];
    if ([btn respondsToSelector:@selector(numberLabel)]) {
        btn.numberLabel.text = numberStr;
    }

}
- (void)setSelect:(int)index
{
    for (int i = 0; i < self.buttonGroup.count; i++) {
        QSSectionButtonBase* btn = self.buttonGroup[i];
        btn.selected = i == index;
    }
//    for (int i = 0; i < self.splitterArray.count; i++) {
//        UIView* splitter = self.splitterArray[i];
//        splitter.hidden = i == index || i == index - 1;
//    }
}
@end
