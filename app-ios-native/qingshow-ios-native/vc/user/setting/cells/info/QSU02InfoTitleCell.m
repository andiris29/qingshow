//
//  QSU02InfoTitleCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/5/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU02InfoTitleCell.h"
#import "QSPeopleUtil.h"
@interface QSU02InfoTitleCell()

@property (strong, nonatomic) UILabel* label;

@end

@implementation QSU02InfoTitleCell

- (instancetype)init {
    self = [super init];
    if (self) {
        UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(100, 0, 200, 40)];
        label.font = NEWFONT;
        label.textColor = [UIColor grayColor];
        self.label = label;
        [self addSubview:label];
    }
    return self;
}

- (void)bindWithUser:(NSDictionary *)dict{
    self.textLabel.text = u02InfoTypeToTitle(self.rowType);
    if (self.rowType == U02SectionInfoRowExpectation) {
        self.textLabel.font = NEWFONT;
        self.label.text = [QSPeopleUtil getExpectationsDesc:dict];
    }

}
- (BOOL)cellDidClicked {
    if (self.rowType == U02SectionInfoRowExpectation) {
        [self.delegate showExpectationVc];
    }
    return YES;
}

@end
