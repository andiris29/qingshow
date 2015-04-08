//
//  QSLocationPickerProvider.h
//  qingshow-ios-native
//
//  Created by wxy325 on 4/4/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSLocationPickerProvider : NSObject <UIPickerViewDataSource, UIPickerViewDelegate>

- (id)initWithPicker:(UIPickerView*)picker;

@end
