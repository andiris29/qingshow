//
//  QSNumberPageControl.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/17/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSNumberPageControl.h"
#import <QuartzCore/QuartzCore.h>

@interface QSNumberPageControl ()

@property (strong, nonatomic) UILabel* numberLabel;

@end

@implementation QSNumberPageControl
- (void)setCurrentPage:(int)currentPage
{
    _currentPage = currentPage;
    [self updateText];
}

- (void)setNumberOfPages:(int)numberOfPages
{
    _numberOfPages = numberOfPages;
    [self updateText];
}

#pragma mark - Init
- (instancetype)init
{
    self = [super initWithFrame:CGRectMake(0, 0, 53, 20)];
    if (self) {
        self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.5];
        self.layer.masksToBounds = YES;
        self.layer.cornerRadius = 10.f;
        _currentPage = 0;
        _numberOfPages = 0;
        self.numberLabel = [[UILabel alloc] initWithFrame:self.bounds];
        self.numberLabel.textColor = [UIColor whiteColor];
        self.numberLabel.textAlignment = NSTextAlignmentCenter;
        self.numberLabel.font = [UIFont fontWithName:@"FZLanTingHeiS-EL-GB" size:11];
        [self addSubview:self.numberLabel];
        
        [self updateText];
    }
    return self;
}

- (void)updateText
{
    self.numberLabel.text = [NSString stringWithFormat:@"%d/%d", (self.currentPage + 1), self.numberOfPages];
}

@end
