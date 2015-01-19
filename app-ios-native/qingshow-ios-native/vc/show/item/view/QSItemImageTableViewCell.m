//
//  QSItemImageTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 1/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSItemImageTableViewCell.h"

#import "QSItemUtil.h"

@interface QSItemImageTableViewCell ()

@property (strong, nonatomic) QSSingleImageScrollView* imageScrollView;
@property (weak, nonatomic) NSDictionary* itemDict;
@end

@implementation QSItemImageTableViewCell

#pragma mark - Life Cycle
+ (CGSize)getLabelSize:(NSString*)str
{
    float borderWitdh = 320 - 239;
    float labelWidth = [UIScreen mainScreen].bounds.size.width - borderWitdh;
    CGSize conSize = CGSizeMake(labelWidth, INFINITY);
    UIFont* font = [UIFont systemFontOfSize:12.f];
    CGSize size;
    if ([str respondsToSelector:@selector(sizeWithAttributes:)]) {
        //Above IOS 7
        NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc]init];
        paragraphStyle.lineBreakMode = NSLineBreakByWordWrapping;
        size = [str boundingRectWithSize:conSize options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName : font, NSParagraphStyleAttributeName : paragraphStyle} context:nil].size;
    } else {
        //Below IOS 7
        size = [str sizeWithFont:font constrainedToSize:conSize lineBreakMode:NSLineBreakByWordWrapping];
    }
    if (size.height < 17) {
        size.height = 17;
    }
    return size;
}

- (void)awakeFromNib {
    // Initialization code
    [self baseYsetup];
    self.imageScrollView = [[QSSingleImageScrollView alloc] initWithFrame:self.imageContainerView.bounds direction:QSImageScrollViewDirectionHor];
    self.imageScrollView.pageControlOffsetY = 60.f;
    self.imageScrollView.delegate = self;
    self.imageScrollView.enableLazyLoad = YES;
    [self.imageContainerView addSubview:self.imageScrollView];
    self.discountLabel.isWithStrikeThrough = YES;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
#pragma mark - Bind
- (void)bindWithItem:(NSDictionary*)itemDict
{
    self.itemDict = itemDict;
    float height = [QSItemImageTableViewCell getHeightWithItem:itemDict];
    [self resizeWithHeight:height];
    self.imageScrollView.imageUrlArray = [QSItemUtil getImagesUrl:itemDict];
    if ([QSItemUtil getPriceAfterDiscount:itemDict].length) {
        self.saleLabel.hidden = NO;
        self.priceLabel.text = [QSItemUtil getPriceAfterDiscount:itemDict];
        self.discountLabel.text = [QSItemUtil getPrice:itemDict];
        [self.discountLabel sizeToFit];
    } else {
        self.saleLabel.hidden = YES;
        self.priceLabel.text = [QSItemUtil getPrice:itemDict];
        self.discountLabel.text = @"";
    }
    self.nameLabel.text = [QSItemUtil getImageDesc:itemDict atIndex:(int)self.imageScrollView.pageControl.currentPage];

    self.shopBtn.hidden = [QSItemUtil getShopUrl:itemDict] == nil;
    
}
- (void)baseYsetup
{
    
}
- (void)resizeWithHeight:(float)height
{
    float y = height - self.infoContainerView.frame.size.height;
    CGRect rect = self.infoContainerView.frame;
    rect.origin.y = y;
    self.infoContainerView.frame = rect;
    
    rect = self.imageContainerView.frame;
    rect.size.height = height;
    self.imageContainerView.frame = rect;
    self.imageScrollView.frame = self.imageContainerView.bounds;
}

#pragma mark - Static
+ (CGFloat)getHeightWithItem:(NSDictionary*)itemDict
{
    NSDictionary* coverMetadata = nil;
    coverMetadata = itemDict[@"imageMetadata"];
    
    float iniWidth = [UIScreen mainScreen].bounds.size.width;
    
    float height = iniWidth;
    float width = iniWidth;

    if (coverMetadata && coverMetadata[@"height"]) {
        height = ((NSNumber*)coverMetadata[@"height"]).floatValue;
    }
    if (coverMetadata && coverMetadata[@"width"]) {
        width = ((NSNumber*)coverMetadata[@"width"]).floatValue;
    }
    height = height * iniWidth / width;
    return height;
}

- (IBAction)shopBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickShopBtn:)]) {
        [self.delegate didClickShopBtn:self];
    }
}
- (void)imageScrollView:(QSImageScrollViewBase*)view didChangeToPage:(int)page
{
    self.nameLabel.text = [QSItemUtil getImageDesc:self.itemDict atIndex:page];
    float height = [QSItemImageTableViewCell getLabelSize:self.nameLabel.text].height;
    [self updateLayout:height + 3.f];
}
- (void)updateLayout:(float)height
{
    CGRect rect = self.nameLabel.frame;
    rect.size.height = height;
    self.nameLabel.frame = rect;
    CGRect rect2 = self.priceLabel.frame;
    rect2.origin.y = rect.origin.y + rect.size.height + 5.f;
    self.priceLabel.frame = rect2;
    rect2 = self.discountLabel.frame;
    rect2.origin.y = self.priceLabel.frame.origin.y;
    self.discountLabel.frame =rect2;
}
@end
