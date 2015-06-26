//
//  QSMatcherCanvasView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSMatcherCanvasView.h"
#import "UINib+QSExtension.h"
#import "NSArray+QSExtension.h"
#import "QSItemUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSCommonUtil.h"
#import "QSCanvasImageView.h"
#import "UIView+ScreenShot.h"

@interface QSMatcherCanvasView ()

@property (strong, nonatomic) NSMutableDictionary* categoryIdToEntity;
@property (strong, nonatomic) NSMutableDictionary* categoryIdToView;
@end

@implementation QSMatcherCanvasView
+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QSMatcherCanvasView"];
}


- (void)awakeFromNib {
    self.categoryIdToEntity = [@{} mutableCopy];
    self.categoryIdToView = [@{} mutableCopy];
}

- (void)bindWithCategory:(NSArray*)categoryArray {
    NSArray* newIdArray = [categoryArray mapUsingBlock:^id(NSDictionary* dict) {
        return [QSCommonUtil getIdOrEmptyStr:dict];
    }];
    for (NSDictionary* oldId in [self.categoryIdToView allKeys]) {
        if ([newIdArray indexOfObject:oldId] == NSNotFound) {
            UIView* oldView = self.categoryIdToView[oldId];
            [oldView removeFromSuperview];
            [self.categoryIdToView removeObjectForKey:oldId];
            [self.categoryIdToEntity removeObjectForKey:oldId];
        }
    }
    
    NSArray* oldIdArray = [self.categoryIdToView allKeys];
    NSArray* newCategoryArray = [categoryArray filteredArrayUsingBlock:^BOOL(NSDictionary* dict) {
        NSString* idStr = [QSCommonUtil getIdOrEmptyStr:dict];
        return [oldIdArray indexOfObject:idStr] == NSNotFound;
    }];
    
    for (int i = 0; i < newCategoryArray.count; i++) {
        float sizeHeight = self.frame.size.height / 2;
        float sizeWidth = sizeHeight / 16 * 9;
        float width = self.frame.size.width / 3;
        float height = self.frame.size.height  / ((newCategoryArray.count + 2)/ 3);
        UIImageView* imgView = [[QSCanvasImageView alloc] initWithFrame:CGRectMake(width * (i % 3), height * (i / 3), sizeWidth, sizeHeight)];
        imgView.userInteractionEnabled = YES;
        UILongPressGestureRecognizer* ges = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(didTapView:)];
        ges.minimumPressDuration = 0.f;
        [imgView addGestureRecognizer:ges];
        
        NSDictionary* categoryDict = newCategoryArray[i];
        NSString* categoryId = [QSCommonUtil getIdOrEmptyStr:categoryDict];
        self.categoryIdToEntity[categoryId] = categoryDict;
        self.categoryIdToView[categoryId] = imgView;

        [self addSubview:imgView];
    }
}

- (void)setItem:(NSDictionary*)itemDict forCategory:(NSDictionary*)category {
    NSString* categoryId = [QSCommonUtil getIdOrEmptyStr:category];
    [self setItem:itemDict forCategoryId:categoryId];
}
- (void)setItem:(NSDictionary *)itemDict forCategoryId:(NSString *)categoryId {
    UIImageView* imgView = self.categoryIdToView[categoryId];
    __weak UIImageView* weakImgView = imgView;
    [imgView setImageFromURL:[QSItemUtil getThumbnail:itemDict] completeBlock:^{
        UIImage* img = weakImgView.image;
        CGSize imgSize = img.size;
        CGSize viewSize = weakImgView.bounds.size;
        CGFloat newHeigh = viewSize.width / imgSize.width * imgSize.height;
        viewSize.height = newHeigh;
        CGRect bounds = weakImgView.bounds;
        bounds.size = viewSize;
        weakImgView.bounds = bounds;
    }];
    
}
#pragma mark - Gesture
- (void)didTapView:(UIGestureRecognizer*)ges {
    if (ges.state == UIGestureRecognizerStateBegan) {
        [self bringSubviewToFront:ges.view];
        [self updateHighlightView:ges.view];
        
        NSArray* idArray = [self.categoryIdToView allKeys];
        NSString* categoryId = nil;
        for (categoryId in idArray) {
            UIView* view = self.categoryIdToView[categoryId];
            if (view == ges.view) {
                NSDictionary* categoryDict = self.categoryIdToEntity[categoryId];
                if ([self.delegate respondsToSelector:@selector(canvasView:didTapCategory:)]) {
                    [self.delegate canvasView:self didTapCategory:categoryDict];
                }
            }
        }
    }
}

- (void)updateHighlightView:(UIView*)highlightView {
    NSArray* viewArray = [self.categoryIdToView allValues];
    for (UIImageView* imgView in viewArray) {
        if (imgView != highlightView) {
            imgView.layer.borderColor = [UIColor clearColor].CGColor;
            imgView.layer.borderWidth = 0.f;
        } else {
            imgView.layer.borderColor = [UIColor colorWithRed:240.f/255.f green:149.f/255.f blue:164.f/255.f alpha:1.f].CGColor;
            imgView.layer.borderWidth = 1.f;
        }
        
    }
}
- (UIImage*)submitView {
    [self updateHighlightView:nil];
    return [self makeScreenShot];
}
@end
