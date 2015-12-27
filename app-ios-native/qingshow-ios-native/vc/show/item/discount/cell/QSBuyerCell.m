//
//  QSBuyerCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/26.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSBuyerCell.h"
#import "UINib+QSExtension.h"
#import "QSMatcherCollectionViewHeaderUserRowView.h"

@interface QSBuyerCell ()
@property (strong, nonatomic) QSMatcherCollectionViewHeaderUserRowView* headView;

@end

@implementation QSBuyerCell

+ (instancetype)generateCell {
    return [UINib generateViewWithNibName:@"QSBuyerCell"];
}

- (void)awakeFromNib {
    self.headView = [[QSMatcherCollectionViewHeaderUserRowView alloc] init];
    self.headView.alignCenter = YES;
    [self.buyerContainerView addSubview:self.headView];
    self.headView.frame = self.buyerContainerView.bounds;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.headView.frame = self.buyerContainerView.bounds;
}

- (CGFloat)getHeight:(NSDictionary*)itemDict {
    return 74.f;
}
- (void)bindWithData:(NSDictionary *)itemDict {
    
}
- (void)bindWithBuyerInfo:(NSDictionary*)dict {
#warning TODO Bind With Users
    
}
@end
