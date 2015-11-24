//
//  QSMatcherCollectionViewHeader.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSMatcherCollectionViewHeader.h"
#import "QSMatcherCollectionViewHeaderUserRowView.h"
#import "UINib+QSExtension.h"
#import "QSDateUtil.h"
#import "QSPeopleUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@interface QSMatcherCollectionViewHeader ()
@property (strong, nonatomic) QSMatcherCollectionViewHeaderUserRowView* headersView;

@end

@implementation QSMatcherCollectionViewHeader
+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QSMatcherCollectionViewHeader"];
}
- (void)awakeFromNib {
    self.headersView = [[QSMatcherCollectionViewHeaderUserRowView alloc] init];
    self.headersView.kindomIconHidden = NO;
    [self.userHeadContaienr addSubview:self.headersView];
    self.headersView.frame = self.userHeadContaienr.bounds;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    self.headersView.frame = self.userHeadContaienr.bounds;
}

- (void)bindWithOwners:(NSArray*)owners ownerCount:(int)count index:(int)index {
    if (!owners || !owners.count) {
        self.userHeadContaienr.hidden = YES;
        self.topNumberLabel.hidden = YES;
        self.numberLabel.hidden = YES;
        self.headImgView.hidden = YES;
        self.topListTextLabel.hidden = YES;
    } else {
        self.userHeadContaienr.hidden = NO;
        self.topNumberLabel.hidden = NO;
        self.numberLabel.hidden = NO;

        [self.headersView bindWithUsers:owners];
        self.numberLabel.text = [NSString stringWithFormat:@"+%d", count];
        if (index >= 0 && index < owners.count) {
            NSDictionary* peopleDict = owners[index];
            NSURL* iconUrl = [QSPeopleUtil getHeadIconUrl:peopleDict type:QSImageNameType50];
            self.headImgView.hidden = NO;
            [self.headImgView setImageFromURL:iconUrl];
        } else {
            self.headImgView.hidden = YES;
        }
    }
}
- (void)updateDate:(NSDate*)date {
    self.dateLabel.text = [QSDateUtil buildDayStringFromDate:date];
    
}
@end
