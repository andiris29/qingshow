//
//  QSU02InfoBaseCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/5/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU02InfoBaseCell.h"
#import "QSU02InfoTitleCell.h"
#import "UINib+QSExtension.h"
NSString* u02InfoTypeToTitle(U02SectionInfoRow row) {
    
    return @[@"昵称",
             @"年龄",
             @"身高",
             @"体重",
             @"胸围",
             @"肩宽",
             @"腰围",
             @"臀围",
             @"体型",
             @"穿衣风格",
             @"搭配效果"
             ][row];
}

@implementation QSU02InfoBaseCell
+ (QSU02AbstractTableViewCell*)generateCellWithRowType:(NSInteger)rowType
{
    switch (rowType) {
        case U02SectionInfoRowName:
        case U02SectionInfoRowAge:
        case U02SectionInfoRowHeight:
        case U02SectionInfoRowWeight:
        case U02SectionInfoRowBust:
        case U02SectionInfoRowShouler:
        case U02SectionInfoRowWaist:
        case U02SectionInfoRowHips:{
            return [UINib generateViewWithNibName:@"QSU02InfoTextCell"];
            break;
        }
        case U02SectionInfoRowBodyType:
        case U02SectionInfoRowDressStyle: {
            return [UINib generateViewWithNibName:@"QSU02InfoPickerCell"];
        }
        case U02SectionInfoRowExpectation: {
            return [[QSU02InfoTitleCell alloc] init];
        }
        default: {
            return nil;
        }
    }
}
@end
