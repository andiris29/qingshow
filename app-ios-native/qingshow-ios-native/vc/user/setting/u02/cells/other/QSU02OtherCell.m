//
//  QSUserSettingOtherCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/5/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU02OtherCell.h"

NSString* u02OtherTypeToTitle(U02SectionOtherRow type) {
    return @[@"更改密码",@"佣金账户"][type];
}

@implementation QSU02OtherCell
+ (QSU02AbstractTableViewCell*)generateCellWithRowType:(NSInteger)rowType{
    return [[QSU02OtherCell alloc] init];
}
- (id)init {
    self = [super init];
    if (self) {
        self.textLabel.font = NEWFONT;
        self.textLabel.frame = CGRectMake(8, 8, 50, 30);
        self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    return self;
}


- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)bindWithUser:(NSDictionary *)userDict {
    self.textLabel.text = u02OtherTypeToTitle(self.rowType);
}
- (void)cellDidClicked {
    if (self.rowType == U02SectionOtherRowPasswd) {
        [self.delegate showChangePasswordVc];
    }
    else if (self.rowType == U02SectionOtherRowBonus)
    {
        [self.delegate  showBonuesVC];
    }
}
@end
