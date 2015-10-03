//
//  QSUserSettingManagerCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/5/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU02ManagerCell.h"

NSString* managerCellTypeToTitle(U02SectionManagerRow type)
{
    return @[@"收货地址管理", @"订单管理"][type];
}

@implementation QSU02ManagerCell
+ (QSU02AbstractTableViewCell*)generateCellWithRowType:(NSInteger)rowType{
    return [[QSU02ManagerCell alloc] init];
}
- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
- (void)bindWithUser:(NSDictionary *)userDict
{
    self.textLabel.font = NEWFONT;
    self.textLabel.text = managerCellTypeToTitle(self.rowType);
    self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
}
- (BOOL)cellDidClicked {
    switch (self.rowType) {
        case U02SectionManagerRowAddress:{
            [self.delegate showAddressList];
            return YES;
            break;
        }
    }
    return NO;
}
@end
