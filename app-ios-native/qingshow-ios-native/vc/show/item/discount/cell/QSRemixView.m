//
//  QSRemixView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/1.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSRemixView.h"
#import "QSRemixImageView.h"
#import "QSItemUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "NSDictionary+QSExtension.h"
#import "QSRectUtil.h"

@interface QSRemixView ()

@property (strong, nonatomic) QSRemixImageView* masterImageView;
@property (strong, nonatomic) NSMutableArray* slaveImageViews;

@property (strong, nonatomic) NSMutableArray* allImageViews;
@end

@implementation QSRemixView
#pragma mark - Init
- (instancetype)init {
    self = [super init];
    if (self) {
        self.slaveImageViews = [@[] mutableCopy];
    }
    return self;
}
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.slaveImageViews = [@[] mutableCopy];
    }
    return self;
}
#pragma mark -
- (void)bindWithMasterItem:(NSDictionary*)masterItem
                 remixInfo:(NSDictionary*)remixInfo {
    if (self.masterImageView) {
        [self.masterImageView removeFromSuperview];
        self.masterImageView = nil;
    }
    for (UIView* v in self.slaveImageViews) {
        [v removeFromSuperview];
    }
    [self.slaveImageViews removeAllObjects];
    
    NSArray* masterConfig = [remixInfo arrayValueForKeyPath:@"master.rect"];
    self.masterImageView = [self _generateImageViewItem:masterItem config:masterConfig];
    [self.masterImageView setIsSelected:YES];
    [self addSubview:self.masterImageView];
    
    NSArray* slaveInfos = [remixInfo arrayValueForKeyPath:@"slaves"];
    for (NSDictionary* slaveInfo in slaveInfos) {
        NSDictionary* itemDict = [slaveInfo dictValueForKeyPath:@"itemRef"];
        NSArray* rectConfig = [slaveInfo arrayValueForKeyPath:@"rect"];
        QSRemixImageView* imgView = [self _generateImageViewItem:itemDict config:rectConfig];
        [self.slaveImageViews addObject:imgView];
        [self addSubview:imgView];
    }
    
    self.allImageViews = [self.slaveImageViews mutableCopy];
    [self.allImageViews addObject:self.masterImageView];
    [self bringSubviewToFront:self.masterImageView];
}

- (void)didTapImgView:(UITapGestureRecognizer*)ges {
    QSRemixImageView* imgView = (QSRemixImageView*)ges.view;
    NSDictionary* itemDict = imgView.userInfo;
    NSArray* skuProperties = [QSItemUtil getSkuProperties:itemDict];
    if ([QSItemUtil getReadOnly:itemDict] || !skuProperties) {
        return;
    }
    
    for (QSRemixImageView* v in self.allImageViews) {
        v.isSelected = imgView == v;
        if (v.isSelected) {
            [self bringSubviewToFront:v];
        }
    }
    
    if ([self.delegate respondsToSelector:@selector(remixView:didTapItem:)]) {
        [self.delegate remixView:self didTapItem:itemDict];
    }
}
#pragma mark - Private
- (CGRect)_generateRectFromRectConfig:(NSArray*)rectConfig {
    if (rectConfig.count != 4) {
        return CGRectZero;
    } else {
        CGSize size = self.frame.size;
        NSNumber* x = rectConfig[0];
        NSNumber* y = rectConfig[1];
        NSNumber* width = rectConfig[2];
        NSNumber* height = rectConfig[3];
        
        return CGRectMake(x.doubleValue * size.width / 100.0, y.doubleValue * size.height / 100.0, width.doubleValue * size.width / 100.0, height.doubleValue * size.height / 100.0);
    }
}

- (QSRemixImageView*)_generateImageViewItem:(NSDictionary*)itemDict
                                     config:(NSArray*)rectConfig {
    QSRemixImageView* imgView = [[QSRemixImageView alloc] init];
    imgView.frame = [self _generateRectFromRectConfig:rectConfig];
    imgView.userInfo = itemDict;
    __weak QSRemixImageView* weakImgView = imgView;
    imgView.hidden = YES;
    [imgView.imageView setImageFromURL:[QSItemUtil getThumbnail:itemDict] beforeCompleteBlock:^(UIImage *img) {

        CGSize viewSize = weakImgView.bounds.size;
        CGRect bounds = weakImgView.bounds;
        viewSize = [QSRectUtil scaleSize:img.size toFitSize:viewSize];
        bounds.size = viewSize;
        weakImgView.bounds = bounds;
        weakImgView.frame = [QSRectUtil reducedFrame:weakImgView.frame forContainer:self.bounds];
        weakImgView.hidden = NO;
    } animation:NO];

    imgView.userInteractionEnabled = YES;
    UITapGestureRecognizer* tapGes = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapImgView:)];
    [imgView addGestureRecognizer:tapGes];
    
    return imgView;
}
@end
