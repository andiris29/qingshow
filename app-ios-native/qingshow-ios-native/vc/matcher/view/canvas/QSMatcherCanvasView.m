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
    
    for (int i = 0; i < newCategoryArray.count; i++) {
        float sizeHeight = self.frame.size.height / 2;
        float sizeWidth = sizeHeight / 16 * 9;
        float width = self.frame.size.width / 3;
        float height = self.frame.size.height  / ((newCategoryArray.count + 2)/ 3);
        UIImageView* imgView = [[QSCanvasImageView alloc] initWithFrame:CGRectMake(width * (i % 3), height * (i / 3), sizeWidth, sizeHeight)];
        imgView.userInteractionEnabled = YES;
        UILongPressGestureRecognizer* ges = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(didTapEntityView:)];
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
    [imgView setImageFromURL:[QSItemUtil getThumbnail:itemDict] beforeCompleteBlock:^(UIImage *img) {
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
    [self updateHighlightView:nil];
    if ([self.delegate respondsToSelector:@selector(canvasView:didTapCategory:)]) {
        [self.delegate canvasView:self didTapCategory:nil];
    }
}
- (void)didTapEntityView:(UIGestureRecognizer*)ges {
    if (ges.state == UIGestureRecognizerStateBegan) {
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


#pragma mark -
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    if ([gestureRecognizer isKindOfClass:[UILongPressGestureRecognizer class]]) {
        return NO;
    }
    return YES;
}

@end
