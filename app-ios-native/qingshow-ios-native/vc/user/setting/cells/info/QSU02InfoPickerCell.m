//
//  QSUserSettingPickerCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU02InfoPickerCell.h"
#import "QSPeopleUtil.h"

@implementation QSU02InfoPickerCell

- (void)awakeFromNib {
    // Initialization code
    [self.valueLabel setTextColor:[UIColor grayColor]];
}
- (void)bindWithUser:(NSDictionary *)peopleDict {
    _typeLabel.text = u02InfoTypeToTitle(self.rowType);
    switch (self.rowType) {
        case U02SectionInfoRowBodyType: {
            self.valueLabel.text = [QSPeopleUtil getBodyTypeDesc:peopleDict];
            break;
        }
        case U02SectionInfoRowDressStyle: {
            self.valueLabel.text = [QSPeopleUtil getDressStyleDesc:peopleDict];
            break;
        }
        default:
            break;
    }
}




- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
- (BOOL)cellDidClicked {
    [self.delegate showPickerWithType:self.rowType];
    return YES;
}
@end
