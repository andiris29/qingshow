//
//  QSU02AbstractTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/5/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU02AbstractTableViewCell.h"
#import "QSU02ImgCell.h"
#import "QSU02InfoBaseCell.h"
#import "QSU02ManagerCell.h"
#import "QSU02OtherCell.h"

NSString* u02SectionToTitle(U02Section sec){
    return @[@"选择图片", @"管理", @"基本信息", @"其他"][sec];
}

@implementation QSU02AbstractTableViewCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
- (void)bindWithUser:(NSDictionary*)userDict {
    //Method to be Override
    
}

+ (QSU02AbstractTableViewCell*)generateCellWithSectionType:(U02Section)sectionType rowType:(NSInteger)rowType
{
    QSU02AbstractTableViewCell* cell = nil;
    switch (sectionType) {
        case U02SectionImage:
        {
            cell = [QSU02ImgCell generateCellWithRowType:rowType];
            break;
        }
        case U02SectionInfo: {
            cell = [QSU02InfoBaseCell generateCellWithRowType:rowType];
            break;
        }
        case U02SectionManager: {
            cell = [QSU02ManagerCell generateCellWithRowType:rowType];
            break;
        }
        case U02SectionOther: {
            cell = [QSU02OtherCell generateCellWithRowType:rowType];
            break;
        }
        default:
            break;
    }
    cell.sectionType = sectionType;
    cell.rowType = rowType;
    return cell;
}
- (BOOL)cellDidClicked {
    return NO;
}
- (void)resignKeyboardAndPicker {
    
}
@end
