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

@interface QSMatcherCollectionViewHeader ()<QSMatcherCollectionViewHeaderUserRowViewDelegate>
@property (strong, nonatomic) QSMatcherCollectionViewHeaderUserRowView* headersView;
@property (weak, nonatomic) NSDictionary* peopleDict;
@property (strong, nonatomic) NSArray* peopleArray;
@end

@implementation QSMatcherCollectionViewHeader
+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QSMatcherCollectionViewHeader"];
}
- (void)awakeFromNib {
    self.headersView = [[QSMatcherCollectionViewHeaderUserRowView alloc] init];
    self.headersView.delegate = self;
    self.headersView.kindomIconHidden = NO;

    [self.userHeadContaienr addSubview:self.headersView];
    self.headersView.frame = self.userHeadContaienr.bounds;
    
    UITapGestureRecognizer* tapGes = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapHeaderImgView:)];
    self.headImgView.userInteractionEnabled = YES;
    [self.headImgView addGestureRecognizer:tapGes];
}
- (void)layoutSubviews {
    [super layoutSubviews];
    self.headersView.frame = self.userHeadContaienr.bounds;
}

- (void)bindWithOwners:(NSArray*)owners ownerCount:(int)count index:(int)index {
    if (!owners || !owners.count) {
        self.peopleDict = nil;
        self.peopleArray = nil;
        self.userHeadContaienr.hidden = YES;
        self.topNumberLabel.hidden = YES;
        self.numberLabel.hidden = YES;
        self.headImgView.hidden = YES;
    } else {
        self.userHeadContaienr.hidden = NO;
        self.topNumberLabel.hidden = NO;
        self.numberLabel.hidden = NO;
        self.peopleArray = owners;
        [self.headersView bindWithUsers:owners];
        self.numberLabel.text = [NSString stringWithFormat:@"+%d", count];
        if (index >= 0 && index < owners.count) {
            NSDictionary* peopleDict = owners[index];
            self.peopleDict = peopleDict;
            NSURL* iconUrl = [QSPeopleUtil getHeadIconUrl:peopleDict type:QSImageNameType50];
            self.headImgView.hidden = NO;
            self.topLabel.hidden = NO;
            self.topLabel.text = [NSString stringWithFormat:@"TOP%d", index];
            [self.headImgView setImageFromURL:iconUrl];
        } else {
            self.topLabel.hidden = YES;
            self.peopleDict = nil;
            self.headImgView.hidden = YES;
        }
    }
}
- (void)updateDate:(NSDate*)date {
    self.dateLabel.text = [QSDateUtil buildDayStringFromDate:date];
}

- (void)didTapHeaderImgView:(UITapGestureRecognizer*)gesture {
    if ([self.delegate respondsToSelector:@selector(header:didClickPeople:)]) {
        [self.delegate header:self didClickPeople:self.peopleDict];
    }
}


- (void)userRowView:(QSMatcherCollectionViewHeaderUserRowView*)view didClickIndex:(NSUInteger)index {
    if (index < self.peopleArray.count) {
        NSDictionary* p = self.peopleArray[index];
        if ([self.delegate respondsToSelector:@selector(header:didClickPeople:)]) {
            [self.delegate header:self didClickPeople:p];
        }
    }
}
@end
