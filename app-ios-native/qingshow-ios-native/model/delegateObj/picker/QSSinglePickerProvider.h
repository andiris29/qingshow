//
//  QSSinglePickerProvider.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/7/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@class QSSinglePickerProvider;

@protocol QSSinglePickerProviderDelegate <NSObject>

- (void)provider:(QSSinglePickerProvider*)provider didSelectRow:(int)row value:(NSString*)value;

@end


@interface QSSinglePickerProvider : NSObject <UIPickerViewDataSource, UIPickerViewDelegate>
- (instancetype)initWithDataArray:(NSArray*)dataArray;
- (void)bindPicker:(UIPickerView*)picker;

@property (strong, nonatomic) NSArray* dataArray;
@property (weak, nonatomic) NSObject<QSSinglePickerProviderDelegate>* delegate;
@end
