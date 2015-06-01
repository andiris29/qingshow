//
//  QSLocationPickerProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 4/4/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSLocationPickerProvider.h"
#import <CoreText/CoreText.h>
@interface QSLocationPickerProvider ()

@property (weak, nonatomic) UIPickerView* picker;

@property (strong, nonatomic) NSDictionary* provinceDict;
@property (strong, nonatomic) NSArray* provinceCodeArray;

@property (strong, nonatomic) NSDictionary* cityDict;
@property (strong, nonatomic) NSArray* cityArray;

@property (strong, nonatomic) NSDictionary* districtDict;
@property (strong, nonatomic) NSArray* districtArray;
@end

@implementation QSLocationPickerProvider

- (id)initWithPicker:(UIPickerView *)picker
{
    self = [super init];
    if (self) {
        self.picker = picker;
        picker.dataSource = self;
        picker.delegate = self;
        
        NSData* locationData = [[NSData alloc] initWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"area" ofType:@"json"]];
        NSError* err = nil;
        NSDictionary* dict = [NSJSONSerialization JSONObjectWithData:locationData options:0 error:&err];
        
        self.provinceDict = dict[@"area0"];
        
        self.cityDict = dict[@"area1"];
        
        self.districtDict = dict[@"area2"];
        
        self.provinceCodeArray = [self.provinceDict.allKeys sortedArrayUsingComparator:^NSComparisonResult(NSString* obj1, NSString* obj2) {
            return obj1.integerValue - obj2.integerValue;
        }];
        [self updateCityAndDistrictDatasource];
    }
    return self;
}

#pragma mark - UIPickerViewDataSource
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 3;
}

// returns the # of rows in each component..
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    switch (component) {
        case 0:
            return self.provinceCodeArray.count;
            break;
        case 1:
            return self.cityArray.count;
            break;
        case 2:
            return self.districtArray.count;
            break;
    }
    return 0;
}

- (void)bindWithValue:(NSString*)value
{
    NSArray* comps = [value componentsSeparatedByString:@" "];
    for (int i = 0; i < comps.count; i++) {
        NSString* comp = comps[i];
        switch (i) {
            case 0:
            {
                [self bindProvince:comp];
                break;
            }
            case 1:
            {
                [self bindCity:comp];
                break;
            }
            case 2:
            {
                [self bindDistrict:comp];
                break;
            }
        }
    }
}

- (void)bindProvince:(NSString*)province
{
    int i = 0;
    for (i = 0; i < self.provinceCodeArray.count; i++) {
        NSString* code = self.provinceCodeArray[i];
        NSString* prov = self.provinceDict[code];
        if ([province isEqualToString:prov]) {
            break;
        }
    }
    if (i >= self.provinceCodeArray.count) {
        i = 0;
    }
    [self.picker selectRow:i inComponent:0 animated:NO];
    [self updateCityAndDistrictDatasource];
}
- (void)bindCity:(NSString*)city
{
    int i = 0;
    for (i = 0; i < self.cityArray.count; i++) {
        NSArray* cityInfo = self.cityArray[i];
        NSString* cityName = cityInfo[0];
        if ([cityName isEqualToString:city]) {
            break;
        }
    }
    if (i >= self.cityArray.count){
        i = 0;
    }
    [self.picker selectRow:i inComponent:1 animated:NO];
    [self updateDistrictDatasource];
}
- (void)bindDistrict:(NSString*)dis{
    int i = 0;
    for (i = 0; i < self.districtArray.count; i++) {
        NSArray* disInfo = self.districtArray[i];
        NSString* disName = disInfo[0];
        if ([disName isEqualToString:dis]) {
            break;
        }
    }
    if (i >= self.districtArray.count){
        i = 0;
    }
    [self.picker selectRow:i inComponent:2 animated:NO];
}

- (NSString*)getSelectedValue
{
    NSMutableString* str = [@"" mutableCopy];
    for (int i = 0; i < 3; i++) {
        int row = [self.picker selectedRowInComponent:i];
        NSString* v = [self valueForRow:row forComponent:i];
        [str appendString:v];
        [str appendString:@" "];
    }
    return str;
}

- (NSString*)valueForRow:(NSInteger)row forComponent:(NSInteger)component
{
    NSString* str = nil;
    switch (component) {
        case 0:{
            NSString* code = self.provinceCodeArray[row];
            str = self.provinceDict[code];
            break;
        }
        case 1:{
            str = self.cityArray[row][0];
            break;
        }
        case 2:{
            str = self.districtArray[row][0];
            break;
        }
        default:
            str = @"";
            break;
    }
    return str;
}

- (NSString*)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    NSString* str = [self valueForRow:row forComponent:component];
    return str;
}

- (CGFloat)pickerView:(UIPickerView *)pickerView rowHeightForComponent:(NSInteger)component
{
    return 20.f;
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    if (component == 0) {
        [self updateCityAndDistrictDatasource];
    } else if (component == 1) {
        [self updateDistrictDatasource];
    }
    
    if ([self.delegate respondsToSelector:@selector(locationValueChange:)]) {
        [self.delegate locationValueChange:self];
    }

}

- (void)updateCityAndDistrictDatasource
{
    NSString* provinceCode = self.provinceCodeArray[[self.picker selectedRowInComponent:0]];
    
    self.cityArray = self.cityDict[provinceCode];
    [self.picker selectRow:0 inComponent:1 animated:NO];
    [self.picker reloadComponent:1];
    
    [self updateDistrictDatasource];
}

- (void)updateDistrictDatasource
{
    NSArray* selectedCity = self.cityArray[[self.picker selectedRowInComponent:1]];
    NSString* cityCode = selectedCity[1];
    
    self.districtArray = self.districtDict[cityCode];
    [self.picker selectRow:0 inComponent:2 animated:NO];
    [self.picker reloadComponent:2];
}

//- (CGFloat)pickerView:(UIPickerView *)pickerView widthForComponent:(NSInteger)component;
@end
