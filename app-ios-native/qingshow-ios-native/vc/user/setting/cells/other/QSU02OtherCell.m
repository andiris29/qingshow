//
//  QSUserSettingOtherCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/5/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU02OtherCell.h"
#import "QSUnreadManager.h"
NSString* u02OtherTypeToTitle(U02SectionOtherRow type) {
    return @[@"更改密码",@"佣金账户"][type];
}

@interface QSU02OtherCell ()

@property (strong, nonatomic) UIImageView* dotImageView;

@end

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
        self.dotImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"root_menu_item_dot"]];
        [self.dotImageView sizeToFit];
        self.dotImageView.hidden = YES;
        [self.contentView addSubview:self.dotImageView];
    }
    return self;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    self.dotImageView.center = CGPointMake(90, 20);
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
    if (self.rowType == U02SectionOtherRowBonus) {
        if ([[QSUnreadManager getInstance] shouldShowBonuUnread]) {
            [self showDot];
        } else {
            [self hideDot];
        }
    }
    
}
- (BOOL)cellDidClicked {
    if (self.rowType == U02SectionOtherRowPasswd) {
        [self.delegate showChangePasswordVc];
        return YES;
    }
    else if (self.rowType == U02SectionOtherRowBonus)
    {
        [self.delegate  showBonuesVC];
        return YES;
    }
    return NO;
}

- (void)showDot {
    self.dotImageView.hidden = NO;
}
- (void)hideDot {
    self.dotImageView.hidden = YES;
}
@end
