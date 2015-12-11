//
//  QSMatcherCanvasView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>


@class QSMatcherCanvasView;

@protocol QSMatcherCanvasViewDelegate <NSObject>

- (void)canvasView:(QSMatcherCanvasView*)view didTapCategory:(NSDictionary*)categoryDict;
- (void)canvasView:(QSMatcherCanvasView*)view didRemoveCategory:(NSDictionary*)categoryDict;

@end

@interface QSMatcherCanvasView : UIView <UIGestureRecognizerDelegate, UIAlertViewDelegate>

+ (instancetype)generateView;
- (void)bindWithCategory:(NSArray*)categoryArray;
- (void)rearrangeWithMatcherConfig:(NSDictionary*)matcherConfig
                           modelId:(NSString*)modelId;
- (void)setItem:(NSDictionary*)itemDict forCategory:(NSDictionary*)category isFirst:(BOOL)fFirst;
- (void)setItem:(NSDictionary *)itemDict forCategoryId:(NSString *)categoryId isFirst:(BOOL)fFirst;

- (UIImage*)submitViewItems:(NSArray*)itemArray rects:(NSMutableArray*)rectArray;
- (BOOL)checkRate:(float)rate;
- (BOOL)checkLoadAllImages;

@property (weak, nonatomic) NSObject<QSMatcherCanvasViewDelegate>* delegate;

@end
