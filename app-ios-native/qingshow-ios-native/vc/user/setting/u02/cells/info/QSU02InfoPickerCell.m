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
{
@private NSMutableArray *_dressStyleArray;
    NSMutableArray *_bodyStyleArray;
}

- (void)awakeFromNib {
    // Initialization code
    _chooseStylePickerView.delegate = self;
    _chooseStylePickerView.dataSource = self;
    _chooseStylePickerView.hidden = YES;
    _chooseStylePickerView.userInteractionEnabled = YES;
    [_styleBtn setTintColor:[UIColor grayColor]];
    [_styleBtn setTitleEdgeInsets:UIEdgeInsetsZero];
    _bodyStyleArray = [NSMutableArray arrayWithObjects:@"A型",@"H型",@"V型",@"X型", nil];
    _dressStyleArray = [NSMutableArray arrayWithObjects:@"日韩系",@"欧美系",nil];
}
- (void)bindWithUser:(NSDictionary *)peopleDict {
    _typeLabel.text = u02InfoTypeToTitle(self.rowType);
    switch (self.rowType) {
        case U02SectionInfoRowBodyType: {
            [_styleBtn setTitle:[QSPeopleUtil getBodyTypeDesc:peopleDict] forState:UIControlStateNormal];
            break;
        }
        case U02SectionInfoRowDressStyle: {
            [_styleBtn setTitle:[QSPeopleUtil getDressStyleDesc:peopleDict] forState:UIControlStateNormal];
            break;
        }
        default:
            break;
    }
}

#pragma mark - clickBlack  then  pickerView hidden
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    _chooseStylePickerView.hidden = YES;
}

#pragma mark - PickerViewDelegate
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    if (self.rowType == U02SectionInfoRowBodyType) {
        return _bodyStyleArray.count;
    } else if (self.rowType == U02SectionInfoRowDressStyle) {
        return _dressStyleArray.count;
    } else {
        return 0;
    }
}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    if (self.rowType == U02SectionInfoRowBodyType) {
        NSString *str = nil;
        str = _bodyStyleArray[row];
        return str;
    }
    else if (self.rowType == U02SectionInfoRowDressStyle)
    {
        NSString *str = [NSString string];
        str = _dressStyleArray[row];
        return str;
    } else {
        return nil;
    }
}
- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
#warning expected upload data to Server and refresh tableview; maybe need a delegate or block
}
- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (IBAction)changeStyleBtnPressed:(id)sender {
    if (_chooseStylePickerView.hidden == YES) {
        _chooseStylePickerView.hidden = NO;
    }
    
    
}
@end
