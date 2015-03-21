//
//  QSCreateTradeColorAndSizeBaseTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/20/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeColorAndSizeBaseTableViewCell.h"
#import "QSTradeSelectButton.h"

@implementation QSCreateTradeColorAndSizeBaseTableViewCell

- (void)awakeFromNib {
    // Initialization code
    self.selectionStyle = UITableViewCellSelectionStyleNone;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)btnPressed:(QSTradeSelectButton*)btn
{
    if (btn == self.currentSelectBtn) {
        self.currentSelectBtn.isSelected = NO;
        self.currentSelectBtn = nil;
    } else {
        for (QSTradeSelectButton* btn in self.btnArray) {
            btn.isSelected = NO;
        }
        btn.isSelected = YES;
        self.currentSelectBtn = btn;
    }
    
    if ([self.delegate respondsToSelector:@selector(updateCellTriggerBy:)]) {
        [self.delegate updateCellTriggerBy:self];
    }
    
}

- (void)enableAllBtn
{
    for (QSTradeSelectButton* btn in self.btnArray) {
        btn.enabled = YES;
    }
}

- (id)getInputData
{
    if (!self.currentSelectBtn) {
        return nil;
    } else {
        NSUInteger index = [self.btnArray indexOfObject:self.currentSelectBtn];
        return self.skusArray[index];
    }

}
@end
