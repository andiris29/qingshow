//
//  QSUserLocationTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSUserLocationTableViewCell.h"
#import "QSReceiverUtil.h"
@implementation QSUserLocationTableViewCell

- (void)awakeFromNib {
    // Initialization code
    self.selectionStyle = UITableViewCellSelectionStyleNone;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)bindWithDict:(NSDictionary*)dict
{
    self.nameLabel.text = [QSReceiverUtil getName:dict];
    self.phoneLabel.text = [QSReceiverUtil getPhone:dict];
    self.addressLabel.text = [QSReceiverUtil getAddress:dict];
}
#pragma mark - IBAction
- (IBAction)editBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickEditButtonOfCell:)]) {
        [self.delegate didClickEditButtonOfCell:self];
    }
}
- (IBAction)deleteBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickDeleteButtonOfCell:)]) {
        [self.delegate didClickDeleteButtonOfCell:self];
    }
}
- (IBAction)selectedIndicatorPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickSelectedIndicatorOfCell:)]) {
        [self.delegate didClickSelectedIndicatorOfCell:self];
    }
}
@end
