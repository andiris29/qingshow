//
//  QSUserLocationTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSUserLocationTableViewCell.h"
#import "QSReceiverUtil.h"
#import "QSLayoutUtil.h"
@implementation QSUserLocationTableViewCell

- (void)awakeFromNib {
    // Initialization code
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    _isSelectedReceiver = NO;
    self.selectedIndicator.highlighted = _isSelectedReceiver;
    self.editBtn.highlighted = _isSelectedReceiver;
    self.deleteBtn.highlighted = _isSelectedReceiver;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)setIsSelectedReceiver:(BOOL)isSelectedReceiver
{
    _isSelectedReceiver = isSelectedReceiver;
    self.selectedIndicator.highlighted = _isSelectedReceiver;
    self.editBtn.highlighted = _isSelectedReceiver;
    self.deleteBtn.highlighted = _isSelectedReceiver;
}

- (void)bindWithDict:(NSDictionary*)dict
{
    self.nameLabel.text = [QSReceiverUtil getName:dict];
    self.phoneLabel.text = [QSReceiverUtil getPhone:dict];
    self.addressLabel.text = [NSString stringWithFormat:@"%@ %@", [QSReceiverUtil getProvince:dict], [QSReceiverUtil getAddress:dict]];

    float width = [UIScreen mainScreen].bounds.size.width - 60.f;
    float height = [QSLayoutUtil sizeForString:[NSString stringWithFormat:@"%@ %@", [QSReceiverUtil getProvince:dict], [QSReceiverUtil getAddress:dict]] withMaxWidth:width height:INFINITY font:NEWFONT].height;
    CGRect rect = self.addressLabel.frame;
    rect.size.height = height;
    self.addressLabel.frame = rect;
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

+ (float)getHeightWithDict:(NSDictionary*)dict {
    float width = [UIScreen mainScreen].bounds.size.width - 60.f;
    float height = [QSLayoutUtil sizeForString:[NSString stringWithFormat:@"%@ %@", [QSReceiverUtil getProvince:dict], [QSReceiverUtil getAddress:dict]] withMaxWidth:width height:INFINITY font:NEWFONT].height;
    return 163.f + height - 15.f;
}
@end
