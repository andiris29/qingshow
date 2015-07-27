//
//  QSLocationPickerProvider.h
//  qingshow-ios-native
//
//  Created by wxy325 on 4/4/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@class QSLocationPickerProvider;

@protocol QSLocationPickerProviderDelegate <NSObject>

@optional
- (void)locationValueChange:(QSLocationPickerProvider*)provider;

@end

@interface QSLocationPickerProvider : NSObject <UIPickerViewDataSource, UIPickerViewDelegate>

- (id)initWithPicker:(UIPickerView*)picker;

- (void)bindWithValue:(NSString*)value;
- (NSString*)getSelectedValue;

@property (weak, nonatomic) NSObject<QSLocationPickerProviderDelegate>* delegate;

@end
