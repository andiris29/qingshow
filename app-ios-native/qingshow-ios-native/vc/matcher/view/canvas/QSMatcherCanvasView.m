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
#import "QSEntityUtil.h"
#import "QSCategoryUtil.h"
#import "QSCanvasImageView.h"
#import "QSRandomUtil.h"
#import "UIView+ScreenShot.h"
#import "QSRectUtil.h"

@interface QSMatcherCanvasView ()

@property (strong, nonatomic) NSMutableDictionary* categoryIdToEntity;
@property (strong, nonatomic) NSMutableDictionary* categoryIdToView;

@property (strong, nonatomic) QSCanvasImageView* currentFocusView;

//For Gesture
@property (assign, nonatomic) float prePinchScale;
@property (assign, nonatomic) float preRotation;
@property (assign, nonatomic) CGPoint prePanTranslation;
@property (assign, nonatomic) CGPoint initPanTranslation;

@property (strong, nonatomic) QSCanvasImageView* imgViewToBeRemoved;
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
        return [QSEntityUtil getIdOrEmptyStr:dict];
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
        NSString* idStr = [QSEntityUtil getIdOrEmptyStr:dict];
        return [oldIdArray indexOfObject:idStr] == NSNotFound;
    }];
    
//    [self addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapBlank:)]];
    
    NSMutableArray* mCates = [newCategoryArray mutableCopy];
    for (int i = 0; i < self.maxColumn + 1; i++) {
        NSArray* cates = [newCategoryArray filteredArrayUsingBlock:^BOOL(NSDictionary* dict) {
            NSNumber* c = [QSCategoryUtil getMatchInfoColumn:dict];
            return c && c.intValue == i;
        }];
        [mCates removeObjectsInArray:cates];
        [self _addCategories:cates maxRow:cates.count - 1 maxColumn:self.maxColumn];
    }
    
    if (mCates.count) {
        [self _addCategories:mCates maxRow:self.maxRow maxColumn:self.maxColumn];
    }
}

- (void)_addCategories:(NSArray*)newCategoryArray maxRow:(int)maxRow maxColumn:(int)maxColumn {
    for (NSDictionary* categoryDict in newCategoryArray) {
        float sizeHeight = self.frame.size.height / (maxRow + 1);
        float sizeWidth = self.frame.size.width / (maxColumn + 1);
        int row = -1, column = -1;
        if ([QSCategoryUtil getMathchInfoRow:categoryDict]) {
            row = [QSCategoryUtil getMathchInfoRow:categoryDict].intValue;
        } else {
            row = [QSRandomUtil randomRangeFrom:0 to:maxRow + 1];
        }
        if ([QSCategoryUtil getMatchInfoColumn:categoryDict]) {
            column = [QSCategoryUtil getMatchInfoColumn:categoryDict].intValue;
        } else {
            column = [QSRandomUtil randomRangeFrom:0 to:maxColumn + 1];
        }
        
        float y = self.frame.size.height * (row + 0.5) / (maxRow + 1);
        float x = self.frame.size.width * (column + 0.5) / (maxColumn + 1);
        
        QSCanvasImageView* imgView = [[QSCanvasImageView alloc] initWithFrame:CGRectMake(x, y, sizeWidth, sizeHeight)];
        imgView.center = CGPointMake(x, y);
        imgView.userInteractionEnabled = YES;
        UILongPressGestureRecognizer* ges = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(didTapEntityView:)];
        ges.minimumPressDuration = 0.f;
        [imgView addGestureRecognizer:ges];
        
        NSString* categoryId = [QSEntityUtil getIdOrEmptyStr:categoryDict];
        self.categoryIdToEntity[categoryId] = categoryDict;
        self.categoryIdToView[categoryId] = imgView;
        imgView.categoryId = categoryId;
        [self addSubview:imgView];
    }
}



- (void)setItem:(NSDictionary*)itemDict forCategory:(NSDictionary*)category isFirst:(BOOL)fFirst {
    NSString* categoryId = [QSEntityUtil getIdOrEmptyStr:category];
    [self setItem:itemDict forCategoryId:categoryId isFirst:fFirst];
}
- (void)setItem:(NSDictionary *)itemDict forCategoryId:(NSString *)categoryId isFirst:(BOOL)fFirst {
    QSCanvasImageView* imgView = self.categoryIdToView[categoryId];
    __weak QSCanvasImageView* weakImgView = imgView;
    [imgView.imgView setImageFromURL:[QSItemUtil getThumbnail:itemDict] beforeCompleteBlock:^(UIImage *img) {
        if (fFirst) {
            CGSize viewSize = weakImgView.bounds.size;
            CGRect bounds = weakImgView.bounds;
            viewSize = [QSRectUtil scaleSize:img.size toFitSize:viewSize];
            bounds.size = viewSize;
            weakImgView.bounds = bounds;
            
            weakImgView.frame = [QSRectUtil reducedFrame:weakImgView.frame forContainer:self.bounds];
        } else {
            CGSize imgSize = img.size;
            CGSize viewSize = weakImgView.bounds.size;
            CGFloat newHeigh = viewSize.width / imgSize.width * imgSize.height;
            viewSize.height = newHeigh;
            CGRect bounds = weakImgView.bounds;
            bounds.size = viewSize;
            weakImgView.bounds = bounds;
            
            weakImgView.frame = [QSRectUtil reducedFrame:weakImgView.frame forContainer:self.bounds];
        }
    }];
}

#pragma mark - Gesture
- (void)makeViewFocus:(QSCanvasImageView*)view {
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
        [self makeViewFocus:(QSCanvasImageView*)ges.view];
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

//- (void)didRotate:(UIRotationGestureRecognizer*)ges {
//    if (ges.state == UIGestureRecognizerStateBegan) {
//        self.preRotation = ges.rotation;
//    } else if (ges.state == UIGestureRecognizerStateChanged) {
//        float newRotation = ges.rotation;
//        self.currentFocusView.transform = CGAffineTransformRotate(self.currentFocusView.transform, newRotation - self.preRotation);
//        self.preRotation = newRotation;
//    } else  {
//        self.preRotation = 0;
//    }
//}

- (void)didPinch:(UIPinchGestureRecognizer*)ges {
    if (ges.state == UIGestureRecognizerStateBegan) {
        self.prePinchScale = ges.scale;
    } else if (ges.state == UIGestureRecognizerStateChanged) {
        float newScale = ges.scale;
        CGRect preBound = self.currentFocusView.bounds;
        self.currentFocusView.bounds = CGRectMake(0, 0, preBound.size.width * newScale / self.prePinchScale, preBound.size.height * newScale / self.prePinchScale);
        if (!CGRectContainsRect(self.bounds, self.currentFocusView.frame)) {
            //不移出画布
            self.currentFocusView.bounds = preBound;
        } else {
            self.prePinchScale = newScale;
        }
        [self.currentFocusView hideRemoveBtn];

    } else  {
        self.prePinchScale = 1.f;
    }
    
}

- (void)didPan:(UIPanGestureRecognizer*)ges {
    CGPoint p = [ges translationInView:self];
    if (ges.state == UIGestureRecognizerStateBegan) {
        self.prePanTranslation = p;
        self.initPanTranslation = p;
    } else if (ges.state == UIGestureRecognizerStateChanged) {
        //x
        CGPoint c = self.currentFocusView.center;
        self.currentFocusView.center = CGPointMake(self.currentFocusView.center.x + p.x - self.prePanTranslation.x, self.currentFocusView.center.y);
        if (!CGRectContainsRect(self.bounds, self.currentFocusView.frame)) {
            //不移出画布
            self.currentFocusView.center = c;
        } else {
            CGPoint prePoint = self.prePanTranslation;
            prePoint.x = p.x;
            self.prePanTranslation = prePoint;
        }
        
        //y
        c = self.currentFocusView.center;
        self.currentFocusView.center = CGPointMake(self.currentFocusView.center.x, self.currentFocusView.center.y + p.y - self.prePanTranslation.y);
        if (!CGRectContainsRect(self.bounds, self.currentFocusView.frame)) {
            //不移出画布
            self.currentFocusView.center = c;
        } else {
            CGPoint prePoint = self.prePanTranslation;
            prePoint.y = p.y;
            self.prePanTranslation = prePoint;
        }
        

        //Remove btn
        if (ABS(self.prePanTranslation.x - self.initPanTranslation.x) >= 20 ||
            ABS(self.prePanTranslation.y - self.initPanTranslation.y) >= 20) {
            [self.currentFocusView hideRemoveBtn];
        }
        
    } else {
        self.prePanTranslation = CGPointZero;
        self.initPanTranslation = CGPointZero;
    }
}





- (void)updateHighlightView:(UIView*)highlightView {
    NSArray* viewArray = [self.categoryIdToView allValues];
    for (QSCanvasImageView* imgView in viewArray) {
        imgView.hover = imgView == highlightView;
        if (imgView == highlightView) {
            [imgView updateHoverColor];
        }
    }
}


- (BOOL)checkLoadAtLeastOneImage {
    NSArray* subviews = self.subviews;
    for (QSCanvasImageView* imgView in subviews) {
        if (![imgView isKindOfClass:[QSCanvasImageView class]]) {
            continue;
        }
        if (imgView.imgView.image) {
            return YES;
        }
    }
    return NO;
}


- (BOOL)checkRate:(float)rate {
    NSArray* subviews = self.subviews;
    subviews = [subviews filteredArrayUsingBlock:^BOOL(QSCanvasImageView* imgView) {
        if (![imgView isKindOfClass:[QSCanvasImageView class]]) {
            return NO;
        }
        if (!imgView.imgView.image) {
            return NO;
        }
        return YES;
    }];
    
    while (subviews.count > 1) {
        UIView* firstView = [subviews firstObject];
        subviews = [subviews subarrayWithRange:NSMakeRange(1, subviews.count - 1)];
        float f = [QSRectUtil getViewUncoveredSquare:firstView otherViews:subviews];
        float square = [QSRectUtil getSquare:firstView.frame];
        if (f / square < rate) {
            return NO;
        }
    }
    
    return YES;
}
- (UIImage*)submitView {
    [self updateHighlightView:nil];
    
    //放大一定倍数后再截图以提高图片清晰度
    float rate = 2.f;
    CGRect bounds = self.bounds;
    bounds.size.width *= rate;
    bounds.size.height *= rate;
    UIView* newView = [[UIView alloc] initWithFrame:bounds];
    newView.backgroundColor = self.backgroundColor;
    for (id v in self.subviews) {
        if ([v isKindOfClass:[QSCanvasImageView class]]) {
            QSCanvasImageView* imgView = v;
            CGRect frame = imgView.frame;
            frame.origin.x *= rate;
            frame.origin.y *= rate;
            frame.size.width *= rate;
            frame.size.height *= rate;
            UIImageView* newImageView = [[UIImageView alloc] initWithFrame:frame];
            newImageView.image = imgView.imgView.image;
            [newView addSubview:newImageView];
        }
    }
    return [newView makeScreenShot];
}


#pragma mark -
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    return YES;
}

#pragma mark - QSCanvasImageViewDelegate
- (void)canvasImageViewDidClickRemoveBtn:(QSCanvasImageView*)view {
    self.imgViewToBeRemoved = view;
    UIAlertView* alertView = [[UIAlertView alloc] initWithTitle:@"需要删除搭配单品？" message:@"" delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确定", nil];
    [alertView show];
}

#pragma mark - UIAlertViewDelegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == alertView.cancelButtonIndex) {
        self.imgViewToBeRemoved = nil;
    } else {
        QSCanvasImageView* view = self.imgViewToBeRemoved;
        NSString* categoryId = view.categoryId;
        [view removeFromSuperview];
        NSDictionary* categoryDict = self.categoryIdToEntity[categoryId];
        [self.categoryIdToEntity removeObjectForKey:categoryId];
        [self.categoryIdToView removeObjectForKey:categoryId];
        if ([self.delegate respondsToSelector:@selector(canvasView:didRemoveCategory:)]) {
            [self.delegate canvasView:self didRemoveCategory:categoryDict];
        }
    }
}
@end
