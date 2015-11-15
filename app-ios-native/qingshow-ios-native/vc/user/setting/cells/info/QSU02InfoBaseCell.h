//
//  QSU02InfoBaseCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/5/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU02AbstractTableViewCell.h"

typedef NS_ENUM(NSInteger, U02SectionInfoRow) {
    U02SectionInfoRowName = 0,
    U02SectionInfoRowAge,
    U02SectionInfoRowHeight,
    U02SectionInfoRowWeight,
    U02SectionInfoRowBust,
    U02SectionInfoRowShouler,
    U02SectionInfoRowWaist,
    U02SectionInfoRowHips,
    U02SectionInfoRowBodyType,
    U02SectionInfoRowDressStyle,
    U02SectionInfoRowExpectation
};

NSString* u02InfoTypeToTitle(U02SectionInfoRow row);

@interface QSU02InfoBaseCell : QSU02AbstractTableViewCell

+ (QSU02AbstractTableViewCell*)generateCellWithRowType:(NSInteger)rowType;

@end
