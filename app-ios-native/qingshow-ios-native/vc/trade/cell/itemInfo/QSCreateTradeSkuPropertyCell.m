//
//  QSCreateTradeSkuPropertyCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 8/1/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeSkuPropertyCell.h"
#import "UINib+QSExtension.h"

@implementation QSCreateTradeSkuPropertyCell

+ (QSCreateTradeSkuPropertyCell*)generateCell {
    return [UINib generateViewWithNibName:@"QSCreateTradeSkuPropertyCell"];
}

- (void)bindWithDict:(NSDictionary *)dict {
    
}

- (void)bindSkuProperty:(NSString*)skuProperty {
    NSArray* comps = [skuProperty componentsSeparatedByString:@":"];
    if (comps.count > 0) {
        self.leftLabel.text = comps[0];
    } else {
        self.leftLabel.text = @"";
    }
    if (comps.count > 1) {
        self.rightLabel.text = comps[1];
    } else {
        self.rightLabel.text = @"";
    }
}

@end
