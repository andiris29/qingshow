//
//  QSDiscountRemixCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSDiscountRemixCell.h"
#import "UINib+QSExtension.h"
#import "QSItemUtil.h"
#import "QSRemixView.h"

@interface QSDiscountRemixCell () <QSRemixViewDelegate>
@property (strong, nonatomic) QSRemixView* remixView;
@property (strong, nonatomic) NSDictionary* itemDict;
@end

@implementation QSDiscountRemixCell

#pragma mark -
+ (instancetype)generateCell {
    return [UINib generateViewWithNibName:@"QSDiscountRemixCell"];
}
#pragma mark - Life Cycle
- (void)awakeFromNib {
    self.remixBtn.layer.cornerRadius = self.remixBtn.bounds.size.height / 2;
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    self.remixView = [[QSRemixView alloc] initWithFrame:self.remixContainer.bounds];
    self.remixView.delegate = self;
    [self.remixContainer addSubview:self.remixView];
}

#pragma mark -
- (void)bindWithData:(NSDictionary *)itemDict {
    
    self.titleLabel.text = [QSItemUtil getShopNickName:itemDict];
    self.mesageLabel.text = [QSItemUtil getExpectableMessage:itemDict];
    [self _updatePreviousAndNextBtn];
}

- (void)bindWithItem:(NSDictionary*)itemDict remix:(NSDictionary*)remixInfoDict {
    [self.remixView bindWithMasterItem:itemDict remixInfo:remixInfoDict];
    [self _updatePreviousAndNextBtn];
}

- (CGFloat)getHeight:(NSDictionary *)itemDict {
    return 440.f;
}

#pragma mark - IBAction
- (IBAction)remixBtnPressed:(UIButton*)sender {
    if ([self.delegate respondsToSelector:@selector(discountCellRemixBtnPressed:)]) {
        [self.delegate discountCellRemixBtnPressed:self];
        [self _updatePreviousAndNextBtn];
    }

}

- (IBAction)previousRemixBtnPressed:(UIButton*)sender {
    if ([self.delegate respondsToSelector:@selector(discountCellPreviousRemixBtnPressed:)]) {
        [self.delegate discountCellPreviousRemixBtnPressed:self];
        [self _updatePreviousAndNextBtn];
    }

}

- (IBAction)nextRemixBtnPressed:(UIButton*)sender {
    if ([self.delegate respondsToSelector:@selector(discountCellNextRemixBtnPressed:)]) {
        [self.delegate discountCellNextRemixBtnPressed:self];
    }
}

#pragma mark - QSRemixViewDelegate 
- (void)remixView:(QSRemixView*)view didTapItem:(NSDictionary*)item {
    if ([self.delegate respondsToSelector:@selector(discountCell:didSelectItem:)]) {
        [self.delegate discountCell:self didSelectItem:item];
    }
}

- (void)_updatePreviousAndNextBtn {
    BOOL hasPrevious = NO;
    if ([self.delegate respondsToSelector:@selector(discountCellHasPreviousRemix:)]) {
        hasPrevious = [self.delegate discountCellHasPreviousRemix:self];
    }
    BOOL hasNext = NO;
    if ([self.delegate respondsToSelector:@selector(discountCellHasNextRemix:)]) {
        hasNext = [self.delegate discountCellHasNextRemix:self];
    }
    self.previousBtn.hidden = !hasPrevious;
    self.nextBtn.hidden = !hasNext;
}
@end
