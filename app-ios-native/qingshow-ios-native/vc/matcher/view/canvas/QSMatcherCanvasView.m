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
#import "NSDictionary+QSExtension.h"


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

}
- (void)rearrangeWithMatcherConfig:(NSDictionary*)matcherConfig
                           modelId:(NSString*)modelId {
    QSCanvasImageView* modelView = self.categoryIdToView[modelId];
    if (![modelView isEqual:[NSNull null]]) {
        NSArray* modelConfig = [matcherConfig arrayValueForKeyPath:@"master.rect"];
        [self _updateView:modelView withRectConfig:modelConfig];

    }
    
    NSArray* itemConfigs = [matcherConfig arrayValueForKeyPath:@"slaves"];
    for (NSDictionary* itemConfig in itemConfigs) {
        NSArray* rectConfig = [itemConfig arrayValueForKeyPath:@"rect"];
        NSString* categoryId = [itemConfig stringValueForKeyPath:@"categoryRef"];
        QSCanvasImageView* v = self.categoryIdToView[categoryId];
        if (![v isEqual:[NSNull null]]) {
            [self _updateView:v withRectConfig:rectConfig];
        }
    }
}
- (void)_updateView:(QSCanvasImageView*)view withRectConfig:(NSArray*)rectConfig {
    if (!rectConfig || rectConfig.count != 4) {
        return;
    }
    float x = self.frame.size.width * ((NSNumber*)rectConfig[0]).intValue / 100;
    float y = self.frame.size.height * ((NSNumber*)rectConfig[1]).intValue / 100;
    float sizeWidth = self.frame.size.width * ((NSNumber*)rectConfig[2]).intValue / 100;
    float sizeHeight = self.frame.size.height * ((NSNumber*)rectConfig[3]).intValue / 100;
    view.frame = CGRectMake(x, y, sizeWidth, sizeHeight);
    
    if (view.imgView.image) {
        UIImage* img = view.imgView.image;
        CGSize viewSize = view.bounds.size;
        CGRect bounds = view.bounds;
        viewSize = [QSRectUtil scaleSize:img.size toFitSize:viewSize];
        bounds.size = viewSize;
        view.bounds = bounds;
        view.frame = [QSRectUtil reducedFrame:view.frame forContainer:self.bounds];
    }
    
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
    
    [self _addCategories:newCategoryArray];
}

- (void)_addCategories:(NSArray*)newCategoryArray {
    for (NSDictionary* categoryDict in newCategoryArray) {
        NSString* categoryId = [QSEntityUtil getIdOrEmptyStr:categoryDict];
        
        int xPercent = [QSRandomUtil randomRangeFrom:0 to:70];
        int yPercent = [QSRandomUtil randomRangeFrom:0 to:70];
        int widthPercent = [QSRandomUtil randomRangeFrom:30 to:100 - xPercent];
        int heightPercent = [QSRandomUtil randomRangeFrom:30 to:100 - yPercent];
        
        float sizeHeight = self.frame.size.height * heightPercent / 100;
        float sizeWidth = self.frame.size.width * widthPercent / 100;
        float y = self.frame.size.height * xPercent / 100;
        float x = self.frame.size.width * yPercent / 100;
        
        QSCanvasImageView* imgView = [[QSCanvasImageView alloc] initWithFrame:CGRectMake(x, y, sizeWidth, sizeHeight)];
        imgView.userInteractionEnabled = YES;
        UILongPressGestureRecognizer* ges = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(didTapEntityView:)];
        ges.minimumPressDuration = 0.f;
        [imgView addGestureRecognizer:ges];
        
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
        CGRect newBound = CGRectMake(0, 0, preBound.size.width * newScale / self.prePinchScale, preBound.size.height * newScale / self.prePinchScale);

        if (newBound.size.width > 10 && newBound.size.height > 10) {
            //防止被缩太小
            self.currentFocusView.bounds = newBound;
        }

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

- (BOOL)checkLoadAllImages {
    NSArray* subviews = self.subviews;
    for (QSCanvasImageView* imgView in subviews) {
        if (![imgView isKindOfClass:[QSCanvasImageView class]]) {
            continue;
        }
        if (!imgView.imgView.image) {
            return NO;
        }
    }
    return YES;
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
- (UIImage*)submitViewItems:(NSArray*)itemArray rects:(NSMutableArray*)rectArray {
    [self updateHighlightView:nil];
    
    CGRect bounds = self.bounds;
    CGFloat width = bounds.size.width;
    CGFloat height = bounds.size.height;
    //计算rect
    for (NSDictionary* itemDict in itemArray) {
        NSString* categoryId = [QSItemUtil getCategoryStr:itemDict];
        UIView* itemImgView = self.categoryIdToView[categoryId];
        if (![QSEntityUtil checkIsNil:itemImgView]) {
            [rectArray addObject:
  @[@(itemImgView.frame.origin.x * 100 / width),
    @(itemImgView.frame.origin.y * 100 / height),
    @(itemImgView.frame.size.width * 100 / width),
    @(itemImgView.frame.size.height * 100 / height)]
             ];
        } else {
            [rectArray addObject:@[]];
        }
    }
    
    
    //放大一定倍数后再截图以提高图片清晰度
    float rate = 2.f;

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
    [self _transformForNewView:newView];
    return [newView makeScreenShot];
}

- (void)_transformForNewView:(UIView*)newView {
    CGFloat left = newView.bounds.size.width,
    right = 0.f,
    top = newView.bounds.size.height,
    bottom = 0.f;
    
    for (UIView* v in newView.subviews) {
        left = left < v.frame.origin.x ? left : v.frame.origin.x;
        right = right > v.frame.origin.x + v.frame.size.width ? right : v.frame.origin.x + v.frame.size.width;
        top = top < v.frame.origin.y ? top : v.frame.origin.y;
        bottom = bottom > v.frame.origin.y + v.frame.size.height ? bottom : v.frame.origin.y + v.frame.size.height;
    }
    left = left > 0 ? left : 0;
    right = right < newView.bounds.size.width ? right : newView.bounds.size.width;
    top = top > 0 ? top : 0;
    bottom = bottom < newView.bounds.size.height ? bottom : newView.bounds.size.height;
    CGFloat width = right - left;
    CGFloat height = bottom - top;
    if (width / newView.bounds.size.width >= 0.6 ||
        height / newView.bounds.size.height >= 0.5) {
        return;
    }
    
    CGFloat scaleXrate = newView.bounds.size.width / width;
    CGFloat scaleYrate = newView.bounds.size.height * 0.8 / height;
    
    CGFloat scaleRate = scaleXrate < scaleYrate ? scaleXrate : scaleYrate;
    
    CGFloat oldCenterX = (left + right) / 2;
    CGFloat oldCenterY = (top + bottom) / 2;
    CGFloat newCenterX = oldCenterX * scaleRate;
    CGFloat newCenterY = oldCenterY * scaleRate;
    CGFloat deltaX = newView.bounds.size.width / 2 - newCenterX;
    CGFloat deltaY = newView.bounds.size.height / 2 - newCenterY;
    
    for (UIView* v in newView.subviews) {
        CGRect frame = v.frame;
        
        frame.origin.x = frame.origin.x * scaleRate + deltaX;
        frame.origin.y = frame.origin.y * scaleRate + deltaY;
        frame.size.width *= scaleRate;
        frame.size.height *= scaleRate;
        
        v.frame = frame;
    }
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
