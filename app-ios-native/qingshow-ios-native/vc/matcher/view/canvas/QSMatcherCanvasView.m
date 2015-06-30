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
#import "QSCategoryUtil.h"
#import "QSCanvasImageView.h"
#import "QSRandomUtil.h"
#import "UIView+ScreenShot.h"

@interface QSMatcherCanvasView ()

@property (strong, nonatomic) NSMutableDictionary* categoryIdToEntity;
@property (strong, nonatomic) NSMutableDictionary* categoryIdToView;

@property (strong, nonatomic) UIView* currentFocusView;

//For Gesture
@property (assign, nonatomic) float prePinchScale;
@property (assign, nonatomic) float preRotation;
@property (assign, nonatomic) CGPoint prePanTranslation;
@end

@implementation QSMatcherCanvasView
+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QSMatcherCanvasView"];
}


- (void)awakeFromNib {
    self.categoryIdToEntity = [@{} mutableCopy];
    self.categoryIdToView = [@{} mutableCopy];
    [self addGesture];
    self.maxColumn = 1;
    self.maxRow = 3;
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
    
    [self addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapBlank:)]];
    
    for (NSDictionary* categoryDict in newCategoryArray) {
        float sizeHeight = self.frame.size.height / (self.maxRow + 1);
        float sizeWidth = self.frame.size.width / (self.maxColumn + 1);
        int row = -1, column = -1;
        if ([QSCategoryUtil getMathchInfoRow:categoryDict]) {
            row = [QSCategoryUtil getMathchInfoRow:categoryDict].intValue;
        } else {
            row = [QSRandomUtil randomRangeFrom:0 to:self.maxRow + 1];
        }
        if ([QSCategoryUtil getMatchInfoColumn:categoryDict]) {
            column = [QSCategoryUtil getMatchInfoColumn:categoryDict].intValue;
        } else {
            column = [QSRandomUtil randomRangeFrom:0 to:self.maxColumn + 1];
        }
        float y = self.frame.size.height * (row + 0.5) / (self.maxRow + 1);
        float x = self.frame.size.width * (column + 0.5) / (self.maxColumn + 1);

        QSCanvasImageView* imgView = [[QSCanvasImageView alloc] initWithFrame:CGRectMake(x, y, sizeWidth, sizeHeight)];
        imgView.center = CGPointMake(x, y);
        imgView.userInteractionEnabled = YES;
        UILongPressGestureRecognizer* ges = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(didTapEntityView:)];
        ges.minimumPressDuration = 0.f;
        [imgView addGestureRecognizer:ges];
        
        NSString* categoryId = [QSCommonUtil getIdOrEmptyStr:categoryDict];
        self.categoryIdToEntity[categoryId] = categoryDict;
        self.categoryIdToView[categoryId] = imgView;
        imgView.categoryId = categoryId;
        [self addSubview:imgView];
    }
}

- (void)setItem:(NSDictionary*)itemDict forCategory:(NSDictionary*)category {
    NSString* categoryId = [QSCommonUtil getIdOrEmptyStr:category];
    [self setItem:itemDict forCategoryId:categoryId];
}
- (void)setItem:(NSDictionary *)itemDict forCategoryId:(NSString *)categoryId {
    QSCanvasImageView* imgView = self.categoryIdToView[categoryId];
    __weak QSCanvasImageView* weakImgView = imgView;
    [imgView.imgView setImageFromURL:[QSItemUtil getThumbnail:itemDict] beforeCompleteBlock:^(UIImage *img) {
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
- (void)makeViewFocus:(UIView*)view {
    self.currentFocusView = view;
    [self bringSubviewToFront:view];
    [self updateHighlightView:view];
    
    NSArray* idArray = [self.categoryIdToView allKeys];
    NSString* categoryId = nil;
    for (categoryId in idArray) {
        UIView* v = self.categoryIdToView[categoryId];
        if (v == view) {
            NSDictionary* categoryDict = self.categoryIdToEntity[categoryId];
            if ([self.delegate respondsToSelector:@selector(canvasView:didTapCategory:)]) {
                [self.delegate canvasView:self didTapCategory:categoryDict];
            }
        }
    }
}
//Tap
- (void)didTapBlank:(UIGestureRecognizer*)ges {
    return;
    [self updateHighlightView:nil];
    if ([self.delegate respondsToSelector:@selector(canvasView:didTapCategory:)]) {
        [self.delegate canvasView:self didTapCategory:nil];
    }
}
- (void)didTapEntityView:(UIGestureRecognizer*)ges {
    if (ges.state == UIGestureRecognizerStateBegan) {
        QSCanvasImageView* imgView = (QSCanvasImageView*)ges.view;
        CGPoint p = [ges locationInView:imgView];
        if ([imgView judgeIsHitRemoveButton:p]) {
            [self canvasImageViewDidClickRemoveBtn:imgView];
            return;
        }
        
        if (self.currentFocusView) {
            return;
        }
        [self makeViewFocus:ges.view];
    } else if (ges.state != UIGestureRecognizerStateChanged) {
        //End
        self.currentFocusView = nil;
    }
}

//Pan, drag, pinch
- (void)addGesture {
    UIPinchGestureRecognizer* pinchGes = [[UIPinchGestureRecognizer alloc] initWithTarget:self action:@selector(didPinch:)];
    pinchGes.delegate = self;
    [self addGestureRecognizer:pinchGes];
    
    //        UIRotationGestureRecognizer* rotateGes = [[UIRotationGestureRecognizer alloc] initWithTarget:self action:@selector(didRotate:)];
    //        rotateGes.delegate = self;
    //        [self addGestureRecognizer:rotateGes];
    
    UIPanGestureRecognizer* panGes = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(didPan:)];
    panGes.delegate = self;
    [self addGestureRecognizer:panGes];
}

- (void)didRotate:(UIRotationGestureRecognizer*)ges {
    if (ges.state == UIGestureRecognizerStateBegan) {
        self.preRotation = ges.rotation;
    } else if (ges.state == UIGestureRecognizerStateChanged) {
        float newRotation = ges.rotation;
        self.currentFocusView.transform = CGAffineTransformRotate(self.currentFocusView.transform, newRotation - self.preRotation);
        self.preRotation = newRotation;
    } else  {
        self.preRotation = 0;
    }
}

- (void)didPinch:(UIPinchGestureRecognizer*)ges {
    if (ges.state == UIGestureRecognizerStateBegan) {
        self.prePinchScale = ges.scale;
    } else if (ges.state == UIGestureRecognizerStateChanged) {
        float newScale = ges.scale;
        CGAffineTransform preTrans = self.currentFocusView.transform;
        self.currentFocusView.transform = CGAffineTransformScale(self.currentFocusView.transform, newScale / self.prePinchScale, newScale / self.prePinchScale);
        if (!CGRectContainsRect(self.frame, self.currentFocusView.frame)) {
            //不移出画布
            self.currentFocusView.transform = preTrans;
        } else {
            self.prePinchScale = newScale;
        }

    } else  {
        self.prePinchScale = 1.f;
    }
    
}

- (void)didPan:(UIPanGestureRecognizer*)ges {
    CGPoint p = [ges translationInView:self];
    if (ges.state == UIGestureRecognizerStateBegan) {
        self.prePanTranslation = p;
    } else if (ges.state == UIGestureRecognizerStateChanged) {
        CGPoint c = self.currentFocusView.center;
        self.currentFocusView.center = CGPointMake(c.x + p.x - self.prePanTranslation.x, c.y + p.y - self.prePanTranslation.y);
        if (!CGRectContainsRect(self.frame, self.currentFocusView.frame)) {
            //不移出画布
            self.currentFocusView.center = c;
        } else {
            self.prePanTranslation = p;
        }
        
    } else {
        self.prePanTranslation = CGPointZero;
    }
}





- (void)updateHighlightView:(UIView*)highlightView {
    NSArray* viewArray = [self.categoryIdToView allValues];
    for (QSCanvasImageView* imgView in viewArray) {
        imgView.hover = imgView == highlightView;
    }
}
- (UIImage*)submitView {
    [self updateHighlightView:nil];
    return [self makeScreenShot];
}


#pragma mark -
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    return YES;
}

#pragma mark - QSCanvasImageViewDelegate
- (void)canvasImageViewDidClickRemoveBtn:(QSCanvasImageView*)view {
    NSString* categoryId = view.categoryId;
    [view removeFromSuperview];
    NSDictionary* categoryDict = self.categoryIdToEntity[categoryId];
    [self.categoryIdToEntity removeObjectForKey:categoryId];
    [self.categoryIdToView removeObjectForKey:categoryId];
    if ([self.delegate respondsToSelector:@selector(canvasView:didRemoveCategory:)]) {
        [self.delegate canvasView:self didRemoveCategory:categoryDict];
    }
}
@end
