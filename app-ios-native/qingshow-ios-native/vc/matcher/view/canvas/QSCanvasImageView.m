//
//  QSCanvasImageView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCanvasImageView.h"

@interface QSCanvasImageView ()
@property (assign, nonatomic) float prePinchScale;
@property (assign, nonatomic) float preRotation;
@property (assign, nonatomic) CGPoint prePanTranslation;
@end

@implementation QSCanvasImageView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.userInteractionEnabled = YES;
        self.contentMode = UIViewContentModeScaleAspectFill;
        self.clipsToBounds = YES;
        
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
    return self;
}

- (void)didRotate:(UIRotationGestureRecognizer*)ges {
    if (ges.state == UIGestureRecognizerStateBegan) {
        self.preRotation = ges.rotation;
    } else if (ges.state == UIGestureRecognizerStateChanged) {
        float newRotation = ges.rotation;
        self.transform = CGAffineTransformRotate(self.transform, newRotation - self.preRotation);
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
        self.transform = CGAffineTransformScale(self.transform, newScale / self.prePinchScale, newScale / self.prePinchScale);
        self.prePinchScale = newScale;
    } else  {
        self.prePinchScale = 1.f;
    }

}

- (void)didPan:(UIPanGestureRecognizer*)ges {
    CGPoint p = [ges translationInView:self];
    if (ges.state == UIGestureRecognizerStateBegan) {
        self.prePanTranslation = p;
    } else if (ges.state == UIGestureRecognizerStateChanged) {
        CGAffineTransform tran = self.transform;
        self.transform = CGAffineTransformTranslate(self.transform, p.x - self.prePanTranslation.x, p.y - self.prePanTranslation.y);
        
        if (self.superview && !CGRectContainsRect(self.superview.frame, self.frame)) {
            //不移出画布
            self.transform = tran;
        } else {
            self.prePanTranslation = p;
        }

    } else {
        self.prePanTranslation = CGPointZero;
    }
}

#pragma mark -
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    return YES;
}

@end
