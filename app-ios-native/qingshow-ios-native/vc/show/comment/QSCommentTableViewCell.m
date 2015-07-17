//
//  QSCommentTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>

#import "QSCommentTableViewCell.h"

#import "UIImageView+MKNetworkKitAdditions.h"

#import "QSCommentUtil.h"
#import "QSPeopleUtil.h"
#import "QSLayoutUtil.h"


@implementation QSCommentTableViewCell

#pragma mark - Height
+ (CGSize)getCellSize:(NSDictionary*)commentDict
{
    NSString* str = [QSCommentUtil getContent:commentDict];
    float baseHeight = 62.f - 17.f;
    CGSize size = [self getCommentSize:str];
    size.height += baseHeight;
    return size;
}
+ (CGSize)getCommentSize:(NSString*)str
{
    //237
    float borderWitdh = 320 - 237;
    float labelWidth = [UIScreen mainScreen].bounds.size.width - borderWitdh;
    UIFont* font = [UIFont systemFontOfSize:14.f];
    CGSize size =[QSLayoutUtil sizeForString:str withMaxWidth:labelWidth height:INFINITY font:font];
    if (size.height < 17) {
        size.height = 17;
    }
    return size;
}
#pragma mark - Life Cycle

- (void)awakeFromNib {
    self.iconImageView.layer.cornerRadius = self.iconImageView.bounds.size.width / 2;
    self.iconImageView.layer.masksToBounds = YES;
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapIconImage:)];
    [self.iconImageView addGestureRecognizer:ges];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

#pragma mark - Bind
- (void)bindWithComment:(NSDictionary*)commentDict
{
    NSDictionary* peopleDict = [QSCommentUtil getPeople:commentDict];
    self.nameLabel.text = [QSPeopleUtil getNickname:peopleDict];
    [self.iconImageView setImageFromURL:[QSPeopleUtil getHeadIconUrl:peopleDict] placeHolderImage:[UIImage imageNamed:@"nav_btn_account"] animation:YES];

    self.dateLabel.text = [QSCommentUtil getFormatedDateString:commentDict];
    NSString* content = [QSCommentUtil getContent:commentDict];
    CGSize contentLabelSize = [QSCommentTableViewCell getCommentSize:content];
    CGRect rect = self.contentLabel.frame;
    rect.size.height = contentLabelSize.height;
    self.contentLabel.frame = rect;
    self.contentLabel.text = content;
    rect = self.splitter.frame;
    rect.origin.y = [QSCommentTableViewCell getCellSize:commentDict].height - 1;
    self.splitter.frame = rect;
    
}
- (IBAction)didTapIconImage:(UIGestureRecognizer*)ges
{
    if ([self.delegate respondsToSelector:@selector(didTapIcon:)]) {
        [self.delegate didTapIcon:self];
    }
}
@end
