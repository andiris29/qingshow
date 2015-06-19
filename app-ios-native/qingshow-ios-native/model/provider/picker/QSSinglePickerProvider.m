//
//  QSSinglePickerProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/7/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSSinglePickerProvider.h"

@interface QSSinglePickerProvider ()
@property (weak, nonatomic) UIPickerView* picker;


@end

@implementation QSSinglePickerProvider

- (instancetype)initWithDataArray:(NSArray*)dataArray
{
    self = [super init];
    if (self) {
        self.dataArray = dataArray;
    }
    return self;
}
- (void)bindPicker:(UIPickerView*)picker{
    self.picker = picker;
    picker.dataSource = self;
    picker.delegate = self;
    [picker reloadAllComponents];
}
- (void)selectData:(NSString*)data {
    int index = [self.dataArray indexOfObject:data];
    if (index == NSNotFound) {
        return;
    } else {
        [self.picker selectRow:index inComponent:0 animated:NO];
    }
}

#pragma mark - UIPicker DataSource
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return self.dataArray.count;
}

#pragma mark - UIPicker Delegate
- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    return self.dataArray[row];
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    if ([self.delegate respondsToSelector:@selector(provider:didSelectRow:value:)]) {
        [self.delegate provider:self didSelectRow:row value:self.dataArray[row]];
    }
}

@end
